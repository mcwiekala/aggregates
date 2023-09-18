package io.cwiekala.aggregates;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.cwiekala.aggregates.domain.auction.AuctionEvent.AuctionCreated;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.infrastructure.InMemoryEventPublisher;
import java.util.List;

interface EventsAbility {

    InMemoryEventPublisher eventPublisher = new InMemoryEventPublisher();

    default void assertThatBidEventsHappened() {
        List<Object> events = eventPublisher.getEvents();
        assertThat(events.size()).isEqualTo(3);
        EventChecker eventChecker = new EventChecker(events);
        eventChecker.assertEventAndPop(AuctionCreated.class);
        eventChecker.assertEventAndPop(BidWasPlaced.class);
        eventChecker.assertEventAndPop(BidWasPlaced.class);
    }

}
