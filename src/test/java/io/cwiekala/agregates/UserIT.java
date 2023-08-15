package io.cwiekala.agregates;

import static io.cwiekala.agregates.model.Currency.EURO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

import io.cwiekala.agregates.model.Address;
import io.cwiekala.agregates.model.Auction;
import io.cwiekala.agregates.model.User;
import io.cwiekala.agregates.repository.AddressRepository;
import io.cwiekala.agregates.repository.AuctionRepository;
import io.cwiekala.agregates.services.UserService;
import java.math.BigDecimal;
import java.util.List;
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
    UserService userService;

    @Test
    @SneakyThrows
    @Transactional
        // LazyInitializationException
    void test() throws Exception {
        // given
        User user1 = new User("Tom", new Address("London"));
        User user2 = new User("Dan", new Address("Paris"));

        Auction auction = Auction.builder()
            .title("Domain-Driven Design: Tackling Complexity in the Heart of Software").build();

        auctionRepository.save(auction);

        // when - asynchronous calls
        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread th, Throwable ex) {
                System.out.println("Uncaught exception: " + ex);
            }
        };

        Thread thread1 = new Thread(() -> userService.placeBid(user1, auction, BigDecimal.valueOf(200L), EURO));
        thread1.setUncaughtExceptionHandler(h);
        thread1.start();

        Thread thread2 = new Thread(() -> userService.placeBid(user1, auction, BigDecimal.valueOf(100L), EURO));
        thread2.setUncaughtExceptionHandler(h);
        thread2.start();

        Thread.sleep(3000);

        // then
        Auction auction1 = auctionRepository.getReferenceById(auction.getId());
        System.out.println("");
        assertThat(auction1.getBids().size()).isEqualTo(2);
//        await()
//            .atMost(Duration.ONE_MINUTE)
//            .untilAsserted(() -> {
//                Auction auction1 = auctionRepository.getReferenceById(auction.getId());
//                System.out.println("");
//                assertThat(auction1.getBids().size()).isEqualTo(2);
//            });
    }


}
