package web.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.ReviewEntity;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ReviewDto {

    private int id; // 아이디
    private String userId; //사용자 아이디
    private String productId; // 제품 아이디
    private String rContent; // 리뷰 내용
    private int rating; //평점
//    private String createAt; // 작성일
    private boolean rewarded; // 포인트 지급 여부

    //추가정보
    private String userName;
    private String productName;

    //엔티티-> dto변환
    public static ReviewDto fromEntity(ReviewEntity reviewEntity){
        return ReviewDto.builder()
                .id(reviewEntity.getId())
                .userId(reviewEntity.getUserId())
                .productId(reviewEntity.getProductId())
                .rContent(reviewEntity.getRcontent())
                .rating(reviewEntity.getRating())
                .rewarded(reviewEntity.getRewarded())
                .build();
    }


    //DTo->엔티티 변환

    public ReviewEntity toEntity(){
        return ReviewEntity.builder()
                .id(id)
                .userId(userId)
                .productId(productId)
                .rcontent(productId)
                .rating(rating)
                .rewarded(rewarded)
                .build();
    }




}
