package web.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.model.entity.UserChallengeEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallengeEntity, Integer> {

    // 사용자별 챌린지 참여 조회
    List<UserChallengeEntity> findByUserId(int userId);

    // 챌린지별 참여자 조회
    List<UserChallengeEntity> findByChallengeId(int challengeId);

    // 사용자의 특정 챌린지 참여 조회
    Optional<UserChallengeEntity> findByUserIdAndChallengeId(int userId, int challengeId);

    // 완료 상태별 조회
    List<UserChallengeEntity> findByCompleted(boolean completed);

    // 리워드 지급 상태별 조회
    List<UserChallengeEntity> findByRewarded(boolean rewarded);

    // 참여 상태별 조회
    List<UserChallengeEntity> findByStatus(UserChallengeEntity.ChallengeParticipationStatus status);

    // 사용자별 참여 상태 조회
    List<UserChallengeEntity> findByUserIdAndStatus(int userId, UserChallengeEntity.ChallengeParticipationStatus status);

}
