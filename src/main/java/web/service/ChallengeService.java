package web.service;

import lombok.RequiredArgsConstructor;
import org.aspectj.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.model.dto.ChallengeDto;
import web.model.entity.UserEntity;
import web.model.repository.ChallengeEntityRepository;
import web.model.repository.UserEntityRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드의 생성자 자동생성
@Transactional
public class ChallengeService {

    private ChallengeEntityRepository challengeEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private FileUtil fileUtil;


    //1.챌린지 목록 조회



    //2.특정 챌린지 상세조회



    //3.챌린지 참여기능




    //4.사용자 챌린지 목록 조회



    //5.챌린지 목록등록 (관리자 전용)
    public boolean registerChallenge(ChallengeDto challengeDto ,int id){
        //1.현재 회원번호의 엔티티 찾기(연관관계)FK, Optional : null 값 제어 기능 제공
        Optional<UserEntity>optionalUserEntity = userEntityRepository.findById(id);
        if(optionalUserEntity.isEmpty())return false; // 만약 조회된 회원엔티티가 없으면 false
        //2.현재  번호의 엔티티 찾기 ( 연관관계 ) FK

    }





//
//
//    // 관리자 권한 확인
//    public boolean checkAdminRole(int userId) {
//        // 사용자 엔티티 조회
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//
//        // 관리자 역할 확인 (role 필드가 있다고 가정)
//        return "ADMIN".equals(user.getRole());
//    }

}
