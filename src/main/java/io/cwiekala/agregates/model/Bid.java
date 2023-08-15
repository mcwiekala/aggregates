package io.cwiekala.agregates.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Bid {
    @Id
    private UUID id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Builder
    public Bid(BigDecimal amount, User user, Auction auction) {
        this.id = UUID.randomUUID();
        this.amount = amount;
        this.user = user;
        this.auction = auction;
    }
}
