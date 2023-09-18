package io.cwiekala.aggregates.domain.bid;

import io.cwiekala.aggregates.utils.AggregateRoot;
import org.javamoney.moneta.Money;

@AggregateRoot
class BidPublisher {

    Money maximumAmount;

    BidWasPlacedOLD handle(PlaceBid command) {
        return BidWasPlacedOLD.builder().build();
    }

    // maxiumum amount
    // auctionerId
    // auctioner can bid over his own maximum, but the price shouldnt be changed
}
