package web.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDto {

    private int id;    // 유저 아이디 (pk) 로 구분
    private String uname;// 유저 이름
    private String email; // 유저 이메일
    private String pw;   // 유저 비밀번호
    private int pointBalance;  //잔여 포인트

    //유저 조회할때 필요한 필드(미구현)


    // dto-> entity로 변환
    public UserEntity toEntity(){
        return UserEntity.builder()
                .id(id)
                .uname(uname)
                .email(email)

                .pw(pw)
                .pointBalance(pointBalance)
                .build();
    }
    //  UserEntity를  UserDto로 변환하는 과정
    public static UserDto fromEntity(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .uname(entity.getUname())
                .build();
    }


    //*toDto : 유저 전체 조회 , 유저 조회 사용
    public static UserDto toDto(UserEntity userEntity){
        return UserDto.builder()
                .email(userEntity.getEmail())
                .uname(userEntity.getUname())
                .build();
    }


}
