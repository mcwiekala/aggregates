//package io.cwiekala.agregates.model;
//
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import jakarta.persistence.Id;
//import java.math.BigDecimal;
//import java.util.UUID;
//import lombok.Builder;
//import lombok.Data;
//
//@Data
//public class Money {
//    @Id
//    private UUID id;
//
//    private BigDecimal amount;
//
//    @Enumerated(EnumType.STRING)
//    private Currency currency;
//
//    Money(BigDecimal amount, Currency currency) {
//        this.id = UUID.randomUUID();
//        this.amount = amount;
//        this.currency = currency;
//    }
//
//    public static Money ofEuro(Long integer){
//        return new Money(BigDecimal.valueOf(integer), Currency.EURO);
//    }
//
//}
