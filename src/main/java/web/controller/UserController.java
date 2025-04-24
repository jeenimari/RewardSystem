package web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.dto.UserDto;
import web.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    //1.로그인
    @PostMapping("/login")
    public ResponseEntity<String>login(@RequestBody UserDto userDto){
        String token = userService.login(userDto);
        if(token!=null){return ResponseEntity.status(200).body(token);// 만약 토큰이 존재하면 로그인성공
        } else {return ResponseEntity.status(401).body("로그인성공");} //인증실패 : 401

    }// f end


    //2.로그아웃 로그아웃 할 토큰 가져오기

    @GetMapping("/logout")
    public ResponseEntity<Void>logout(@RequestHeader("Authorization")String token){
        userService.logout(token);
        return ResponseEntity.status(204).build(); // 204 :성공했지만 반환할 값이 없음.
    }



    //3.회원가입

    @PostMapping("/signup") // http://localhost:8080/user/signup

    public ResponseEntity<Boolean>signUp(@RequestBody UserDto userDto){
        boolean result = userService.signUp(userDto);
        if(result){
            return ResponseEntity.status(201).body(true ); // 201(create ok) 뜻 true 반환
        }else {
            return ResponseEntity.status(400).body(false);// 400(bad request 잘못된 요청 뜻)
        }
    }// f end




    //4.회원수정
@PutMapping("/update")
    public ResponseEntity<UserDto> userUpdate(
            @RequestBody UserDto userDto,
            @RequestHeader("Authorization") String token){
        UserDto updatedUser = userService.userUpdate(userDto, token);
        if(updatedUser != null){
            return ResponseEntity.status(200).body(updatedUser); // 수정 성공
        } else {
            return ResponseEntity.status(400).body(null); // 수정 실패
        }
    }



//    @GetMapping("/view")
//    public ResponseEntity<ProductDto>viewProducts(@RequestParam long pno){ //required 생략시 pno 필수
//        ProductDto productDto = productService.viewProduct(pno);
//        if(productDto == null){
//            return ResponseEntity.status(404).body(null); //404 not found 와 null 반환
//        }else {
//            return ResponseEntity.status(200).body(productDto); // 200과 값 반환
//        }
//    }
   //5.사용자프로필 조회(인증권한 토큰기반) : 설계 : ?id=(토큰)
    @GetMapping("/info") //http://localhost:8080/user/info // headers: {'Authorization' : '토큰' }
    public ResponseEntity<UserDto>viewUser(
            @RequestParam int id,
            @RequestHeader("Authorization")String token) {
        UserDto userDto = userService.viewUser(id,token);
        if(userDto == null){
            return ResponseEntity.status(404).body(null);
        }else {
            return ResponseEntity.status(200).body(userDto);
        }
        }// f end


}
