package web.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.dto.ProductDto;
import web.service.ProductService;
import web.util.JwtUtil;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;
    private final JwtUtil jwtUtil;


    // 1. 외부 제품 등록
    @PostMapping("/external")
    public ResponseEntity<Boolean> registerExternalProduct(
            @RequestHeader("Authorization") String token,
            @RequestBody ProductDto productDto) {

        String userId;
        try {
            // "Bearer " 접두사 제거
            String jwtToken = token;
            if (token.startsWith("Bearer ")) {
                jwtToken = token.substring(7);
            }

            // 토큰 검증 및 클레임 추출
            Claims claims = jwtUtil.validateToken(jwtToken);
            if (claims == null) {
                return ResponseEntity.status(401).body(false); // 인증 실패
            }

            // 클레임에서 사용자 ID 추출
            userId = claims.get("id").toString();

        } catch (Exception e) {
            return ResponseEntity.status(401).body(false); // 인증 실패
        }

        boolean result = productService.registerExternalProduct(productDto, userId);

        if (result) {
            return ResponseEntity.status(201).body(true); // 생성 성공
        } else {
            return ResponseEntity.status(400).body(false); // 생성 실패
        }
    }




}
