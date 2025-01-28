package com.umc.ttt.domain.place.converter;

import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.place.dto.PlaceResponseDTO;
import com.umc.ttt.domain.place.entity.Place;

import java.util.List;

public class PlaceConverter {

    public static PlaceResponseDTO.CurationDTO toCurationPreviewDTO(Place place, Member member) {
        return PlaceResponseDTO.CurationDTO.builder()
                .memberId(member.getId())
                .placeId(place.getId())
                .curationTitle(place.getCurationTitle())
                .curationContent(place.getCurationContent())
                .build();
    }

    public static PlaceResponseDTO.PlaceDTO toPlaceDTO(Place place, Member member, boolean isScraped, boolean isAdmin) {
        return PlaceResponseDTO.PlaceDTO.builder()
                .placeId(place.getId())
                .title(place.getTitle())
                .category(place.getCategory())
                .address(place.getAddress())
                .holiday(place.getHoliday())
                .weekdaysBusiness(place.getWeekdaysBusiness())
                .sunBusiness(place.getSunBusiness())
                .phone(place.getPhone())
                .hasParking(place.getHasParking())
                .hasCafe(place.getHasCafe())
                .hasIndiePub(place.getHasIndiePub())
                .hasBookClub(place.getHasBookClub())
                .hasSpaceRental(place.getHasSpaceRental())
                .image(place.getImage())
                .userRating(getUserRating(place, member))   // 같은 취향 유저들의 평점
                .totalRating(place.getRating()) // 전체 평점
                .curationTitle(place.getCurationTitle())
                .curationContent(place.getCurationContent())
                .isScraped(isScraped)
                .isAdmin(isAdmin)
                .build();
    }

    private static Double getUserRating(Place place, Member member) {
        // TODO: 사용자 평점 계산 로직 - 추후 구현
        return null;
    }

    public static PlaceResponseDTO.PlacePreviewDTO toPlacePreviewDTO(Place place, boolean isScraped) {
        return PlaceResponseDTO.PlacePreviewDTO.builder()
                .placeId(place.getId())
                .title(place.getTitle())
                .category(place.getCategory())
                .address(place.getAddress())
                .image(place.getImage())
                .totalRating(place.getRating())
                .isScraped(isScraped)
                .build();
    }

    public static PlaceResponseDTO.PlaceListDTO toPlaceListDTO(List<Place> places, Long nextCursor, int limit, boolean hasNext, List<Long> scrapedPlaceIds) {
        List<PlaceResponseDTO.PlacePreviewDTO> placePreviewDTOs = places.stream()
                .map(place -> toPlacePreviewDTO(place, scrapedPlaceIds.contains(place.getId())))
                .toList();

        return PlaceResponseDTO.PlaceListDTO.builder()
                .places(placePreviewDTOs)
                .cursor(nextCursor)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }

    public static PlaceResponseDTO.PlaceSuggestListDTO toPlaceSuggestListDTO(List<Place> places, List<Long> scrapedPlaceIds) {
        List<PlaceResponseDTO.PlacePreviewDTO> placePreviewDTOs = places.stream()
                .map(place -> toPlacePreviewDTO(place, scrapedPlaceIds.contains(place.getId())))
                .toList();

        return PlaceResponseDTO.PlaceSuggestListDTO.builder()
                .places(placePreviewDTOs)
                .build();
    }

    public static PlaceResponseDTO.EditorPickPlaceDTO toEditorPickPlaceDTO(Place place, boolean isScraped) {
        return PlaceResponseDTO.EditorPickPlaceDTO.builder()
                .placeId(place.getId())
                .title(place.getTitle())
                .category(place.getCategory())
                .image(place.getImage())
                .curationTitle(place.getCurationTitle())
                .isScraped(isScraped)
                .build();
    }

    public static PlaceResponseDTO.EditorPickPlaceListDTO toEditorPickPlaceListDTO(List<Place> places, List<Long> scrapedPlaceIds) {
        List<PlaceResponseDTO.EditorPickPlaceDTO> editorPickPlaceDTOS = places.stream()
                .map(place -> toEditorPickPlaceDTO(place, scrapedPlaceIds.contains(place.getId())))
                .toList();

        return PlaceResponseDTO.EditorPickPlaceListDTO.builder()
                .places(editorPickPlaceDTOS)
                .build();
    }
}
