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
public class ReviewEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userid", nullable = false, length = 30)
    private String userId;  // String 타입 유지

    @Column(name = "productid", nullable = false, length = 30)
    private Integer productId;

    @Column(name = "rcontent", nullable = false, columnDefinition = "TEXT")
    private String rcontent;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private Boolean rewarded;

    // 추가: ProductEntity와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productid", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductEntity product;
}