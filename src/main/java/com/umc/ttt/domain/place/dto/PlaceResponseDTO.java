package com.umc.ttt.domain.place.dto;

import com.umc.ttt.domain.place.entity.enums.PlaceCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PlaceResponseDTO {

    @Builder
    @Getter
    public static class CurationDTO {
        private Long memberId;
        private Long placeId;
        private String curationTitle;
        private String curationContent;
    }

    @Builder
    @Getter
    public static class PlaceDTO {
        private Long placeId;
        private String title;
        private PlaceCategory category;
        private String address;
        private String holiday;
        private String weekdaysBusiness;
        private String sunBusiness;
        private String phone;
        private boolean hasParking;
        private boolean hasCafe;
        private boolean hasIndiePub;
        private boolean hasBookClub;
        private boolean hasSpaceRental;
        private String image;
        private Double userRating;  // 같은 취향 유저들의 평점
        private Double totalRating;  // 전체 평점
        private String curationTitle;
        private String curationContent;
        private boolean isScraped;
        private boolean isAdmin;
    }

    @Builder
    @Getter
    public static class PlacePreviewDTO {
        private Long placeId;
        private String title;
        private PlaceCategory category;
        private String address;
        private String image;
        private Double totalRating;
        private boolean isScraped;
    }

    @Builder
    @Getter
    public static class PlaceListDTO {
        private List<PlacePreviewDTO> places;
        private Long cursor;
        private int limit;
        private boolean hasNext;
    }

    @Builder
    @Getter
    public static class PlaceSuggestListDTO {
        private List<PlacePreviewDTO> places;
    }

    @Builder
    @Getter
    public static class EditorPickPlaceDTO {
        private Long placeId;
        private String title;
        private PlaceCategory category;
        private String image;
        private String curationTitle;
        private boolean isScraped;
    }

    @Builder
    @Getter
    public static class EditorPickPlaceListDTO {
        private List<EditorPickPlaceDTO> places;
    }
}
