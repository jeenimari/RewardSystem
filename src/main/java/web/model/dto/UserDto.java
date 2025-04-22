package web.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDto {

    private String id;    // 유저 아이디
    private String uname;// 유저 이름
    private String email; // 유저 이메일
    private String pw;   // 유저 비밀번호
    private int point_balance;  //잔여 포인트


}
