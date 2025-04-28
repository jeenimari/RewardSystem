package web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.util.JwtUtil;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
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
        int loginId;

        try{


        }catch ()
    }



}
