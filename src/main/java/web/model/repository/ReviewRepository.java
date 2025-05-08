package web.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.model.entity.ReviewEntity;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity,Integer> {

    //사용자별 리뷰 조회
    List<ReviewEntity> findByUserId(String userId);  // String 타입 유지
    Page<ReviewEntity> findByUserId(String userId, Pageable pageable);  // String 타입 유지

    //제품별 리뷰 조회
    List<ReviewEntity> findByProductId(Integer productId);
    Page<ReviewEntity> findByProductId(Integer productId, Pageable pageable);

    //별점별 리뷰 조회
    List<ReviewEntity> findByRating(Integer rating);
    Page<ReviewEntity> findByRating(Integer rating, Pageable pageable);

    //특정 키워드 포함된 리뷰 조회
    List<ReviewEntity> findByRcontentContaining(String keyword);
    Page<ReviewEntity> findByRcontentContaining(String keyword, Pageable pageable);

    //사용자별 제품별 리뷰 조회
    List<ReviewEntity> findByUserIdAndProductId(String userId, Integer productId);  // String 타입 유지

    //포인트 지급 여부별 리뷰 조회
    List<ReviewEntity> findByRewarded(Boolean rewarded);

    //제품별 평균 별점 계산
    long countByProductId(Integer productId);
}