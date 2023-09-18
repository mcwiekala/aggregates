package io.cwiekala.aggregates.application;

import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.domain.bid.PlaceBid;
import io.cwiekala.aggregates.utils.comments.ApplicationService;

@ApplicationService
class BidFacade { // placingBid?

//    BidRepository bidRepository; // TODO: fix repo


    // ALBO EVENT ALBO COMMAND, agregat nie procesuje komend!
    void handle2(PlaceBid command) {
        // process command and do logic ???
//        Bid bid = new Bid(command.getAmount());
//        bid.placeBid(Money.zero(Bid.USD));
    }

    void handle(BidWasPlaced event) {
        // handle event from different aggregate and do logic in Bid
//        new Bid();
    }



}
