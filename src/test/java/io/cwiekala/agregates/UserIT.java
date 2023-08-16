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

//    @Container
//    private static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer()
//        .withDatabaseName("foo")
//        .withUsername("foo")
//        .withPassword("secret");

//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
//        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
//    }
//
//    @Test
//    void testDocker() {
//        assertThat(postgresqlContainer.isRunning()).isEqualTo(true);
//    }

    @Test
    @SneakyThrows
    @Transactional
    void testSync() throws Exception {
        // given
        User user1 = new User("Tom", new Address("London"));
        User user2 = new User("Dan", new Address("Paris"));
        userRepository.save(user1);
        userRepository.save(user2);

        Auction auction = Auction.builder()
            .title("Domain-Driven Design: Tackling Complexity in the Heart of Software").build();

        auctionRepository.save(auction);

        // when - asynchronous calls
        userService.placeBid(user1, auction, BigDecimal.valueOf(200L), EURO);
        userService.placeBid(user2, auction, BigDecimal.valueOf(100L), EURO);

        // then
        List<Bid> all = bidRepository.findByAuctionId(auction.getId());
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    @SneakyThrows
    @Transactional
    void testAsync() throws Exception {
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

}
