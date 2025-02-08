package com.umc.ttt.domain.bookClub.controller;

import com.umc.ttt.domain.bookClub.converter.BookClubConverter;
import com.umc.ttt.domain.bookClub.dto.BookClubRequestDTO;
import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.bookClub.service.BookClubQueryService;
import com.umc.ttt.domain.bookClub.service.BookClubService;
import com.umc.ttt.domain.bookClub.service.ReadingRecordService;
import com.umc.ttt.domain.bookLetter.validation.annotataion.CheckPage;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.global.annotation.CurrentMember;
import com.umc.ttt.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-clubs")
public class BookClubController {
    private final BookClubService bookClubService;
    private final BookClubQueryService bookClubQueryService;
    private final ReadingRecordService readingRecordService;

    @GetMapping("/{bookClubId}/details")
    @Operation(summary = "책마다 북클럽 상세 페이지 조회",description = "책마다 북클럽 상세 페이지를 조회하는 API입니다. 책 정보, 사용자 및 권장 완독률, 멤버 리스트를 제공합니다.")
    public ApiResponse<BookClubResponseDTO.getBookClubDetailsResultDTO> getBookClubDetails(@PathVariable(name="bookClubId") Long bookClubId,
                                                                                           @CurrentMember Member member) {
     BookClubResponseDTO.getBookClubDetailsResultDTO bookClub = bookClubQueryService.getBookClubDetails(bookClubId, member);
        return ApiResponse.onSuccess(bookClub);
    }

    @GetMapping("/{bookClubId}/join")
    @Operation(summary = "책마다 북클럽 가입 페이지 조회",description = "책마다 북클럽 가입 페이지를 조회하는 API입니다. 책 정보, 책마다 북클럽 정보를 제공합니다.")
    public ApiResponse<BookClubResponseDTO.getBookClubJoinPageResultDTO> getBookClubJoinPage(@PathVariable(name="bookClubId") Long bookClubId,
                                                                                             @CurrentMember Member member) {
        BookClubResponseDTO.getBookClubJoinPageResultDTO bookClub = bookClubQueryService.getBookClubJoinPageDTO(bookClubId, member);
        return ApiResponse.onSuccess(bookClub);
    }

    @PostMapping("/{bookClubId}/join")
    @Operation(summary = "책마다 북클럽 가입하기",description = "책마다 북클럽 가입하기 API입니다.")
    public ApiResponse<BookClubResponseDTO.joinBookClubResultDTO> joinBookClub(@PathVariable(name="bookClubId") Long bookClubId,
                                                                               @CurrentMember Member member) {
        BookClubMember bookClubMember = bookClubService.joinBookClub(bookClubId, member);
        return ApiResponse.onSuccess(BookClubConverter.toJoinBookClubResultDTO(bookClubMember));
    }

    @GetMapping("/members")
    @Operation(summary = "책마다 북클럽 참여현황", description = "책마다 북클럽 참여현황 API입니다.")
    public ApiResponse<BookClubResponseDTO.bookClubListDTO> myBookClubs(@CurrentMember Member member) {
        return ApiResponse.onSuccess(bookClubService.myBookClubs(member));
    }

    @GetMapping("/home")
    @Operation(summary = "책마다 북클럽 홈 화면(유저 아이디, 프로필)",description = "책마다 북클럽 홈 화면 - 유저 아이디, 프로필 조회하는 API입니다.")
    public ApiResponse<BookClubResponseDTO.getBookClubHomeUserDTO> getBookClubHomeUser(@CurrentMember Member member) {
        return ApiResponse.onSuccess(BookClubConverter.toGetBookClubHomeUserDTO(member.getId(),member.getProfileUrl()));
    }

    @GetMapping("/home/readingRecord")
    @Operation(summary = "책마다 북클럽 홈 화면 (다른 회원들의 북클럽 인증들)",description = "책마다 북클럽 홈 화면 - 다른 회원들의 북클럽 인증들을 조회하는 API입니다.\n\n" +
            "첫 페이지 조회 시 cursor 값으로 0을 전달해주세요.\n\n" +
            "첫 페이지가 아닌 경우 이전 응답의 hasNext가 true일 때, nextCursor 값을 cursor로 전달해주세요.")
    public ApiResponse<BookClubResponseDTO.bookClubMemberRecordListDTO> getBookClubRecordList(@RequestParam(name = "cursor", defaultValue = "0") Long cursor,
                                                                                              @RequestParam(name = "limit", defaultValue = "10") int limit,
                                                                                              @CurrentMember Member member) {
        BookClubResponseDTO.bookClubMemberRecordListDTO response = readingRecordService.getBookClubMemberRecords(cursor, limit, member);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/home/bookClubs")
    @Operation(summary = "책마다 북클럽 (이달의 책마다 북클럽)",description = "책마다 북클럽 홈 화면 - 이달의 책마다 북클럽들을 조회하는 API입니다.\n\n" +
            "첫 페이지 조회 시 cursor 값으로 ㄱ을 전달해주세요.\n\n" +
            "커서 값은 <<<<<책 제목>>>>>입니다\n\n" +
            "첫 페이지가 아닌 경우 이전 응답의 hasNext가 true일 때, nextCursor 값을 cursor로 전달해주세요.")
    public ApiResponse<BookClubResponseDTO.getMonthClubListDTO> getMonthBookClubList(@RequestParam(name = "cursorTitle", defaultValue = "ㄱ") String cursorTitle,
                                                                                     @RequestParam(name = "limit", defaultValue = "10") int limit) {
        BookClubResponseDTO.getMonthClubListDTO response = bookClubService.getMonthClubResults(cursorTitle, limit);
        return ApiResponse.onSuccess(response);
    }
}