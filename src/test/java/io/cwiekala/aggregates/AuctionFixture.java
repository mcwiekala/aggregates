package io.cwiekala.aggregates;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.cwiekala.aggregates.application.AuctionFacade;
import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.commands.Result;
import io.cwiekala.aggregates.domain.auction.Auction;
import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.AuctionCreated;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.domain.auction.AuctionRepository;
import io.cwiekala.aggregates.infrastructure.InMemoryAuctionRepository;
import io.cwiekala.aggregates.infrastructure.InMemoryEventPublisher;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import lombok.ToString;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class AuctionFixture {

    public static final CurrencyUnit USD = Monetary.getCurrency("USD");
    public static final Money $5 = Money.of(5, USD);
    public static final Money $10 = Money.of(10, USD);
    public static final Money $80 = Money.of(80, USD);
    public static final Money $100 = Money.of(100, USD);
    public static final Money $120 = Money.of(120, USD);


}
