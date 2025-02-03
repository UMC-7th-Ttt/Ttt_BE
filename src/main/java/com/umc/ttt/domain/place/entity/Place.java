package com.umc.ttt.domain.place.entity;

import com.umc.ttt.domain.place.entity.enums.PlaceCategory;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;   // 상호명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceCategory category;    // 카테고리(BOOKSTORE, CAFE)

    @Column(nullable = false)
    private String address; // 주소

    @Column(nullable = false)
    private double xPos;    // 위도

    @Column(nullable = false)
    private double yPos;    // 경도

    private String holiday; // 휴무일

    private String weekdaysBusiness;    // 평일 영업시간

    private String satBusiness; // 토요일 영업시간

    private String sunBusiness; // 일요일 영업시간

    private String phone;   // 전화번호

    @Column(nullable = false)
    private boolean hasParking;    // 주차 가능 여부(서점, 카페)

    // 독립 서점 데이터
    @Column(nullable = false)
    private boolean hasCafe;  // 카페 여부

    @Column(nullable = false)
    private boolean hasIndiePub;  // 독서출판물 여부

    @Column(nullable = false)
    private boolean hasBookClub;  // 독서 모임 여부

    // 북카페 데이터
    @Column(nullable = false)
    private boolean hasSpaceRental; // 공간 대여 여부

    // 추가 정보
    @Column(columnDefinition = "TEXT")
    private String image;   // 장소 이미지

    private double rating = 0.0;  // 장소 전체 평균 평점(계산)

    // 큐레이션
    private String curationTitle;    // 제목

    @Column(columnDefinition = "TEXT")
    private String curationContent;    // 내용

    public void updateImage(String image) {
        this.image = image;
    }

    public void updateCuration(String curationTitle, String curationContent) {
        this.curationTitle = curationTitle;
        this.curationContent = curationContent;
    }

}
