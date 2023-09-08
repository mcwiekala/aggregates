package io.cwiekala.agregates.domain.auction;

import io.cwiekala.agregates.utils.ValueObject;
import org.javamoney.moneta.Money;

@ValueObject
class ShipmentOption {

    ShipmentType shipmentStrategy;
    Money shipmentCost;

}
