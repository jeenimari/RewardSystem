package web.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.model.entity.ProductEntity;

import java.util.List;

public interface ProductEntityRepository extends JpaRepository<ProductEntity,Integer> {

    //카테고리별 제품 찾기
    List<ProductEntity>findByCategory(String category);

    //벤더별 제품 찾기
    List<ProductEntity>findByVendor(String vendor);


    //제품명으로 검색
    List<ProductEntity>findByNameContaining(String keyword);

    //인기 제품 TOP10
    List<ProductEntity>findTop10();


    //최근 등록 제품
    List<ProductEntity>findCreated();


}
