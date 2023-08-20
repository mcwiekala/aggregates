package io.cwiekala.agregates.services;

import io.cwiekala.agregates.model.Address;
import io.cwiekala.agregates.model.Auction;
import io.cwiekala.agregates.model.Bid;
import io.cwiekala.agregates.model.Currency;
import io.cwiekala.agregates.model.Favorite;
import io.cwiekala.agregates.model.User;
import io.cwiekala.agregates.repository.AuctionRepository;
import io.cwiekala.agregates.repository.UserRepository;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@AllArgsConstructor
@Slf4j
public class UserService {

    private AuctionRepository auctionRepository;
    private UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Bid placeBid(UUID userId, UUID auctionId, BigDecimal money, Currency currency) {
        boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();

        User user = userRepository.findById(userId).orElseThrow();
        Auction auction = auctionRepository.findById(auctionId).orElseThrow();
        log.info("START - placeBid User: {}; Auction version: {}; thread: {}", user.getName(), auction.getVersion(), Thread.currentThread().getName());
        log.info("START - placeBid Auction version: " + auction.getVersion());
        try {
            int randomTimeInMilis = ThreadLocalRandom.current().nextInt(0, 1000);
            Thread.sleep(randomTimeInMilis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bid bid = auction.placeBid(user, money, currency);
        auctionRepository.saveAndFlush(auction);
        userRepository.saveAndFlush(user); // many users to many auctions - no lock on user table
        log.info("STOP - placeBid User: {}; Auction version: {}; thread: {}", user.getName(), auction.getVersion(), Thread.currentThread().getName());
        return bid;
    }

    @Transactional
    public Bid placeBid(User user, Auction auction, BigDecimal money, Currency currency) {
        log.info("START - placeBid User version: " + user.getVersion());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bid bid = auction.placeBid(user, money, currency);
        auctionRepository.saveAndFlush(auction);
        userRepository.saveAndFlush(user); // many users to many auctions - no lock on user table
        log.info("END - placeBid User version: " + user.getVersion());
        return bid;
    }

    @Transactional
    public User changeUserAddress(UUID userId, Address address) {
        User user = userRepository.findById(userId).orElseThrow();
        log.info("START - changeUserAddress: " + user.getVersion());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        user.setAddress(address);
        userRepository.saveAndFlush(user);
        log.info("END - changeUserAddress: " + user.getVersion());
        return user;
    }

    @Transactional
    public User changeUserAddress(User user, Address address) {
        log.info("START - changeUserAddress: " + user.getVersion());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        user.setAddress(address);
        userRepository.saveAndFlush(user);
        log.info("END - changeUserAddress: " + user.getVersion());
        return user;
    }
}
