package io.cwiekala.aggregates.domain.auction;

import io.cwiekala.aggregates.utils.ValueObject;
import org.javamoney.moneta.Money;

@ValueObject
class ShipmentOption {

    ShipmentType shipmentStrategy;
    Money shipmentCost;

}
