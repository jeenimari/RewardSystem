package web.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.model.entity.UserChallengeEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChallnegeRepository extends JpaRepository<UserChallengeEntity, Integer> {


    //특정 사용자가 참여한 모든 챌린지 정보 조회
    List<UserChallengeEntity>findByUserId(int userId);


    //특정 사용자의 특정 챌린지 참여 정보 조회

    Optional<UserChallengeEntity>findByUserIdAndChallengeId(int userId,int challengeId);



    //특정 사용자의 특정 상태의 챌린지 참여 정보조회

    List<UserChallengeEntity>findByUserAndStatus(int userId,String status);

}
