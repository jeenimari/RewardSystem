package web.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "img")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor //롬복
public class ImgEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ino; //이미지 식별 번호

    @Column(nullable = false)
    private String iname; //이미지 명


    //단방향 통신
    @ManyToOne@JoinColumn(name="id") // 이미지가 제품 참조
    private ProductEntity productEntity;

}
