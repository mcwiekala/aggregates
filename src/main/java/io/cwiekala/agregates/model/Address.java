package io.cwiekala.agregates.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Address")
public class Address {

    @Id
    private UUID id;
    private String flatNumber;
    private String homeNumber;
    private String street;
    private String city;
    private String postalCode;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn()
//    @JoinColumn(name = "user_id")
    private User user;

    @Version
    private Integer version;

    public Address(String flatNumber, String homeNumber, String street, String city, String postalCode) {
        this.id = UUID.randomUUID();
        this.flatNumber = flatNumber;
        this.homeNumber = homeNumber;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
    }

    public Address(String city) {
        this.id = UUID.randomUUID();
        this.city = city;
    }
}
