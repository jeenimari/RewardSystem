package web.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//기본키
    private int id; // Pk 설정

    @Column(unique = true,nullable = false) // 유저 이름 유니크
    private String uname;// 유저 이름

    @Column(unique = true,nullable = false)
    private String email; // 유저 이메일

    @Column(nullable = false)
    private String pw;   // 유저 비밀번호

    @ColumnDefault("0")
    private int pointBalance;  //잔여 포인트

}
