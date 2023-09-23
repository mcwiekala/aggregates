package io.cwiekala.aggregates;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.javamoney.moneta.Money;

class AuctionFixture {

    public static final CurrencyUnit USD = Monetary.getCurrency("USD");
    public static final Money $5 = Money.of(5, USD);
    public static final Money $10 = Money.of(10, USD);
    public static final Money $80 = Money.of(80, USD);
    public static final Money $100 = Money.of(100, USD);
    public static final Money $120 = Money.of(120, USD);


}
