package io.cwiekala.aggregates.infrastructure;

import io.cwiekala.aggregates.domain.auction.Auction;
import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.cwiekala.aggregates.domain.auction.AuctionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryAuctionRepository implements AuctionRepository {

    List<Auction> list = new ArrayList<>();

    public Optional<Auction> findById(AuctionId id) {
        return list.stream()
            .filter(auction -> auction.getId().equals(id))
            .findFirst();
    }

    public Auction save(Auction auction) {
        list.add(auction);
        return auction;
    }

}
