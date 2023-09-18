package io.cwiekala.aggregates.domain.bid;

import io.cwiekala.aggregates.utils.comments.AggregateRoot;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.javamoney.moneta.Money;

@AggregateRoot
@Value
@AllArgsConstructor
public class Bid {
    // TODO: Either? ^^ - wtedy publisher w application!

    Money amount;

//    Money maximumAmount;
    public static final CurrencyUnit USD = Monetary.getCurrency("USD");

//    public Either<BidPlacementFailure, BidWasPlaced>  handle(BidWasPlaced command) {
////        return BidWasPlaced.builder().build();
//        if(command != null){
//            return announceSuccess(BidWasPlaced.now(UUID.randomUUID()));
//        } else {
//            return announceFailure(BidPlacementFailure.now(UUID.randomUUID()));
//        }
//    }

//    public Either<BidPlacementFailure, BidWasPlaced> placeBid(Money money) {
////        return BidWasPlaced.builder().build();
//        if(money != null){
//            return announceSuccess(BidWasPlaced.now(AuctionId.generate(), null));
//        } else {
//            return announceFailure(BidPlacementFailure.now(UUID.randomUUID()));
//        }
//    }

    // maxiumum amount
    // auctionerId
    // auctioner can bid over his own maximum, but the price shouldnt be changed
}
