package web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.dto.ReviewDto;
import web.service.ReviewService;
import web.util.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReviewController {
    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;


//1.리뷰작성 ( 등록)

    @PostMapping
    public ResponseEntity<Boolean>createReview(
            @RequestHeader("Authorization")String token,
            @RequestBody ReviewDto reviewDto){

        System.out.println("token = " + token + ", reviewDto = " + reviewDto);
        //1.현재 토큰의 작성자 구하기
        String userEmail;
        try{
            String jwtToken = token;

            //토큰 검증
            userEmail = jwtUtil.validateToken(jwtToken);
            if(userEmail ==null){
                return ResponseEntity.status(401).body(false); //인증실패
            }
            //2. 리뷰 등록 처리
            boolean reuslt = reviewService.createReview(reviewDto,userEmail);
            if(reuslt){
                return ResponseEntity.status(201).body(true);// 생성 성공
            }else {
                return ResponseEntity.status(400).body(false); //생성 실ㅍ
            }

        }catch (Exception e){
            System.out.println("e = " + e);
            return ResponseEntity.status(500).body(false);//인증실패
        }
    }


    //2.제품별 리뷰 목록 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDto>>getProductReviews(@PathVariable int productId){
        List<ReviewDto>reviewDtos = reviewService.getReviewProduct(productId);
        return ResponseEntity.ok(reviewDtos);
    }//f end

    //3.사용자 작성 리뷰 목록 조회
    @GetMapping("/user")
    public ResponseEntity<List<ReviewDto>>getUserReviews(@RequestHeader("Authorization")String token){
        String userEmail;
        try{


            // 토큰 검증
            userEmail = jwtUtil.validateToken(token);
            if (userEmail == null) {
                return ResponseEntity.status(401).body(null); // 인증 실패
            }
            List<ReviewDto> reviewDtos = reviewService.getReviewsByUser(userEmail);
            return ResponseEntity.ok(reviewDtos);

        }catch (Exception e){
            return ResponseEntity.status(401).body(null);// 인증 실패
        }
    }//f end

    //4.리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<Boolean>updateReview(
            @PathVariable int reviewId,
            @RequestHeader("Authorization")String token,
            @RequestBody ReviewDto reviewDto
    ){
        String userEmail;
        try{

            // 토큰 검증
            userEmail = jwtUtil.validateToken(token);
            if (userEmail == null) {
                return ResponseEntity.status(401).body(null); // 인증 실패
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(false); //인증실패
        }
        boolean result = reviewService.updateReview(reviewId,reviewDto,userEmail);
        return ResponseEntity.status(result? 200: 403).body(result);

    }//f end

    //5.리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Boolean>deleteReview(
            @PathVariable int reviewId,
            @RequestHeader("Authorization")String token){
        String userEmail;
        try{


            // 토큰 검증
            userEmail = jwtUtil.validateToken(token);
            if (userEmail == null) {
                return ResponseEntity.status(401).body(false); // 인증 실패
            }

        } catch (Exception e) {
            return ResponseEntity.status(401).body(false); //인증실패
        }
        boolean result = reviewService.deleteReview(reviewId,userEmail);
        return ResponseEntity.status(result ? 200 : 403).body(result);
    }
}




//String userId;
//        try {
//// "Bearer " 접두사 제거
//String jwtToken = token;
//            if (token.startsWith("Bearer ")) {
//jwtToken = token.substring(7);
//            }
//
//// 토큰 검증
//String email = jwtUtil.validateToken(jwtToken);
//            if(email == null){
//        return ResponseEntity.status(401).body(false); //인증 실패
//            }
//
//// 클레임에서 사용자 ID 추출
//userId = email; //이메일을 유저 아이디로 사용 가장 간단한 방법은 첫 번째 방법으로,이메일로 사용자를 조회하여 ID를 가져오는 방식
//
//        } catch (Exception e) {
//        return ResponseEntity.status(401).body(false); // 인증 실패
//        }
//
//boolean result = productService.registerExternalProduct(productDto, userId);
//
//        if (result) {
//        return ResponseEntity.status(201).body(true); // 생성 성공
//        } else {
//                return ResponseEntity.status(400).body(false); // 생성 실패
//        }
//                }
