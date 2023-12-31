package io.cwiekala.agregates.repository;

import io.cwiekala.agregates.model.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

}
