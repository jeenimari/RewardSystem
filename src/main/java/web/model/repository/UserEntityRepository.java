package web.model.repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.model.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity,Integer> {

    // [1] 추상메소드를 이용한 memail 조회
    Optional<UserEntity>findByEmail(String userId);

    //1.기본 crud 제공
    // (2) 개발자 정의한 쿼리메소드 : 메소드 명명 규칙
    // (3) 개발자 정의한 네이티브쿼리 : SQL 직접 작성



}
