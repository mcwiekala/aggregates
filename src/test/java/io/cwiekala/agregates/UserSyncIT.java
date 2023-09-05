package io.cwiekala.agregates;

import static io.cwiekala.agregates.model.Currency.EURO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.cwiekala.agregates.model.Address;
import io.cwiekala.agregates.model.Auction;
import io.cwiekala.agregates.model.Bid;
import io.cwiekala.agregates.model.User;
import io.cwiekala.agregates.repository.AddressRepository;
import io.cwiekala.agregates.repository.AuctionRepository;
import io.cwiekala.agregates.repository.BidRepository;
import io.cwiekala.agregates.repository.UserRepository;
import io.cwiekala.agregates.services.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@SpringBootTest
class UserSyncIT {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BidRepository bidRepository;

    @Autowired
    UserService userService;

    @Test
    void checkLockWhenUsersCompeteSync() {
        // given:
        List<User> users = List.of(new User("User1", new Address("London")),
            new User("User2", new Address("London")),
            new User("User3", new Address("London")),
            new User("User4", new Address("London")),
            new User("User5", new Address("London")),
            new User("User6", new Address("London")),
            new User("User7", new Address("London")),
            new User("User8", new Address("London")),
            new User("User9", new Address("London")),
            new User("User10", new Address("London")));

        users.forEach(user ->
            userRepository.save(user));

        Auction auction = Auction.builder()
            .title("Domain-Driven Design: Tackling Complexity in the Heart of Software").build();

        auctionRepository.save(auction);

        // when:
        users.forEach(user ->
            userService.placeBid(user.getId(), auction.getId(), BigDecimal.valueOf(200L), EURO));

        // then:
        Auction auction1 = auctionRepository.getReferenceById(auction.getId());
        List<Bid> all = bidRepository.findByAuctionId(auction1.getId());
        assertThat(all.size()).isEqualTo(10);
    }

    @Test
    void checkLockOnUnrelevantOperationsSync() throws Exception {
        // given:
        User user = new User("User1", new Address("London"));
        userRepository.save(user);

        Auction auction = Auction.builder()
            .title("Domain-Driven Design: Tackling Complexity in the Heart of Software").build();
        auctionRepository.save(auction);

        // when:
        userService.changeUserAddress(user, new Address("Warsaw"));
        ObjectOptimisticLockingFailureException exception = assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            userService.placeBid(user, auction, BigDecimal.valueOf(200L), EURO);
        });
        assertThat(exception.getCause().getClass()).isEqualTo(StaleObjectStateException.class);
        /**
            then: user lost auction because of exception

            The OptimisticLock exception occurs when a user waits until the auction is
            nearly complete and then attempts to place a bid. However, an unrelated update
            action has taken place on their account, leading to this issue.

            caused by:
            - ObjectOptimisticLockingFailureException
            - StaleObjectStateException
         */
    }

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        addressRepository.deleteAll();
        auctionRepository.deleteAll();
        bidRepository.deleteAll();
    }
}
