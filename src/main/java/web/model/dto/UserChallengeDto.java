package web.model.dto;

import lombok.*;
import web.model.entity.ReviewEntity;
import web.model.entity.UserChallengeEntity;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class UserChallengeDto {

    //사용자가 참여한 챌린지 정보를 담는 객체


    private int id;              // 참여 ID
    private int userId;          // 사용자 ID
    private int challengeId;     // 챌린지 ID
     private boolean completed;   // 완료 여부
    private boolean rewarded;    // 리워드 지급 여부
    private String status;       // 상태 (PENDING, COMPLETED, FAILED 등)

    // 추가 정보
    private String userName;     // 사용자 이름
    private String challengeTitle;     // 챌린지 제목
    private String challengeType;      // 챌린지 유형
    private int rewardPoints;          // 보상 포인트

    // 엔티티 -> DTO 변환
    public static UserChallengeDto fromEntity(UserChallengeEntity entity) {
        return UserChallengeDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .challengeId(entity.getChallengeId())
                .completed(entity.isCompleted())
                .rewarded(entity.isRewarded())
                .status(entity.getStatus().name())
                .build();
    }

    // DTO -> 엔티티 변환
    public UserChallengeEntity toEntity() {
        return UserChallengeEntity.builder()
                .id(id)
                .userId(userId)
                .challengeId(challengeId)
                .completed(completed)
                .rewarded(rewarded)
                .status(status != null ?
                        UserChallengeEntity.ChallengeParticipationStatus.valueOf(status) :
                        UserChallengeEntity.ChallengeParticipationStatus.IN_PROGRESS)
                .build();
    }

}
