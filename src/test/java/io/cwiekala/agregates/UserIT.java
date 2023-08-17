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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class UserIT {

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
    @Transactional
    void checkLockWhenUsersCompeteSync() throws Exception {
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

        // when
        users.forEach(user ->
            userService.placeBid(user, auction, BigDecimal.valueOf(200L), EURO));

        // then:
        Auction auction1 = auctionRepository.getReferenceById(auction.getId());
        List<Bid> all = bidRepository.findByAuctionId(auction1.getId());
        assertThat(all.size()).isEqualTo(10);
    }

    @Test
    @SneakyThrows
    @Transactional
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

        // when - asynchronous calls
        final ExecutorService executor = Executors.newFixedThreadPool(10);

        users.forEach(user ->
            executor.execute(() -> userService.placeBid(user, auction, BigDecimal.valueOf(200L), EURO)));
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

         /*
         then:
          - ObjectOptimisticLockingFailureException
          - DataIntegrityViolationException
          - ConstraintViolationException
          - JdbcSQLIntegrityConstraintViolationException
          - StaleObjectStateException
          */
        await()
            .atMost(Duration.ONE_MINUTE)
            .untilAsserted(() -> {
                Auction auction1 = auctionRepository.getReferenceById(auction.getId());
                List<Bid> all = bidRepository.findByAuctionId(auction1.getId());
                assertThat(all.size()).isEqualTo(10);
            });
    }

    @Test
    @SneakyThrows
    @Transactional
    void checkLockOnUnrelevantOperationsSync() throws Exception {
        // given
        User user1 = new User("User1", new Address("London"));
        userRepository.save(user1);

        Auction auction = Auction.builder()
            .title("Domain-Driven Design: Tackling Complexity in the Heart of Software").build();

        auctionRepository.save(auction);

        // when
        userService.placeBid(user1, auction, BigDecimal.valueOf(200L), EURO);
        userService.changeUserAddress(user1, new Address("Warsaw"));

        // then:
        User user = userRepository.findById(user1.getId()).get();
        Auction auction1 = auctionRepository.getReferenceById(auction.getId());
        List<Bid> allAuctionBids = bidRepository.findByAuctionId(auction1.getId());
        Optional<Bid> possibleBid = allAuctionBids.stream().filter(bid -> bid.getUser().getId() == user.getId())
            .findFirst();

        possibleBid.ifPresent(bid -> assertThat(bid.getAmount()).isEqualTo(BigDecimal.valueOf(200L)));
        assertThat(possibleBid).isPresent();
        assertThat(user.getAddress().getCity()).isEqualTo("Warsaw");
    }

    @Test
    @SneakyThrows
    @Transactional
    void checkLockOnUnrelevantOperationsAsync() throws Exception {
        // given
        User user1 = new User("User1", new Address("London"));
        userRepository.save(user1);

        Auction auction = Auction.builder()
            .title("Domain-Driven Design: Tackling Complexity in the Heart of Software").build();

        auctionRepository.save(auction);

        // when - asynchronous calls
        final ExecutorService executor = Executors.newFixedThreadPool(10);

        executor.execute(() -> userService.placeBid(user1, auction, BigDecimal.valueOf(200L), EURO));
        executor.execute(() -> userService.changeUserAddress(user1, new Address("Warsaw")));

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

         /*
         then:
          - ObjectOptimisticLockingFailureException
          - DataIntegrityViolationException
          - ConstraintViolationException
          - JdbcSQLIntegrityConstraintViolationException
          - StaleObjectStateException
          */
        await()
            .atMost(Duration.TEN_SECONDS)
            .untilAsserted(() -> {
                User user = userRepository.findById(user1.getId()).get();
                Auction auction1 = auctionRepository.getReferenceById(auction.getId());
                List<Bid> allAuctionBids = bidRepository.findByAuctionId(auction1.getId());
                Optional<Bid> possibleBid = allAuctionBids.stream().filter(bid -> bid.getUser().getId() == user.getId())
                    .findFirst();

                possibleBid.ifPresent(bid -> assertThat(bid.getAmount()).isEqualTo(BigDecimal.valueOf(200L)));
                assertThat(possibleBid).isPresent();
                assertThat(user.getAddress().getCity()).isEqualTo("Warsaw");
            });
    }
}
