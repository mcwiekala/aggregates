package io.cwiekala.agregates.services;

import static io.cwiekala.agregates.model.Currency.EURO;

import io.cwiekala.agregates.model.Address;
import io.cwiekala.agregates.model.Auction;
import io.cwiekala.agregates.model.Bid;
import io.cwiekala.agregates.model.Currency;
import io.cwiekala.agregates.model.User;
import io.cwiekala.agregates.repository.AuctionRepository;
import io.cwiekala.agregates.repository.BidRepository;
import io.cwiekala.agregates.repository.UserRepository;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class UserService {

    private AuctionRepository auctionRepository;
    private UserRepository userRepository;

    @Transactional
    public Bid placeBid(User user, Auction auction, BigDecimal money, Currency currency) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bid bid = auction.placeBid(user, money, currency);
        auctionRepository.save(auction);
        return bid;
    }

    @Transactional
    public User changeUserAddress(User user, Address address) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        user.setAddress(address);
        userRepository.save(user);
        return user;
    }

}
