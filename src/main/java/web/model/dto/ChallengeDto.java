package web.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.ChallengeEntity;
import web.model.entity.UserEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ChallengeDto {


    private int cno;    // 챌린지 식별 PK
    private String title; // 챌린지 제목
    private String content; // 설명
    private String type;   // 챌린지 유형
    private int rewardPoints ; //보상포인트
    private String status; // 상태  (액티브 , 인액티브)


    //엔티티 -> dto 변환 정적 메서드

    public static ChallengeDto fromEntity(ChallengeEntity challengeEntity){
        return ChallengeDto.builder()
                .cno(challengeEntity.getCno())
                .content(challengeEntity.getContent())
                .title(challengeEntity.getTitle())
                .type(challengeEntity.getType().name())
                .rewardPoints(challengeEntity.getRewardPoints())
                .status(challengeEntity.getStatus().name())
                .build();
    }


    //DTO-> 엔티티 변환

    public ChallengeEntity toEntity(){
        return ChallengeEntity.builder()
                .cno(cno)
                .title(title)
                .content(content)
                .type(ChallengeEntity.ChallengeType.valueOf(type))
                .rewardPoints(rewardPoints)
                .status(status!=null ? ChallengeEntity.ChallengeStatus.valueOf(status):ChallengeEntity.ChallengeStatus.ACTIVE)
                .build();
    }

    //dto -> 엔티티 변환



//
//    // dto-> entity로 변환
//    public UserEntity toEntity(){
//        return UserEntity.builder()
//                .id(id)
//                .uname(uname)
//                .email(email)
//                .pw(pw)
//                .pointBalance(pointBalance)
//                .build();
//    }
//
//    //*toDto : 유저 전체 조회 , 유저 조회 사용
//    public static UserDto toDto(UserEntity userEntity){
//        return UserDto.builder()
//                .email(userEntity.getEmail())
//                .uname(userEntity.getUname())
//                .build();
//    }









}// c end
