package io.cwiekala.agregates.application;

import io.cwiekala.agregates.domain.bid.Bid;
import io.cwiekala.agregates.domain.bid.BidWasPlaced;
import io.cwiekala.agregates.domain.bid.PlaceBid;
import io.cwiekala.agregates.utils.ApplicationService;
import org.javamoney.moneta.Money;

@ApplicationService
class BidFacade { // placingBid?

//    BidRepository bidRepository; // TODO: fix repo


    // ALBO EVENT ALBO COMMAND, agregat nie procesuje komend!
    void handle2(PlaceBid command) {
        // process command and do logic ???
        Bid bid = new Bid(command.getAmount());
        bid.placeBid(Money.zero(Bid.USD));
    }

    void handle(BidWasPlaced event) {
        // handle event from different aggregate and do logic in Bid
//        new Bid();
    }



}
