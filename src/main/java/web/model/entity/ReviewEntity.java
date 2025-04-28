package web.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "userid", nullable = false, length = 30)
    private String userId;

    @Column(name = "productid", nullable = false, length = 30)
    private String productId;

    @Column(name = "rcontent", nullable = false, columnDefinition = "TEXT")
    private String rcontent;


    @Column(nullable = false)
    private Integer rating;


    @Column(nullable = false)
    private Boolean rewarded;

}
