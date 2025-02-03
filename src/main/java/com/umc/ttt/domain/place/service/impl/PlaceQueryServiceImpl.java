package com.umc.ttt.domain.place.service.impl;

import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.entity.MemberPreferredCategory;
import com.umc.ttt.domain.member.entity.enums.Role;
import com.umc.ttt.domain.member.repository.MemberPreferredCategoryRepository;
import com.umc.ttt.domain.place.converter.PlaceConverter;
import com.umc.ttt.domain.place.dto.PlaceResponseDTO;
import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.place.entity.enums.PlaceCategory;
import com.umc.ttt.domain.place.repository.PlaceRepository;
import com.umc.ttt.domain.place.service.PlaceQueryService;
import com.umc.ttt.domain.scrap.repository.PlaceScrapRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.handler.PlaceHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceQueryServiceImpl implements PlaceQueryService {

    private final PlaceRepository placeRepository;
    private final PlaceScrapRepository placeScrapRepository;
    private final MemberPreferredCategoryRepository memberPreferredCategoryRepository;

    @Override
    public PlaceResponseDTO.PlaceDTO getPlace(Long placeId, Member member) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceHandler(ErrorStatus.PLACE_NOT_FOUND));
        boolean isScraped = placeScrapRepository.existsByScrapFolderMemberAndPlace(member, place);
        boolean isAdmin = member.getRole() == Role.ADMIN;
        return PlaceConverter.toPlaceDTO(place, isScraped, isAdmin);
    }

    @Override
    public PlaceResponseDTO.PlaceListDTO getPlaceList(Double lat, Double lon, String sort, Long cursor, int limit, Member member) {
        List<Place> places = new ArrayList<>();

        if (lat == null && lon == null) {
            // 추천 순 정렬
            if (sort.equals("bookstore")) {
                places = cursor.equals(0L) ? placeRepository.findFirstPageByCategoryOrderByRecommendation("BOOKSTORE",limit + 1)
                        : placeRepository.findByCategoryOrderByRecommendationWithCursor("BOOKSTORE", cursor,limit + 1);
            } else if (sort.equals("cafe")) {
                places = cursor.equals(0L) ? placeRepository.findFirstPageByCategoryOrderByRecommendation("CAFE",limit + 1)
                        : placeRepository.findByCategoryOrderByRecommendationWithCursor("CAFE", cursor,limit + 1);
            } else {    // 전체
                places = cursor.equals(0L) ? placeRepository.findFirstPageOrderByRecommendation(limit + 1)
                        : placeRepository.findOrderByRecommendationWithCursor(cursor,limit + 1);
            }
        } else if (lat != null && lon != null){
            // 거리 순 정렬
            if (sort.equals("bookstore")) {
                places = cursor.equals(0L) ? placeRepository.findFirstPageByCategoryOrderByDistance(lat, lon, "BOOKSTORE",limit + 1)
                        : placeRepository.findByCategoryOrderByDistanceWithCursor(lat, lon, "BOOKSTORE", cursor,limit + 1);
            } else if (sort.equals("cafe")) {
                places = cursor.equals(0L) ? placeRepository.findFirstPageByCategoryOrderByDistance(lat, lon, "CAFE",limit + 1)
                        : placeRepository.findByCategoryOrderByDistanceWithCursor(lat, lon, "CAFE", cursor,limit + 1);
            } else {    // 전체
                places = cursor.equals(0L) ? placeRepository.findFirstPageOrderByDistance(lat, lon, limit + 1)
                        : placeRepository.findOrderByDistanceWithCursor(lat, lon, cursor, limit + 1);
            }
        }

        // 스크랩 여부
        List<Long> scrapedPlaceIds = placeScrapRepository.findScrapedPlaceIdsByMemberAndPlaces(member, places);

        boolean hasNext = false;
        Long nextCursor = null;

        if (places.size() > limit) {
            places = places.subList(0, limit);
            nextCursor = places.get(places.size() - 1).getId();
            hasNext = true;
        }

        return PlaceConverter.toPlaceListDTO(places, nextCursor, limit, hasNext, scrapedPlaceIds);
    }

    @Override
    public PlaceResponseDTO.PlaceListDTO searchPlaceList(String keyword, Long cursor, int limit, Member member) {
        Pageable pageable = PageRequest.of(0, limit + 1);
        List<Place> places = placeRepository.findPlacesByKeyword(keyword, cursor, pageable);

        boolean hasNext = places.size() > limit;

        List<Place> paginatedPlaces = hasNext ? places.subList(0, limit) : places;
        List<Long> scrapedPlaceIds = placeScrapRepository.findScrapedPlaceIdsByMemberAndPlaces(member, paginatedPlaces);
        Long nextCursor = hasNext ? paginatedPlaces.get(paginatedPlaces.size() - 1).getId() : null;

        return PlaceConverter.toPlaceListDTO(paginatedPlaces, nextCursor, limit, hasNext, scrapedPlaceIds);
    }

    @Override
    public PlaceResponseDTO.PlaceSuggestListDTO suggestPlaces(Member member) {
        List<MemberPreferredCategory> preferredCategories = memberPreferredCategoryRepository.findByMember(member);

        boolean preferBookstore = false;
        boolean preferCafe = false;

        if (preferredCategories != null && !preferredCategories.isEmpty()) {
            for (MemberPreferredCategory preferredCategory : preferredCategories) {
                if (preferredCategory.getBookFormatCategory().getId() == 6) {   // 독립서점
                    preferBookstore = true;
                } else if (preferredCategory.getBookFormatCategory().getId() == 7) { // 북카페
                    preferCafe = true;
                }
            }
        }

        List<Place> places;

        if (preferBookstore && !preferCafe) {
            places = placeRepository.findPlacesByCategory(PlaceCategory.BOOKSTORE);
        } else if (preferCafe && !preferBookstore) {
            places = placeRepository.findPlacesByCategory(PlaceCategory.CAFE);
        } else {
            places = placeRepository.findAll();
        }

        List<Place> randomPlaces = places.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        collected -> {
                            Collections.shuffle(collected);
                            return collected.stream().limit(10).toList();
                        }
                ));
        List<Long> scrapedPlaceIds = placeScrapRepository.findScrapedPlaceIdsByMemberAndPlaces(member, randomPlaces);

        return PlaceConverter.toPlaceSuggestListDTO(randomPlaces, scrapedPlaceIds);
    }

    @Override
    public PlaceResponseDTO.EditorPickPlaceListDTO getEditorPickPlaces(Member member) {
        // 에디터 픽 공간 5곳 - 로이 픽
        List<String> titles = Arrays.asList("북앤드로잉", "유어마인드", "북파크", "땡스북스", "책방오늘");

        List<Place> places = titles.stream()
                .map(placeRepository::findPlaceByTitle)
                .filter(Objects::nonNull)
                .toList();

        List<Long> scrapedPlaceIds = placeScrapRepository.findScrapedPlaceIdsByMemberAndPlaces(member, places);

        return PlaceConverter.toEditorPickPlaceListDTO(places, scrapedPlaceIds);
    }

}
