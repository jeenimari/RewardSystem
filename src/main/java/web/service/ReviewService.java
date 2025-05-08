package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import web.model.dto.ReviewDto;
import web.model.entity.ImgEntity;
import web.model.entity.ProductEntity;
import web.model.entity.ReviewEntity;
import web.model.entity.UserEntity;
import web.model.repository.ImgEntityRepository;
import web.model.repository.ProductEntityRepository;
import web.model.repository.ReviewRepository;
import web.model.repository.UserEntityRepository;
import web.util.FileUtil;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserEntityRepository userEntityRepository;
    private final ProductEntityRepository productEntityRepository;
    private final ImgEntityRepository imgEntityRepository;
    private final FileUtil fileUtil;

    // 1. 리뷰 작성
    public boolean createReview(ReviewDto reviewDto, String userEmail) {
        // 1. 제품 존재 여부 확인 - 수정: 파라미터 타입이 이미 Integer이므로 변환 불필요
        Optional<ProductEntity> optionalProduct = productEntityRepository.findById(reviewDto.getProductId());
        if (optionalProduct.isEmpty()) {
            return false; // 제품이 존재하지 않음
        }
        ProductEntity productEntity = optionalProduct.get();
        // 수정: String 대신 Integer 사용
        Integer productId = productEntity.getId();

        // 2. 사용자 존재 여부 확인
        Optional<UserEntity> optionalUser = userEntityRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            return false; // 사용자가 존재하지 않음
        }
        UserEntity userEntity = optionalUser.get();
        String userId = String.valueOf(userEntity.getId());

        // 3. 이미 리뷰를 작성했는지 확인
        // 수정: Integer 타입의 productId 전달
        List<ReviewEntity> existingReviews = reviewRepository.findByUserIdAndProductId(userId, productId);
        if (!existingReviews.isEmpty()) {
            return false; // 이미 리뷰를 작성함
        }

        // 4. 별점 유효성 검사
        if (reviewDto.getRating() < 1 || reviewDto.getRating() > 5) {
            return false; // 별점은 1~5 사이여야 함
        }

        // 5. 리뷰 엔티티 생성 및 저장
        ReviewEntity reviewEntity = ReviewEntity.builder()
                .userId(userId)
                // 수정: Integer 타입 사용
                .productId(productId)
                .rcontent(reviewDto.getRContent())
                .rating(reviewDto.getRating())
                .rewarded(false) // 초기에는 리워드 미지급 상태
                .build();

        ReviewEntity savedReview = reviewRepository.save(reviewEntity);

        // 6. 파일 처리, 첨부파일이 있으면 업로드 진행
        if (reviewDto.getFiles() != null && !reviewDto.getFiles().isEmpty()) {
            for (MultipartFile file : reviewDto.getFiles()) {
                // 파일 업로드
                String saveFileName = fileUtil.fileUpload(file);

                // 업로드 실패 시 트랜잭션 롤백
                if (saveFileName == null) {
                    throw new RuntimeException("리뷰 이미지 업로드 중 오류 발생");
                }

                // 이미지 엔티티 생성 및 저장
                ImgEntity imgEntity = ImgEntity.builder()
                        .iname(saveFileName)
                        .productEntity(productEntity)
                        .build();

                imgEntityRepository.save(imgEntity);
            }
        }

        return true;
    }

    // 2. 제품별 리뷰 목록 조회
    // 2. 제품별 리뷰 목록 조회
    public List<ReviewDto> getReviewProduct(Integer productId) { // int에서 Integer로 변경
        // 수정: 직접 Integer로 전달, String 변환 제거
        List<ReviewEntity> reviewEntities = reviewRepository.findByProductId(productId);
        return reviewEntities.stream()
                .map(entity -> {
                    ReviewDto dto = ReviewDto.fromEntity(entity);

                    // 추가 정보 설정 (제품명, 사용자명)
                    try {
                        // 수정: Integer 타입이므로 변환 불필요
                        Optional<ProductEntity> productEntity = productEntityRepository.findById(entity.getProductId());
                        if (productEntity.isPresent()) {
                            dto.setProductName(productEntity.get().getName());
                        }

                        Optional<UserEntity> userEntity = userEntityRepository.findById(Integer.parseInt(entity.getUserId()));
                        if (userEntity.isPresent()) {
                            dto.setUserName(userEntity.get().getUname());
                        }
                    } catch (Exception e) {
                        System.out.println("추가 정보 조회 중 오류: " + e.getMessage());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 3. 사용자 작성 리뷰 목록 조회
    public List<ReviewDto> getReviewsByUser(String userEmail) {
        Optional<UserEntity> userEntity = userEntityRepository.findByEmail(userEmail);
        if (userEntity.isEmpty()) {
            throw new NoSuchElementException("사용자를 찾을 수 없습니다.");
        }

        String userId = String.valueOf(userEntity.get().getId());
        List<ReviewEntity> reviewEntities = reviewRepository.findByUserId(userId);
        return reviewEntities.stream()
                .map(entity -> {
                    ReviewDto dto = ReviewDto.fromEntity(entity);

                    // 추가 정보 설정 (제품명)
                    try {
                        // 수정: Integer 타입이므로 변환 불필요
                        Optional<ProductEntity> productEntity = productEntityRepository.findById(entity.getProductId());
                        if (productEntity.isPresent()) {
                            dto.setProductName(productEntity.get().getName());
                        }
                    } catch (Exception e) {
                        System.out.println("제품 정보 조회 중 오류: " + e.getMessage());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 4. 리뷰 수정
    public boolean updateReview(int reviewId, ReviewDto reviewDto, String userEmail) {
        // 리뷰 존재 여부 확인
        Optional<ReviewEntity> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return false; // 리뷰가 존재하지 않음
        }

        ReviewEntity reviewEntity = optionalReview.get();

        // 사용자 검증 (본인 리뷰만 수정 가능)
        Optional<UserEntity> optionalUser = userEntityRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            return false; // 사용자가 존재하지 않음
        }

        String userId = String.valueOf(optionalUser.get().getId());
        if (!reviewEntity.getUserId().equals(userId)) {
            return false; // 본인 리뷰가 아님
        }

        // 별점 유효성 검사
        if (reviewDto.getRating() < 1 || reviewDto.getRating() > 5) {
            return false; // 별점은 1~5 사이여야 함
        }

        // 리뷰 내용 업데이트
        reviewEntity.setRcontent(reviewDto.getRContent());
        reviewEntity.setRating(reviewDto.getRating());
        reviewRepository.save(reviewEntity);

        // 제품 정보 조회 (이미지 저장용)
        // 수정: Integer 타입이므로 변환 불필요
        Optional<ProductEntity> optionalProduct = productEntityRepository.findById(reviewEntity.getProductId());
        if (optionalProduct.isEmpty()) {
            return false;
        }
        ProductEntity productEntity = optionalProduct.get();

        // 파일 처리 (새 이미지 업로드)
        if (reviewDto.getFiles() != null && !reviewDto.getFiles().isEmpty()) {
            for (MultipartFile file : reviewDto.getFiles()) {
                String saveFileName = fileUtil.fileUpload(file);

                if (saveFileName == null) {
                    throw new RuntimeException("리뷰 이미지 업로드 중 오류 발생");
                }

                ImgEntity imgEntity = ImgEntity.builder()
                        .iname(saveFileName)
                        .productEntity(productEntity)
                        .build();

                imgEntityRepository.save(imgEntity);
            }
        }

        return true;
    }

    // 5. 리뷰 삭제 (수정사항 없음)
    public boolean deleteReview(int reviewId, String userEmail) {
        // 리뷰 존재 여부 확인
        Optional<ReviewEntity> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return false; // 리뷰가 존재하지 않음
        }

        ReviewEntity reviewEntity = optionalReview.get();

        // 사용자 검증 (본인 리뷰만 삭제 가능)
        Optional<UserEntity> optionalUser = userEntityRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            return false; // 사용자가 존재하지 않음
        }

        String userId = String.valueOf(optionalUser.get().getId());
        if (!reviewEntity.getUserId().equals(userId)) {
            return false; // 본인 리뷰가 아님
        }

        // 리뷰 삭제
        reviewRepository.delete(reviewEntity);
        return true;
    }

    // 6. 리워드 지급 처리 메서드 (수정사항 없음)
    public boolean processReviewReward(int reviewId) {
        Optional<ReviewEntity> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return false;
        }

        ReviewEntity reviewEntity = optionalReview.get();

        // 이미 리워드를 지급한 경우
        if (reviewEntity.getRewarded()) {
            return false;
        }

        // 포인트 지급 처리 (임시 구현)
        reviewEntity.setRewarded(true);
        reviewRepository.save(reviewEntity);
        return true;
    }
}