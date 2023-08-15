package io.cwiekala.agregates.repository;

import io.cwiekala.agregates.model.Auction;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, UUID> {

}
