package ch.heig.amt.vineward.business.repository;

import ch.heig.amt.vineward.business.model.user.UserToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * User token repository.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    @Query("select t from UserToken t where t.token = :token and t.expired = false and t.revoked = false")
    Optional<UserToken> findByToken(String token);
}
