package ch.heig.amt.vineward.business.repository;

import ch.heig.amt.vineward.business.model.user.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User repository.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
