package web.model.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_challenge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallengeEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "challenge_id", nullable = false)
    private int challengeId;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false)
    private boolean rewarded;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ChallengeParticipationStatus status;

    // 챌린지 참여 상태를 나타내는 열거형
    public enum ChallengeParticipationStatus {
        IN_PROGRESS,    // 진행 중
        COMPLETED,  // 완료
        FAILED,     // 실패
        CANCELLED   // 취소
    }

    // 선택적: 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", insertable = false, updatable = false)
    private ChallengeEntity challenge;
}
