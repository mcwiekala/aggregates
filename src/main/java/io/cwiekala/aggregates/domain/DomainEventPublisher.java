package io.cwiekala.aggregates.domain;

interface DomainEventPublisher {

    void publish(DomainEvent event);

}
