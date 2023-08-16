package io.cwiekala.agregates;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.cwiekala.agregates.model.Address;
import io.cwiekala.agregates.model.Auction;
import io.cwiekala.agregates.model.Bid;
import io.cwiekala.agregates.model.User;
import io.cwiekala.agregates.repository.AddressRepository;
import io.cwiekala.agregates.repository.AuctionRepository;
import io.cwiekala.agregates.repository.BidRepository;
import io.cwiekala.agregates.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AgregatesApplicationIT {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    BidRepository bidRepository;

    @Test
    void testApplicationContext(){
        // given
        Address address1 = Address.builder().id(UUID.randomUUID()).city("Warsaw").build();
        addressRepository.save(address1);

        Auction auction1 = Auction.builder().build();
        auctionRepository.save(auction1);

        User user = User.builder().build();
        userRepository.save(user);

        Bid bid = Bid.builder().build();
        bidRepository.save(bid);

        // when
        List<Address> addresses = addressRepository.findAll();
        List<User> users = userRepository.findAll();
        List<Auction> auctions = auctionRepository.findAll();
        List<Bid> bids = bidRepository.findAll();

        // then
        assertThat(addresses.size()).isEqualTo(1);
        assertThat(users.size()).isEqualTo(1);
        assertThat(auctions.size()).isEqualTo(1);
        assertThat(bids.size()).isEqualTo(1);
    }

//    @AfterEach
//    void clean(){
//        addressRepository.deleteAll();
//        userRepository.deleteAll();
//        auctionRepository.deleteAll();
//        bidRepository.deleteAll();
//    }

}
