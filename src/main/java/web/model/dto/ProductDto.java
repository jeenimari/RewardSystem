package web.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Text;
import web.model.entity.ProductEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {  //직접 판매되는 상품이 아니라 타사 사이트(네이버,쿠팡) 의 제품을 보여주는것 외부제품 정보를 저장하는 용도로 변경되어야함.

    private Integer id;                // 자체 식별 ID
    private String name;               // 제품명
    private String description;        // 제품 설명
    private String imageUrl;           // 제품 대표 이미지 URL
    private String productUrl;         // 제품 구매 페이지 URL (쿠팡/네이버 등)
    private String vendor;             // 판매처 (쿠팡, 네이버 등)
    private String price;              // 가격 (문자열로 처리 - "10,000원" 형태 가능)
    private String category;           // 카테고리
    private String externalProductId;  // 외부 사이트의 제품 ID (선택적)
    private int viewCount;             // 조회수
    private String registeredBy;       // 등록한 사용자 ID
    private double averageRating;      // 평균 평점
    private int reviewCount;           // 리뷰 수


}
