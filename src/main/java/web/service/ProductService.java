package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.model.dto.ProductDto;
import web.model.entity.ProductEntity;
import web.model.repository.ProductEntityRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductEntityRepository productEntityRepository;

    /**
     * 외부 제품 등록
     */
    @Transactional
    public boolean registerExternalProduct(ProductDto productDto, String userId) {
        try {
            ProductEntity product = ProductEntity.builder()
                    .name(productDto.getName())
                    .description(productDto.getDescription())
                    .imageUrl(productDto.getImageUrl())
                    .productUrl(productDto.getProductUrl())
                    .vendor(productDto.getVendor())
                    .price(productDto.getPrice())
                    .category(productDto.getCategory())
                    .externalProductId(productDto.getExternalProductId())
                    .viewCount(0)
                    .registeredBy(userId)
                    .build();

            productEntityRepository.save(product);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 모든 제품 목록 조회
     */
    public List<ProductDto> getAllProducts() {
        return productEntityRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 제품 상세 조회
     */
    @Transactional
    public ProductDto getProduct(int id) {
        Optional<ProductEntity> optionalProduct = productEntityRepository.findById(id);
        if (optionalProduct.isPresent()) {
            ProductEntity product = optionalProduct.get();
            // 조회수 증가
            product.setViewCount(product.getViewCount() + 1);
            productEntityRepository.save(product);
            return convertToDto(product);
        }
        return null;
    }

    /**
     * 카테고리별 제품 조회
     */
    public List<ProductDto> getProductsByCategory(String category) {
        return productEntityRepository.findByCategory(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 벤더별 제품 조회 (쿠팡, 네이버 등)
     */
    public List<ProductDto> getProductsByVendor(String vendor) {
        return productEntityRepository.findByVendor(vendor).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 제품 검색
     */
    public List<ProductDto> searchProducts(String keyword) {
        return productEntityRepository.findByNameContaining(keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 인기 제품 목록 (조회수 기준)
     */
    public List<ProductDto> getPopularProducts() {
        return productEntityRepository.findTop10By().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 최근 등록된 제품 목록
     */
    public List<ProductDto> getRecentProducts(int count) {
        return productEntityRepository.findAllByOrderByCreateAtDesc().stream()
                .limit(count)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 제품 엔티티를 DTO로 변환
     */
    private ProductDto convertToDto(ProductEntity entity) {
        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .productUrl(entity.getProductUrl())
                .vendor(entity.getVendor())
                .price(entity.getPrice())
                .category(entity.getCategory())
                .externalProductId(entity.getExternalProductId())
                .viewCount(entity.getViewCount())
                .registeredBy(entity.getRegisteredBy())
                .averageRating(entity.getAverageRating())
                .reviewCount(entity.getReviewCount())
                .build();
    }
}
