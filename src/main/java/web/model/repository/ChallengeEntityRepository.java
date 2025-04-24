package web.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.model.entity.ChallengeEntity;
import web.model.entity.UserEntity;

@Repository
public interface ChallengeEntityRepository extends JpaRepository<ChallengeEntity,Integer> {

}
