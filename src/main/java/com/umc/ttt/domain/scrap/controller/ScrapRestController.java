package com.umc.ttt.domain.scrap.controller;

import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.scrap.dto.ScrapRequestDTO;
import com.umc.ttt.domain.scrap.dto.ScrapResponseDTO;
import com.umc.ttt.domain.scrap.service.ScrapCommandService;
import com.umc.ttt.domain.scrap.service.ScrapQueryService;
import com.umc.ttt.global.annotation.CurrentMember;
import com.umc.ttt.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScrapRestController {

    private final ScrapCommandService scrapCommandService;
    private final ScrapQueryService scrapQueryService;

    @GetMapping("/scraps/folders")
    @Operation(summary = "스크랩 폴더 목록 조회 - 마이페이지", description = "도서, 공간은 기본 폴더입니다.")
    public ApiResponse<ScrapResponseDTO.ScrapFolderListDTO> getScrapFolders(@CurrentMember Member member) {
        return ApiResponse.onSuccess(scrapQueryService.getScrapFolders(member));
    }

    @GetMapping("/scraps/folders/{folderId}")
    @Operation(summary = "특정 폴더의 스크랩 내역 조회 - 마이페이지", description = "무한 스크롤 방식으로 스크랩 내역을 조회합니다.\n\n" +
            "첫 페이지 조회 시 각 cursor 값으로 0을 전달해주세요.\n\n" +
            "첫 페이지가 아닌 경우 이전 응답의 hasNext가 true일 때, nextBookCursor, nextPlaceCursor 값을 각 cursor로 전달해주세요.")
    public ApiResponse<ScrapResponseDTO.ScrapListDTO> getScrapList(@PathVariable(name = "folderId") Long folderId,
                                                                   @RequestParam(name = "bookCursor", required = false) Long bookCursor,
                                                                   @RequestParam(name = "placeCursor", required = false) Long placeCursor,
                                                                   @RequestParam(name = "limit", defaultValue = "10") int limit,
                                                                   @CurrentMember Member member) {
        return ApiResponse.onSuccess(scrapQueryService.getScrapList(folderId, bookCursor, placeCursor, limit, member));
    }

    @PostMapping("/scraps/folders")
    @Operation(summary = "스크랩 폴더 생성 - 마이페이지", description = "마이페이지에서 스크랩 폴더 생성 시 사용되는 api입니다.")
    public ApiResponse<ScrapResponseDTO.ScrapFolderDTO> createScrapFolder(@RequestParam(name = "folder") String folder, @CurrentMember Member member) {
        return ApiResponse.onSuccess(scrapCommandService.createScrapFolder(folder, member));
    }

    @DeleteMapping("/scraps/folders/{folderId}")
    @Operation(summary = "스크랩 폴더 삭제 - 마이페이지", description = "폴더의 스크랩 내역까지 모두 삭제됩니다. 기본 폴더(도서, 공간)는 삭제할 수 없습니다.")
    public ApiResponse<Long> deleteScrapFolder(@PathVariable(name = "folderId") Long folderId, @CurrentMember Member member) {
        return ApiResponse.onSuccess(scrapCommandService.deleteScrapFolder(folderId, member));
    }

    @PostMapping("places/{placeId}/scraps")
    @Operation(summary = "공간 스크랩", description = "폴더가 없는 경우 폴더 생성 후 저장됩니다.")
    @Parameters({
            @Parameter(name = "folder", description = "폴더 이름(ex. 공간)"),
    })
    public ApiResponse<ScrapResponseDTO.PlaceScrapDTO> addScrap(@PathVariable(name = "placeId") Long placeId,
                                                                @RequestParam(name = "folder") String folder, @CurrentMember Member member) {
        return ApiResponse.onSuccess(scrapCommandService.addPlaceScrap(placeId, folder, member));
    }

    @DeleteMapping("/places/{placeId}/scraps")
    @Operation(summary = "공간 스크랩 취소")
    public ApiResponse<ScrapResponseDTO.PlaceScrapDTO> removeScrap(@PathVariable(name = "placeId") Long placeId, @CurrentMember Member member) {
        return ApiResponse.onSuccess(scrapCommandService.removePlaceScrap(placeId, member));
    }

    @PostMapping("books/{bookId}/scraps")
    @Operation(summary = "책 스크랩", description = "폴더가 없는 경우 폴더 생성 후 저장됩니다.")
    @Parameters({
            @Parameter(name = "folder", description = "폴더 이름(ex. 도서)"),
    })
    public ApiResponse<ScrapResponseDTO.BookScrapDTO> addBookScrap(@PathVariable(name = "bookId") Long bookId,
                                                                   @RequestParam(name = "folder") String folder, @CurrentMember Member member) {
        return ApiResponse.onSuccess(scrapCommandService.addBookScrap(bookId, folder, member));
    }

    @DeleteMapping("/books/{bookId}/scraps")
    @Operation(summary = "책 스크랩 취소")
    public ApiResponse<ScrapResponseDTO.BookScrapDTO> removeBookScrap(@PathVariable(name = "bookId") Long bookId, @CurrentMember Member member) {
        return ApiResponse.onSuccess(scrapCommandService.removeBookScrap(bookId, member));
    }

    @DeleteMapping("/scraps/folders/{folderId}/remove")
    @Operation(summary = "스크랩 삭제 - 마이페이지", description = "마이페이지에서 하나 이상의 스크랩 내역을 삭제하고 업데이트된 스크랩 내역을 반환합니다.\n\n" +
            "삭제하려는 스크랩의 id와 type(PLACE 또는 BOOK)을 전달해주세요.\n\n" +
            "스크랩 목록 조회 API 응답의 'id'와 'type'을 사용하여 삭제할 항목을 지정할 수 있습니다.")
    public ApiResponse<ScrapResponseDTO.ScrapListDTO> removeMultipleScraps(@PathVariable(name = "folderId") Long folderId,
                                                                           @Valid @RequestBody ScrapRequestDTO.ScrapRemoveRequestDTO scrapRemoveRequestDTO,
                                                                           @CurrentMember Member member) {
        scrapCommandService.removeScraps(scrapRemoveRequestDTO);
        // 삭제 후 최신 스크랩 목록 반환
        ScrapResponseDTO.ScrapListDTO updatedScrapList = scrapQueryService.getScrapList(
                folderId, 0L, 0L, 10, member);
        return ApiResponse.onSuccess(updatedScrapList);
    }

    @PatchMapping("/scraps/folders/{folderId}")
    @Operation(summary = "스크랩 폴더 이동 - 마이페이지", description = "마이페이지에서 하나 이상의 스크랩 내역을 다른 폴더로 이동합니다.")
    public ApiResponse<Void> moveScrapFolder(@PathVariable(name = "folderId") Long folderId,
                                             @Valid @RequestBody ScrapRequestDTO.ScrapFolderMoveRequestDTO scrapFolderMoveRequestDTO,
                                             @CurrentMember Member member) {
        scrapCommandService.moveScrapFolder(folderId, scrapFolderMoveRequestDTO, member);
        return ApiResponse.onSuccess(null);
    }
}
