package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드의 생성자 자동생성
@Transactional //트랜잭션 : 여러개의 SQL 하나의 논리단위
public class UserService {
}
