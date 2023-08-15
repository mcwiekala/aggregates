package io.cwiekala.agregates.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    private UUID id;

    private String name;

    @JoinColumn(name = "address_id")
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @OneToMany
    @JoinColumn(name = "bid_id")
    private List<Auction> auctions;

    @Builder
    public User(String name, Address address) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
        this.auctions = new ArrayList<>();
    }

    @Transactional
    public Bid placeBid(Auction auction, BigDecimal money, Currency currency){

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Bid not placed!");
        }

        Bid bid = auction.placeBid(this, money);
        return bid;
    }

}
