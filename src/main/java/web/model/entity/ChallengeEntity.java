package web.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class ChallengeEntity {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cno; //pk 식별

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING) //enum의 값을 문자열로 저장하도록 지정
    @Column(nullable = false)
    private ChallengeType type;

    @Column(nullable = false)
    private int rewardPoints;


    @Enumerated(EnumType.STRING) //enum의 값을 문자열로 저장하도록 지정
    @Column(nullable = false)
    private ChallengeStatus status = ChallengeStatus.ACTIVE;



    //enum 정의
    public enum ChallengeType{
        REVIEW, GAME
    }
    public enum ChallengeStatus {
        ACTIVE, INACTIVE
    }



}
