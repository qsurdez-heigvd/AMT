package ch.heig.amt.vineward.business.repository;

import ch.heig.amt.vineward.business.model.Wine;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

/**
 * Wine repository.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Repository
public interface WineRepository extends JpaRepository<Wine, Long> {

    @NonNull
    @Override
    @EntityGraph(attributePaths = {"varieties"})
    List<Wine> findAll();
}
