
package ch.heig.amt.vineward.business.repository;

import ch.heig.amt.vineward.business.model.review.Review;
import ch.heig.amt.vineward.business.model.review.ReviewStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Review repository.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByWineId(Long wineId);

    List<Review> findByWineIdAndStatus(Long wineId, ReviewStatus status);

    List<Review> findAllByStatus(ReviewStatus status);

    long countByStatus(ReviewStatus status);
}
