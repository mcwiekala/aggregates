package io.cwiekala.agregates.model;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Message implements Serializable {

    private String content;
    private String author;
    private String date;

}
