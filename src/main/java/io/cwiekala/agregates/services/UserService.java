package io.cwiekala.agregates.services;

import static io.cwiekala.agregates.model.Currency.EURO;

import io.cwiekala.agregates.model.Auction;
import io.cwiekala.agregates.model.Bid;
import io.cwiekala.agregates.model.Currency;
import io.cwiekala.agregates.model.User;
import io.cwiekala.agregates.repository.AuctionRepository;
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

//    void run(){
//        user1.placeBid(auction, BigDecimal.valueOf(100L), EURO)
//    }

    @Transactional
    public Bid placeBid(User user,Auction auction, BigDecimal money, Currency currency){

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Bid not placed!");
        }

        Bid bid = auction.placeBid(user, money);
        auctionRepository.save(auction);
        return bid;
    }

}
