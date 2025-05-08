package web.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity@Table(name="product")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor //롬복
public class ProductEntity extends BaseTime {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "product_url", nullable = false)
    private String productUrl;

    @Column(nullable = false)
    private String vendor;    // 판매처 (쿠팡, 네이버 등)

    private String price;     // 가격 (문자열로 처리 - "10,000원" 형태 가능)

    private String category;

    @Column(name = "external_product_id")
    private String externalProductId; // 외부 사이트의 제품 ID (선택적)

    @Column(name = "view_count")
    private int viewCount;         // 조회수

    @Column(name = "registered_by")  // 등록한 사용자 ID
    private String registeredBy;


    // 리뷰와의 관계 (양방향)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ReviewEntity> reviews = new ArrayList<>();

    // 평균 평점 계산 메서드
    public double getAverageRating() {
        if (reviews.isEmpty()) return 0;
        return reviews.stream()
                .mapToDouble(ReviewEntity::getRating)
                .average()
                .orElse(0);
    }

    // 리뷰 수 계산 메서드
    public int getReviewCount() {
        return reviews.size();
    }

    //ReviewEntity에 ProductEntity와의 연관관계를 나타내는 product 필드를 추가했습니다.
    //이 필드는 @ManyToOne 관계이며, productid 컬럼으로 연결됩니다.
    //insertable = false, updatable = false 속성은 이미 productId 필드가 있기 때문에 중복 업데이트를 방지하기 위한 설정입니다.
    //ProductEntity에서는 @Builder.Default 애노테이션을 추가하여 빌더 관련 경고를 해결했습니다.
    @Builder.Default // 빌더 기본값 설정
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviewsReviewEntities = new ArrayList<>();
}
