package io.cwiekala.aggregates.domain.bid;

import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.utils.comments.AggregateRoot;
import org.javamoney.moneta.Money;

@AggregateRoot
class BidPublisher {

    Money maximumAmount;

    BidWasPlaced handle(PlaceBid command) {
        return BidWasPlaced.builder().build();
    }

    // maxiumum amount
    // auctionerId
    // auctioner can bid over his own maximum, but the price shouldnt be changed
}
