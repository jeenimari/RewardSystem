package web.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component // 스프링 컨테이너에 빈등록
public class JwtUtil {

    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    @Autowired
    private StringRedisTemplate stringRedisTemplate; // 레디스 조작하기 위한 객체

    //[1]JMT 토큰발급 , 사용자의 이메일 받아서 토큰만들기

    public String createToken(String email){
        String token = Jwts.builder()
                //토큰에 넣을 내용물 , 로그인 성공한 회원의 이메일 넣음
                .setSubject(email)
                //토큰이 발급된 날짜 new Date():자바에서 제공하는 현재 날짜 클래스
                .setIssuedAt(new Date())
                //토큰 만료 시간 1000x초x분x시
                .setExpiration(new Date(System.currentTimeMillis()+( 1000 * 60 * 60 *24 ) ) )//24시간동안만 토큰유지
                //지정한 비밀키로 암호화
                .signWith(secretKey)
                //위정보로 JWT 토큰 생성하고 반환
                .compact();

        //+ 중복 로그인 방지하고자 웹서버가 아닌 Redis에 토큰 저장
        //(1)Redis 에 토큰 저장
        stringRedisTemplate.opsForValue().set("JWT"+email,token,24, TimeUnit.HOURS);
        //(2)레디스에 저장된 키들 확인
        System.out.println(stringRedisTemplate.keys("*"));
        //(3)Redis에 저장된 key값 ghkrdls
        System.out.println(stringRedisTemplate.opsForValue().get("JWT:"+email));
        return token;
    }// createToken End

    //[2]JWT 토큰 검증
    public String validateToken(String token){
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey) // 검증하기 위한 비밀키 넣기
                    .build()    //검즈일행 , 검증 실패시 예외 발생
                    .parseClaimsJws(token) // 검증할 토큰 해석 , 실패시 예외 발생
                    .getBody(); // 검증 된 claims 객체 생성
            //claims 안에 다양한 토큰번호 들어있음
            System.out.println(claims.getSubject() );

            //+중복 로그인 방지하고자 Redis 에서 최근 로그인된 토큰 확인
            String email = claims.getSubject(); // 현재 전달받은 토큰의 저장된 회원정보(이메일)
            //1.레디스에서 최신 토큰 가져오기
            String redisToken = stringRedisTemplate.opsForValue().get("JWT:"+ email);
            if(token.equals(redisToken)){return email;} //현재 로그인상태 정상(중복 로그인이 아님)
            //(3)만약에 두 토큰이 다르면 null이 리턴됨 (토큰 불일치 또는 중복 로그인 감지)
            else {
                System.out.println(">>>중복 로그인 감지");
            }

        }catch (ExpiredJwtException e){
            //토큰이 만료 되었을 때 예외 클래스
            System.out.println(">>>JWT 토큰 기한 만료:"+ e);
        }catch (JwtException e){
            //그 외 모든 토큰 예외 클래스
            System.out.println(">>>JWT예외:"+ e);
        }
        return null;
    }


    //[3]로그아웃시 redis에 저장된 토큰 삭제 서비스
    public void deleteToken(String email){stringRedisTemplate.delete("JWT:"+email) ; }





}
