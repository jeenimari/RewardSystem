package web.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_challenges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "challenge_id", nullable = false)
    private int challengeId;

    @Column(nullable = false)
    private String status; // "in_progress", "completed", "failed" 등의 값

}
