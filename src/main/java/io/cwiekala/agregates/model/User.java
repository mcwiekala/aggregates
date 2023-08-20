package io.cwiekala.agregates.model;

//import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.springframework.transaction.annotation.Transactional;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    private UUID id;

    private String name;

    @ToString.Exclude
    @JoinColumn(name = "address_id")
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

//        @Type(type = "io.cwiekala.agregates.model.Message")
//    @Type(JsonType.class)
//    @Column(columnDefinition = "jsonb")
//    private List<Message> messages = new ArrayList<>();

    private String updated;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // TODO: kaskady!
    @JoinTable(
        name = "_link",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "auction_id"))
    private List<Auction> auctionsInvolved = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id")
    private List<Favorite> favorites = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL) // TODO: kaskady!
    private List<Bid> bids;

    @Version
    private Integer version;

    @Builder
    public User(String name, Address address) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
        this.auctionsInvolved = new ArrayList<>();
    }

//    @Transactional
//    public Bid placeBid(Auction auction, BigDecimal money, Currency currency) {
//        try {
//            Thread.sleep(2000);
//            Bid bid = auction.placeBid(this, money, currency);
//            return bid;
//        } catch (Exception e) {
//            throw new RuntimeException("Bid not placed!");
//        }
//    }

    List<Auction> addAuction(Auction auction) {
        auctionsInvolved.add(auction);
        return auctionsInvolved;
    }

//    List<Message> addMessage(String content, String role) {
//        messages.add(new Message(content, role, Instant.now().toString()));
//        return messages;
//    }

    public List<Favorite> addToFavorites(Favorite auction) {
        favorites.add(auction);
        return favorites;
    }
}
