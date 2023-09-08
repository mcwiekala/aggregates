package io.cwiekala.agregates.domain;

interface DomainEventPublisher {

    void publish(DomainEvent event);

}
