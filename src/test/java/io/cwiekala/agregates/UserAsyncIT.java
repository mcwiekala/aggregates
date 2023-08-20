package io.cwiekala.agregates;

import static io.cwiekala.agregates.model.Currency.EURO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
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
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
class UserAsyncIT {

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
    @SneakyThrows
    void checkLockWhenUsersCompeteAsync() throws Exception {
        // given
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

//        boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
//        System.out.println(actualTransactionActive);

        // when - 10 same asynchronous calls
        final ExecutorService executor = Executors.newFixedThreadPool(10);

        users.parallelStream().forEach(user ->
            executor.execute(() -> userService.placeBid(user.getId(), auction.getId(), BigDecimal.valueOf(200L), EURO)));
        executor.awaitTermination(10, TimeUnit.SECONDS);

        /**
         then:

         caused by:
         - ObjectOptimisticLockingFailureException
         - DataIntegrityViolationException
         - ConstraintViolationException
         - JdbcSQLIntegrityConstraintViolationException
         - StaleObjectStateException

         Lock in: Auction table - because many users placing bids to this table!
         */
        await()
            .atMost(Duration.TEN_SECONDS.multiply(2))
            .untilAsserted(() -> {
                Auction auction1 = auctionRepository.getReferenceById(auction.getId());
                List<Bid> all = bidRepository.findByAuctionId(auction1.getId());
                assertThat(all.size()).isEqualTo(10);
            });
    }

    @Test
    void checkLockOnUnrelevantOperationsAsync() throws Exception {
        // given
        User user = new User("User1", new Address("London"));
        userRepository.saveAndFlush(user);

        Auction auction = Auction.builder()
            .title("Domain-Driven Design: Tackling Complexity in the Heart of Software").build();

        auctionRepository.saveAndFlush(auction);

        // when - 2 different asynchronous calls
        // placing bid is totally different operation than changing Address!
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.execute(() -> userService.placeBid(user.getId(), auction.getId(), BigDecimal.valueOf(200L), EURO));
        executor.execute(() -> userService.changeUserAddress(user.getId(), new Address("Warsaw")));
        executor.awaitTermination(10, TimeUnit.SECONDS);
        /**
         then:

         caused by:
         - ObjectOptimisticLockingFailureException
         - DataIntegrityViolationException
         - ConstraintViolationException
         - JdbcSQLIntegrityConstraintViolationException
         - StaleObjectStateException

         Lock in: User table - because User table was changed 2 times!
         - foreign ID to new bid was added
         - message about changed Address was added
         */
        await()
            .atMost(Duration.TEN_SECONDS)
            .untilAsserted(() -> {
                User userResult = userRepository.findById(user.getId()).orElseThrow();
                Auction auctionResult = auctionRepository.findById(auction.getId()).orElseThrow();

                List<Bid> allAuctionBids = bidRepository.findByAuctionId(auctionResult.getId());
                Optional<Bid> possibleBid = allAuctionBids.stream()
                    .filter(bid -> bid.getUser().getId().equals(userResult.getId()))
                    .findFirst();

                possibleBid.ifPresent(bid -> assertThat(bid.getAmount()).hasToString("200.00"));
                assertThat(possibleBid).isPresent();
                assertThat(userResult.getAddress().getCity()).isEqualTo("Warsaw");
            });
    }


    @BeforeEach
    void clean() {
        addressRepository.deleteAll();
        userRepository.deleteAll();
        auctionRepository.deleteAll();
        bidRepository.deleteAll();
    }
}
