package web.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.dto.ProductDto;
import web.service.ProductService;
import web.util.JwtUtil;

import java.util.List;

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
            // 토큰 검증
            String email = jwtUtil.validateToken(token);
            if(email == null){
                return ResponseEntity.status(401).body(false); //인증 실패
            }

            // 클레임에서 사용자 ID 추출
            userId = email; //이메일을 유저 아이디로 사용 가장 간단한 방법은 첫 번째 방법으로,이메일로 사용자를 조회하여 ID를 가져오는 방식

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


    //2.제품 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductDto>>getAllProducts(){
        List<ProductDto>products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }//f end

    //3.제품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto>getproduct(@PathVariable int id){
        ProductDto productDto = productService.getProduct(id);
        if(productDto != null){
            return ResponseEntity.ok(productDto);
        }else {
            return ResponseEntity.notFound().build();
        }
    }//f end

    //4.카테고리별 제품 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDto>>getProductByCategory(@PathVariable String category){
        List<ProductDto>productDtos = productService.getProductsByCategory(category);
        return ResponseEntity.ok(productDtos); //응답 본문(body)에 포함될 데이터 productDtos는 클라이언트(프론트엔드)에 전송할 데이터입니다. 이 데이터는:
//        응답 본문(Response Body): 클라이언트가 받게 될 실제 데이터 내용입니다.
//        JSON 변환: Spring은 이 객체를 자동으로 JSON 형식으로 변환하여 HTTP 응답에 포함시킵니다.
//        데이터 전달: 클라이언트는 이 데이터를 받아 처리할 수 있습니다(예: 화면에 상품 목록 표시).
    }


    //5. 벤더별 제품 조회
    @GetMapping("/vendor/{vendor}")
    public ResponseEntity<List<ProductDto>>getProductsByVendor(@PathVariable String vendor){
        List<ProductDto>productDtos =  productService.getProductsByVendor(vendor);
        return ResponseEntity.ok(productDtos);
    }


    //6.제품 검색
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>>searchProducts(@RequestParam String keyword){
        List<ProductDto>productDtos = productService.searchProducts(keyword);
        return ResponseEntity.ok(productDtos);
    }

    //7.인기 제품 조회(조회수 기준)
    @GetMapping("/popular")
    public ResponseEntity<List<ProductDto>>getPopularProducts(){
        List<ProductDto>productDtos = productService.getPopularProducts();
        return ResponseEntity.ok(productDtos);
    }

    //8.최근 제품 조회
    @GetMapping("/recent")
    public ResponseEntity<List<ProductDto>>getRecentProducts(@RequestParam(defaultValue = "10")int count){
        List<ProductDto>productDtos = productService.getRecentProducts(count);
        return ResponseEntity.ok(productDtos);
    }




}
