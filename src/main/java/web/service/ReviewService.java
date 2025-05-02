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
    // private final PointService pointService; // 포인트 서비스 (나중에 구현)

    // 1. 리뷰 작성
    public boolean createReview(ReviewDto reviewDto, String userEmail) {
        // 1. 제품 존재 여부 확인
        Optional<ProductEntity> optionalProduct = productEntityRepository.findById(Integer.parseInt(reviewDto.getProductId()));
        if (optionalProduct.isEmpty()) {
            return false; // 제품이 존재하지 않음
        }
        ProductEntity productEntity = optionalProduct.get();
        String productId = productEntity.getId().toString();

        // 2. 사용자 존재 여부 확인 - UserEntityRepository에 findByEmail 메서드 추가 필요
        Optional<UserEntity> optionalUser = userEntityRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            return false; // 사용자가 존재하지 않음
        }
        UserEntity userEntity = optionalUser.get();
        String userId = String.valueOf(userEntity.getId()); // int를 String으로 변환

        // 3. 이미 리뷰를 작성했는지 확인
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

                // 이미지 엔티티 생성 및 저장 (기존 엔티티 구조에 맞춤)
                ImgEntity imgEntity = ImgEntity.builder()
                        .iname(saveFileName)
                        .productEntity(productEntity) // ProductEntity와 연결
                        .build();

                imgEntityRepository.save(imgEntity);
            }
        }

        // 7. 포인트 지급 처리 (포인트 서비스가 구현되면 활성화)
        // boolean rewardResult = pointService.addReviewPoint(userId);
        // if (rewardResult) {
        //    savedReview.setRewarded(true);
        //    reviewRepository.save(savedReview);
        // }

        return true;
    }

    // 2. 제품별 리뷰 목록 조회
    public List<ReviewDto> getReviewProduct(int productId) {
        List<ReviewEntity> reviewEntities = reviewRepository.findByProductId(String.valueOf(productId));
        return reviewEntities.stream()
                .map(entity -> {
                    ReviewDto dto = ReviewDto.fromEntity(entity);

                    // 추가 정보 설정 (제품명, 사용자명)
                    try {
                        Optional<ProductEntity> productEntity = productEntityRepository.findById(Integer.parseInt(entity.getProductId()));
                        if (productEntity.isPresent()) {
                            dto.setProductName(productEntity.get().getName());
                        }
                        // 사용자 ID가 int인 경우 String에서 변환 필요
                        int userIdInt = Integer.parseInt(entity.getUserId());
                        Optional<UserEntity> userEntity = userEntityRepository.findById(Integer.parseInt(entity.getUserId()));
                        if (userEntity.isPresent()) {
                            dto.setUserName(userEntity.get().getUname()); // 사용자 이름은 uname 필드
                        }
                    } catch (Exception e) {
                        // 오류 로깅 (실제 구현 시 로거 사용)
                        System.out.println("추가 정보 조회 중 오류: " + e.getMessage());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 3. 사용자 작성 리뷰 목록 조회
    public List<ReviewDto> getReviewsByUser(String userEmail) {
        // 이메일로 사용자 ID 조회
        Optional<UserEntity> userEntity = userEntityRepository.findByEmail(userEmail);
        if (userEntity.isEmpty()) {
            throw new NoSuchElementException("사용자를 찾을 수 없습니다.");
        }

        String userId = String.valueOf(userEntity.get().getId()); // int를 String으로 변환
        List<ReviewEntity> reviewEntities = reviewRepository.findByUserId(userId);
        return reviewEntities.stream()
                .map(entity -> {
                    ReviewDto dto = ReviewDto.fromEntity(entity);

                    // 추가 정보 설정 (제품명)
                    try {
                        Optional<ProductEntity> productEntity = productEntityRepository.findById(Integer.parseInt(entity.getProductId()));
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

        String userId = String.valueOf(optionalUser.get().getId()); // int를 String으로 변환
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
        Optional<ProductEntity> optionalProduct = productEntityRepository.findById(Integer.parseInt(reviewEntity.getProductId()));
        if (optionalProduct.isEmpty()) {
            return false;
        }
        ProductEntity productEntity = optionalProduct.get();

        // 파일 처리 (새 이미지 업로드)
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

    // 5. 리뷰 삭제
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

        String userId = String.valueOf(optionalUser.get().getId()); // int를 String으로 변환
        if (!reviewEntity.getUserId().equals(userId)) {
            return false; // 본인 리뷰가 아님
        }

        // 리뷰 삭제
        reviewRepository.delete(reviewEntity);
        return true;
    }

    // 6. 리워드 지급 처리 메서드 (포인트 서비스 연동용)
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

        // 포인트 지급 처리 (포인트 서비스가 구현되면 활성화)
        // boolean result = pointService.addReviewPoint(reviewEntity.getUserId());
        // if (result) {
        //     reviewEntity.setRewarded(true);
        //     reviewRepository.save(reviewEntity);
        //     return true;
        // }

        // 임시로 리워드 지급 처리
        reviewEntity.setRewarded(true);
        reviewRepository.save(reviewEntity);
        return true;
    }
}