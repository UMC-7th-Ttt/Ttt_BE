package com.umc.ttt.domain.place.controller;

import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.domain.place.dto.PlaceRequestDTO;
import com.umc.ttt.domain.place.dto.PlaceResponseDTO;
import com.umc.ttt.domain.place.service.PlaceApiService;
import com.umc.ttt.domain.place.service.PlaceCommandService;
import com.umc.ttt.domain.place.service.PlaceImageCrawlingService;
import com.umc.ttt.domain.place.service.PlaceQueryService;
import com.umc.ttt.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceRestController {

    private final PlaceApiService placeApiService;
    private final PlaceCommandService placeCommandService;
    private final PlaceQueryService placeQueryService;
    private final MemberRepository memberRepository;
    private final PlaceImageCrawlingService placeImageCrawlingService;

    @PostMapping
    @Operation(summary = "독립서점, 북카페 Open API 데이터 저장", description = "서버 테스트용 api입니다. 연동x")
    public ApiResponse<String> fetchBooks() throws Exception {
        placeApiService.fetchAndSaveOpenApiData();
        return ApiResponse.onSuccess("독립서점, 북카페 Open API 데이터가 저장되었습니다.");
    }

    @PatchMapping("/images")
    @Operation(summary = "공간 이미지 데이터 저장 - Naver 검색 API", description = "서버 테스트용 api입니다. 연동x")
    public ApiResponse<String> updateImagesForAllPlaces() {
        placeApiService.updateImagesForAllPlaces();
        return ApiResponse.onSuccess("모든 장소의 이미지가 업데이트되었습니다.");
    }

    @PatchMapping("/images/crawling")
    @Operation(summary = "공간 이미지 데이터 저장 - Naver 지도 크롤링", description = "서버 테스트용 api입니다. 연동x\n\n" +
            "크롬 버전에 따라 동작하지 않을 수 있습니다. 로컬 DB에서는 호출하지 말고 naver 검색 api 사용해주세요. 10분에 8-90개 저장됨(데이터 약 1200개)")
    public ApiResponse<String> updateImagesByCrawlingForAllPlaces() {
        placeImageCrawlingService.crawlAndSaveImages();
        return ApiResponse.onSuccess("모든 장소의 이미지가 업데이트되었습니다.");
    }

    @PatchMapping("/{placeId}/curations")
    @Operation(summary = "공간 큐레이션 작성, 수정", description = "관리자만 작성 및 수정 가능합니다. 삭제의 경우 빈 문자열을 전달해주세요.")
    public ApiResponse<PlaceResponseDTO.CurationDTO> updateCuration(@PathVariable(name = "placeId") Long placeId,
                                                                    @Valid @RequestBody PlaceRequestDTO.CurationDTO curationDTO) {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        return ApiResponse.onSuccess(placeCommandService.updateCuration(placeId, curationDTO, member));
    }

    @GetMapping("/{placeId}")
    @Operation(summary = "공간 상세 조회")
    public ApiResponse<PlaceResponseDTO.PlaceDTO> getPlace(@PathVariable(name = "placeId") Long placeId) {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        return ApiResponse.onSuccess(placeQueryService.getPlace(placeId, member));
    }

    @GetMapping
    @Operation(summary = "공간 목록 조회 - 가까운순, 추천순", description = "무한 스크롤 방식으로 공간 목록을 조회합니다.\n\n" +
            "첫 페이지 조회 시 cursor 값으로 0을 전달해주세요.\n\n" +
            "첫 페이지가 아닌 경우 이전 응답의 hasNext가 true일 때, nextCursor 값을 cursor로 전달해주세요.")
    @Parameters({
            @Parameter(name = "lat", description = "현재 위치의 위도. 가까운순으로 조회할 때만 전달해주세요."),
            @Parameter(name = "lon", description = "현재 위치의 경도. 가까운순으로 조회할 때만 전달해주세요."),
            @Parameter(name = "sort", description = "전체: all, 서점: bookstore, 북카페: cafe"),
    })
    public ApiResponse<PlaceResponseDTO.PlaceListDTO> getPlaceList(@RequestParam(name = "lat", required = false) Double lat,
                                                                    @RequestParam(name = "lon", required = false) Double lon,
                                                                    @RequestParam(name = "sort", defaultValue = "all") String sort,
                                                                    @RequestParam(name = "cursor", defaultValue = "0") Long cursor,
                                                                    @RequestParam(name = "limit", defaultValue = "10") int limit) {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        return ApiResponse.onSuccess(placeQueryService.getPlaceList(lat, lon, sort, cursor, limit, member));
    }

    @GetMapping("/search")
    @Operation(summary = "공간 검색", description = "무한 스크롤 방식으로 공간을 검색합니다.\n\n" +
            "첫 페이지 조회 시 cursor 값으로 0을 전달해주세요.\n\n" +
            "첫 페이지가 아닌 경우 이전 응답의 hasNext가 true일 때, nextCursor 값을 cursor로 전달해주세요.")
    public ApiResponse<PlaceResponseDTO.PlaceListDTO> searchPlaceList(@RequestParam(name = "keyword") String keyword,
                                                                      @RequestParam(name = "cursor", defaultValue = "0") Long cursor,
                                                                      @RequestParam(name = "limit", defaultValue = "10") int limit) {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        return ApiResponse.onSuccess(placeQueryService.searchPlaceList(keyword, cursor, limit, member));
    }

    @GetMapping("/suggestions")
    @Operation(summary = "공간 추천", description = "사용자가 선호하는 공간 카테고리를 기반으로 공간을 추천합니다. 추천 결과로 10개의 공간을 반환합니다.")
    public ApiResponse<PlaceResponseDTO.PlaceSuggestListDTO> suggestPlaces() {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        return ApiResponse.onSuccess(placeQueryService.suggestPlaces(member));
    }

    @GetMapping("/editor-pick")
    @Operation(summary = "공간 에디터 픽", description = "에디터가 픽한 공간 5곳을 반환합니다.")
    public ApiResponse<PlaceResponseDTO.EditorPickPlaceListDTO> getEditorPickPlaces() {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        return ApiResponse.onSuccess(placeQueryService.getEditorPickPlaces(member));
    }
}
