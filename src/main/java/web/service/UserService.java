package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.model.dto.UserDto;
import web.model.entity.UserEntity;
import web.model.repository.UserEntityRepository;
import web.util.JwtUtil;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor // final 필드의 생성자 자동생성
@Transactional //트랜잭션 : 여러개의 SQL 하나의 논리단위
public class UserService {
    private final UserEntityRepository userEntityRepository;
    private final JwtUtil jwtUtil;

    private final StringRedisTemplate stringRedisTemplate;

    //1.로그인

    public String login(UserDto userDto){
        //1.아이디(이메일) DB에서 조회하여 엔티티찾기
        UserEntity userEntity
                = userEntityRepository.findByEmail(userDto.getEmail());
        //조회된 엔티티 없으면?
        if(userEntity == null){
            return null;
        }//로그인실패
        //3.조회된 엔티티 비밀번호 검증
        BCryptPasswordEncoder passwordEncoder =new BCryptPasswordEncoder(); //비크립트객체생성
        boolean inMath = passwordEncoder.matches(userDto.getPw(),userEntity.getPw());
        //비밀번호 검증 실패면
        if(inMath==false)return null; //로그인실패
        //5.비밀번호 검증성공하면
        String token = jwtUtil.createToken(userEntity.getEmail());
        System.out.println(">>>발급된 토큰 = " + token);
        //+레디스에 24시간만 저장되는 로그인 로그(기록) 하기
        stringRedisTemplate.opsForValue().set(
                "RECENT_LOGIN:"+userDto.getEmail(),"true",1, TimeUnit.DAYS
        );
        return token;

    }


    //2.로그아웃 로그아웃할 토근 가져오기
    public void logout(String token){
        //해당 토큰이메일 조회
        String email = jwtUtil.validateToken(token);
        //조회된 이메일 레디스 토큰 삭제
        jwtUtil.deleteToken(email);
    }



    //3.회원가입
    public boolean signUp(UserDto userDto){
        //1.암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); //암호화 비크립트 객체 생성
        String hashedPwd = passwordEncoder.encode(userDto.getPw()); //암호화 지원하는 함수
        userDto.setPw(hashedPwd);

        //2.DTO를 entity로 변환하기
        UserEntity userEntity = userDto.toEntity();
        //3.리포지토리 이용한 entitiy 영속화하기 , 영속된 결과 반환
        UserEntity saveEntity = userEntityRepository.save(userEntity);
        //4.영속된 엔티티의 자동생성된 PK 확인
        if(saveEntity.getId()>=1){return true;}
        return false;
    }

    //4.회원수정
    public UserDto userUpdate(UserDto userDto,String token){
        //1.토큰 검증
        String email =  jwtUtil.validateToken(token);
        if(email == null){
            return null; //유효하지 않은 토큰
        }
        //2.토큰의 이메일로 사용자 엔티티 조회
        UserEntity userEntity = userEntityRepository.findByEmail(email);
        if(userEntity == null){
            return null; // 사용자가 존재하지 않음
        }
        //3.권한 검증(userDTo의 id와 조회한 엔티티의 id가 일치하는지)
        if(userDto.getId()!= userEntity.getId()){
            return null; //본인 정보만 수정가능
        }
        //4. 비밀번호 변경 요청이 있는 경우 암호화?

        // 결과를 DTO로 변환하여 반환
        return UserDto.toDto(userEntity);
    }// 회원수정 end





    //5.사용자 프로필 조회

    public UserDto viewUser(int id,String token){


        //1.id에 해당하는 엔티티조회
        Optional<UserEntity>userEntityOptional = userEntityRepository.findById(id);
        //2.조회 결과 없으면 null
        if(userEntityOptional.isEmpty())return null;
        //3.조회 결과 있으면 엔티티 꺼내기 .get()
        UserEntity userEntity =  userEntityOptional.get();
        //4.조회된 엔티티를 DTO로 변환
        return UserDto.toDto(userEntity);

    }


}
