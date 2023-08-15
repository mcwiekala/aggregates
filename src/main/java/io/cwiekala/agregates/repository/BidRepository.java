package io.cwiekala.agregates.repository;

import io.cwiekala.agregates.model.Bid;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, UUID> {

}
