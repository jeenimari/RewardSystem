package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.model.dto.ChallengeDto;
import web.model.dto.UserDto;
import web.model.entity.ChallengeEntity;
import web.model.entity.UserChallengeEntity;
import web.model.entity.UserEntity;
import web.model.repository.ChallengeEntityRepository;
import web.model.repository.UserChallnegeRepository;
import web.model.repository.UserEntityRepository;
import web.util.JwtUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // final 필드의 생성자 자동생성
@Transactional
public class ChallengeService {

    private final ChallengeEntityRepository challengeEntityRepository; //챌린지 정보 crud
    private final UserEntityRepository userEntityRepository; //사용자 정보 crud
    private final UserChallnegeRepository userChallnegeRepository; //사용자-챌린지 연결 정보 crud
    private final JwtUtil jwtUtil;  //jwt 토큰처리 유틸리티


    //1.챌린지 목록 조회
    public List<ChallengeDto>getCheck(){
        List<ChallengeEntity>challengeEntities = challengeEntityRepository.findAll();
        return challengeEntities.stream().map(ChallengeDto::fromEntity).collect(Collectors.toList());

    }



    //2.특정 챌린지 상세조회
    public ChallengeDto detailChallenge(int cno){
        Optional<ChallengeEntity>optionalChallengeEntity = challengeEntityRepository.findById(cno);
        if(optionalChallengeEntity.isEmpty()){
            return null;
        }
        return ChallengeDto.fromEntity(optionalChallengeEntity.get());
    }



    //3.챌린지 참여기능   //오류가 날수 있음 dto와 엔티티 부분을 다시 볼것
    public boolean participateChallenge(int cno, int userId){
        //1.챌린지 존재 여부 확인
        Optional<ChallengeEntity>optionalChallenge = challengeEntityRepository.findById(cno);
        if(optionalChallenge.isEmpty()){
            return false;
        }
        //2.사용자 존재 여부 확인
        Optional<UserEntity>optionalUser = userEntityRepository.findById(userId);
        if(optionalUser.isEmpty()){
            return false;
        }

        //3.이미 참여 중인지 확인
        Optional<UserChallengeEntity>existingParticipation =
                userChallnegeRepository.findByUserIdAndChallengeId(userId,cno);

        if(existingParticipation.isPresent() && existingParticipation.get().getStatus().equals("in_progress")){
            //이미 참여중이면 참여 실패
            return false;
        }


        //4.새로운 참여 정보 생성
        UserChallengeEntity userChallenge = UserChallengeEntity.builder()
                .userId(userId)
                .challengeId(cno)
                .status("in_progress")
                .build();
        //5.참여 정보 저장
        UserChallengeEntity savedEntity = userChallnegeRepository.save(userChallenge);

        return savedEntity.getId()>0; //저장된 엔티티의 ID가 유효하면 성공
    }




    //4.사용자 챌린지 목록 조회

    public List<ChallengeDto>getUserChallenges(int userid){
        //1.사용자 참여 정보 조회
        List<UserChallengeEntity>userChallengeEntityList = userChallnegeRepository.findByUserId(userid);

        //2.참여 중인 챌린지 ID 목록 추출
        List<Integer>challengeIds = userChallengeEntityList.stream().map(UserChallengeEntity::getChallengeId).collect(Collectors.toList());

        if(challengeIds.isEmpty()){
            return List.of(); //빈 리스트 반환
        }

        //3.해당 챌린지 정보 조회
        List<ChallengeEntity>challengeEntityList= challengeEntityRepository.findAllById(challengeIds);

        //4.참여 상태 정보 추가하여 DTO 변환
        return challengeEntityList.stream().map(challengeEntity -> {
            ChallengeDto dto = ChallengeDto.fromEntity(challengeEntity);

            UserChallengeEntity participation = userChallengeEntityList.stream().filter(uc->uc.getChallengeId() == challengeEntity.getCno()).findFirst().orElse(null);

            if(participation != null){
                dto.setStatus(participation.getStatus());
            }
            return dto;
        })
                .collect(Collectors.toList());
    }



    //5.챌린지 목록등록 (관리자 전용)
    public boolean registerChallenge(ChallengeDto challengeDto ,int loginid){
        //1.현재 회원번호의 엔티티 찾기(연관관계)FK, Optional : null 값 제어 기능 제공
        Optional<UserEntity>optionalUserEntity = userEntityRepository.findById(loginid);
        if(optionalUserEntity.isEmpty())return false; // 만약 조회된 회원엔티티가 없으면 false

        //2.관리자 권한 확인
        UserEntity userEntity = optionalUserEntity.get();
        if(!"ADMIN".equals(userEntity.getRole())){return false;
        } //관리자가 아니라면 등록 실패

        //3.dto를 -> 엔티티로 변환
        ChallengeEntity challengeEntity = challengeDto.toEntity();

        //엔티티 저장
        ChallengeEntity savedEntity = challengeEntityRepository.save(challengeEntity);

        //저장 결과 확인
        return savedEntity.getCno() > 0;
    }//f end

    //관리자 권한 확인

    public boolean checkAdminRole(int userId){
        Optional<UserEntity>optionalUserEntity = userEntityRepository.findById(userId);
        if(optionalUserEntity.isEmpty()){
            return false;
        }
    UserEntity user = optionalUserEntity.get();
        //관리자 식별
        return "ADMIN".equals(user.getRole());
    }

    //토큰으로 사용자 정보 조회
    public UserDto info(String token){
        //JWT 토큰에서 이메일 추출
        String email = jwtUtil.validateToken(token);
        if(email == null){
            throw new RuntimeException("유효하지 않은 토큰입니다.");

        }
        //이메일로 사용자 조회
        UserEntity userEntity = userEntityRepository.findByEmail(email);
        if(userEntity == null){
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return UserDto.fromEntity(userEntity);
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
