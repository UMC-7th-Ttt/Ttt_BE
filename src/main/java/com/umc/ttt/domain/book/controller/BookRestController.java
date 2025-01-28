package com.umc.ttt.domain.book.controller;

import com.umc.ttt.domain.book.dto.BookResponseDTO;
import com.umc.ttt.domain.book.service.BookCommandService;
import com.umc.ttt.domain.book.service.BookQueryService;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookRestController {

    private final BookCommandService bookCommandService;
    private final BookQueryService bookQueryService;
    private final MemberRepository memberRepository;

    @PostMapping("/fetch")
    @Operation(summary = "알라딘 Open API 데이터 저장", description = "서버 테스트용 api입니다. 연동x")
    public ApiResponse<String> fetchBooks() {
        bookCommandService.fetchBooks();
        return ApiResponse.onSuccess("알라딘 Open API 데이터가 저장되었습니다.");
    }

    @GetMapping("/search")
    @Operation(summary = "책 검색", description = "책 검색 API이며, 검색 결과는 커서를 기반으로 페이징 처리됩니다.\n\n" +
            "첫 페이지 조회 시 cursor 값으로 0을 전달해주세요.\n\n" +
            "첫 페이지가 아닌 경우 이전 응답의 hasNext가 true일 때, nextCursor 값을 cursor로 전달해주세요.")
    @Parameters({
            @Parameter(name = "keyword", description = "검색 키워드")
    })
    public ApiResponse<BookResponseDTO.SearchBookResultDTO> searchBooks(@RequestParam(value = "keyword", required = true) String keyword,
                                                                        @RequestParam(value = "cursor", required = false, defaultValue = "0") long cursor,
                                                                        @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        BookResponseDTO.SearchBookResultDTO books = bookQueryService.searchBooks(keyword, cursor, limit, member);
        return ApiResponse.onSuccess(books);
    }

    @GetMapping("/search/suggestions")
    @Operation(
            summary = "책 카테고리별 추천 검색어 조회",
            description = "책 카테고리별 추천 검색어 조회 API입니다. query string으로 카테고리 이름을 전달해주세요.\n\n" +
                    "사용 가능한 카테고리 목록:\n" +
                    "- `koreanLiterature`: 한국 문학\n" +
                    "- `humanities`: 인문\n" +
                    "- `selfDevelopment`: 자기계발\n" +
                    "- `essayAndTravel`: 에세이/여행\n" +
                    "- `socialAndNaturalSciences`: 사회/자연과학\n" +
                    "- `worldLiterature`: 세계문학"
    )
    @Parameters({
            @Parameter(name = "categoryName", description = "카테고리 이름")
    })
    public ApiResponse<BookResponseDTO.SuggestBooksResultDTO> suggestBooksByBookCategory(@RequestParam(value = "categoryName", required = true) String categoryName) {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        BookResponseDTO.SuggestBooksResultDTO books = bookQueryService.suggestBooksByBookCategory(categoryName, member);
        return ApiResponse.onSuccess(books);
    }

    @GetMapping("/search/user-suggestions")
    @Operation(summary = "책 사용자별 추천 검색어 조회", description = "책 사용자별 추천 검색어 조회 API이며, 사용자의 취향 키워드를 기반으로 추천됩니다.")
    public ApiResponse<BookResponseDTO.SuggestBooksResultDTO> suggestBooksForUser() {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        BookResponseDTO.SuggestBooksResultDTO books = bookQueryService.suggestBooksForUser(member);
        return ApiResponse.onSuccess(books);
    }

    @GetMapping("/{bookId}")
    @Operation(summary = "책 상세 조회", description = "책 상세 조회 API입니다.")
    public ApiResponse<BookResponseDTO.GetBookDetailResultDTO> getBookDetails(@PathVariable Long bookId) {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        BookResponseDTO.GetBookDetailResultDTO bookDetail = bookQueryService.getBookDetails(bookId, member);
        return ApiResponse.onSuccess(bookDetail);
    }
}
