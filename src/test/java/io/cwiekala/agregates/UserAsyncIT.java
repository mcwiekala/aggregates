package io.cwiekala.agregates;

import static io.cwiekala.agregates.model.Currency.EURO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.cwiekala.agregates.model.Address;
import io.cwiekala.agregates.model.Auction;
import io.cwiekala.agregates.model.User;
import io.cwiekala.agregates.repository.AddressRepository;
import io.cwiekala.agregates.repository.AuctionRepository;
import io.cwiekala.agregates.repository.BidRepository;
import io.cwiekala.agregates.repository.UserRepository;
import io.cwiekala.agregates.services.UserService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

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

        // when - 10 same asynchronous calls
        final ExecutorService executor = Executors.newFixedThreadPool(10);

        List<Future> tasks = new ArrayList<>();
        users.parallelStream().forEach(user ->
            tasks.add(executor.submit(
                () -> userService.placeBid(user.getId(), auction.getId(), BigDecimal.valueOf(200L), EURO))));
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Boolean exceptionWasThrown = false;
        for (Future future : tasks) {
            try {
                future.get();
            } catch (ExecutionException e) {
                ObjectOptimisticLockingFailureException exceptionCause = (ObjectOptimisticLockingFailureException) e.getCause();
                assertThat(exceptionCause.getCause().getClass()).isEqualTo(StaleObjectStateException.class);
                exceptionWasThrown = true;
                System.out.println("--- EXCEPTION WAS THROWN ---");
            }
        }
        /**
         then:
         Lock in Auction table - because many users placing bids to this table!
         */
        assertThat(exceptionWasThrown).isTrue();
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
        Future placeBidTask = executor.submit(() -> userService.placeBid(user.getId(), auction.getId(), BigDecimal.valueOf(200L), EURO));
        Future changeUserAddressTask = executor.submit(() -> userService.changeUserAddress(user.getId(), new Address("Warsaw")));
        executor.awaitTermination(10, TimeUnit.SECONDS);
        List<Future> tasks = List.of(placeBidTask, changeUserAddressTask);

        Boolean exceptionWasThrown = false;
        for (Future future : tasks) {
            try {
                future.get();
            } catch (ExecutionException e) {
                ObjectOptimisticLockingFailureException exceptionCause = (ObjectOptimisticLockingFailureException) e.getCause();
                assertThat(exceptionCause.getCause().getClass()).isEqualTo(StaleObjectStateException.class);
                exceptionWasThrown = true;
                System.out.println("--- EXCEPTION WAS THROWN ---");
            }
        }

        /**
         then:
         Lock in: User table - because User table was changed 2 times!
         - foreign ID to new bid was added
         - message about changed Address was added
         */
        assertThat(exceptionWasThrown).isTrue();
    }

    @BeforeEach
    void clean() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userRepository.deleteAll();
        addressRepository.deleteAll();
        auctionRepository.deleteAll();
        bidRepository.deleteAll();
    }
}
