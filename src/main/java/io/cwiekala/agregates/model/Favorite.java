package io.cwiekala.agregates.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Favorite {

    @Id
    private UUID id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Version
    private Integer version;


}
