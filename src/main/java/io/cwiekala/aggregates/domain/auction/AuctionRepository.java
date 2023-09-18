package io.cwiekala.aggregates.domain.auction;

import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import java.util.Optional;

public interface AuctionRepository {

    Optional<Auction> findById(AuctionId id);

    Auction save(Auction auction);

}
