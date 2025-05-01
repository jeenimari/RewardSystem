package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.model.dto.ReviewDto;
import web.model.entity.ProductEntity;
import web.model.entity.UserEntity;
import web.model.repository.ProductEntityRepository;
import web.model.repository.ReviewRepository;
import web.model.repository.UserEntityRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserEntityRepository userEntityRepository;
    private final ProductEntityRepository productEntityRepository;
    private final PointService pointService;




    //1.리뷰 작성

    public ReviewDto createReview(ReviewDto reviewDto, String userId) {

        //1.리뷰의 제품이 등록하는 여부 확인
        Optional<ProductEntity>optionalProduct(int){}


        //2.사용자 존재 여부 확인


        //3.이미 참여 중인지 확인


        //4.새로운 참여 정보 생성

        //5. 별점 유효성 검사

        //6.이미 리뷰를 작성했는지 확인


        // 리뷰 엔티티 생성 및 저장
    }//f end
}
