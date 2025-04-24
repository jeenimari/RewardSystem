package web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.dto.ChallengeDto;
import web.service.ChallengeService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/challenge")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChallengeController {

    private final ChallengeService challengeService;

    //1.챌린지 목록 조회

    @GetMapping("/check")
    public ResponseEntity<List<ChallengeDto>>getCheck(){
        List<ChallengeDto>challengeDtoList = challengeService.getCheck();
        return ResponseEntity.status(200).body(challengeDtoList);
    }
    //2.특정 챌린지 상세조회
    @GetMapping("/detailcheck")
    public ResponseEntity<ChallengeDto>detailChallenge(@RequestParam int cno){
        ChallengeDto challengeDto = challengeService.detailChallenge(cno);
        if(challengeDto ==null){
            return ResponseEntity.status(404).body(null);//404 not found 와 null 반환
        }else{
            return ResponseEntity.status(200).body(challengeDto);
        }
    }


    //3.챌린지 참여기능
    @PostMapping("/praticipate")
    public ResponseEntity<Boolean>participateChallenge(
            @RequestParam int cno,
            @RequestHeader("Authorization")String token){
        //1.토큰에서 로그인한 사용자 id 추출
        int loginId;
        try{
            loginId = challengeService.info(token).getCno();
        } catch (Exception e) {
            return ResponseEntity.status(401).body(false);
        }

        //2.서비스에 사용자 ID와 챌린지 ID 전달하여 참여 처리
        boolean result = challengeService.participateChallenge(cno,loginId);

        //3.결과에 따른 응답 반환
        if(result){
            return ResponseEntity.status(200).body(true);//참여성공
        }else {
            return ResponseEntity.status(400).body(false); // 참여실패
        }
    }

    //4.사용자 챌린지 목록 조회

    @GetMapping("/my")
    public ResponseEntity<List<ChallengeDto>> getUserChallneges(@RequestHeader("Authorization")String token){
        //1.토큰에서 로그인한 사용자 ID 추출
        int loginId;
        try {
            loginId = challengeService.info(token).getCno();
        }catch (Exception e){
            return ResponseEntity.status(401).body(null);
        }

        //2.서비스에 사용자 ID 전달하여 참여 중인 챌린지 목록 조회
        List<ChallengeDto>challengeDtoList = challengeService.getUserChallenges(loginId);

        //3.조회 결과 반환
        return ResponseEntity.status(200).body(challengeDtoList);

    }//f end



    //5.챌린지 목록등록
    @PostMapping("/register")
    public ResponseEntity<Boolean>registerChallenge(
            @RequestHeader("Authorization")String token, //토큰받기
            @ModelAttribute ChallengeDto challengeDto){

        System.out.println("token = " + token + ", challengeDto = " + challengeDto);
        //1.현재 토큰의 작성자 구하기(id)
        int loginId;
        try{
            loginId = challengeService.info(token).getCno();
        } catch (Exception e) {
            return ResponseEntity.status(401).body(false); // 401 unatuhorized 와 false 반환
        }
        //2.관리자 권한 확인
        boolean isAdmin = challengeService.checkAdminRole(loginId);
        if(!isAdmin){
            return ResponseEntity.status(403).body(false); // 403 포비든(권한없음)과 fals 반환
        }


        //3저장할 dto와 회원번호를 서비스에게 전달
        boolean result = challengeService.registerChallenge(challengeDto,loginId);
        if(result == false)return ResponseEntity.status(400).body(false); //잘못된 요청 400 반환
        //4.요청 성공시 200반환
        return ResponseEntity.status(200).body(true); //200요청 성공과 ture 반환

    }//f end


}
