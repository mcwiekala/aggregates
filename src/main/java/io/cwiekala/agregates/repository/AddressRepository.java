package io.cwiekala.agregates.repository;

import io.cwiekala.agregates.model.Address;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, UUID> {

}
