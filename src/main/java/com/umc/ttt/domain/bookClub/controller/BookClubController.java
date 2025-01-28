package com.umc.ttt.domain.bookClub.controller;

import com.umc.ttt.domain.bookClub.converter.BookClubConverter;
import com.umc.ttt.domain.bookClub.dto.BookClubRequestDTO;
import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookClub.service.BookClubQueryService;
import com.umc.ttt.domain.bookClub.service.BookClubService;
import com.umc.ttt.domain.bookLetter.validation.annotataion.CheckPage;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    @PostMapping("/")
    @Operation(summary = "책마다 북클럽 작성(관리자)",description = "책마다 북클럽을 저장하는 API입니다.")
    public ApiResponse<BookClubResponseDTO.AddUpdateResultDTO> add(@RequestBody @Valid BookClubRequestDTO.AddUpdateDTO request) {
        BookClub bookClub = bookClubService.addBookClub(request);
        return ApiResponse.onSuccess(BookClubConverter.addUpdateResultDTO(bookClub));
    }

    @PatchMapping("/{bookClubId}")
    @Operation(summary = "챌마다 북클럽 수정(관리자)",description = "책마다 북클럽을 수정하는 API입니다.")
    public ApiResponse<BookClubResponseDTO.AddUpdateResultDTO> update(@PathVariable(name="bookClubId") Long bookClubId, @RequestBody @Valid BookClubRequestDTO.AddUpdateDTO request) {
        BookClub bookClub = bookClubService.updateBookClub(bookClubId,request);
        return ApiResponse.onSuccess(BookClubConverter.addUpdateResultDTO(bookClub));
    }

    @DeleteMapping("/{bookClubId}")
    @Operation(summary = "책마다 북클럽 삭제(관리자)",description = "책마다 북클럽을 삭제하는 API입니다.")
    public ApiResponse<Void> deleteBookClub(@PathVariable(name="bookClubId") Long bookClubId){
        bookClubService.deleteBookClub(bookClubId);
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/")
    @Operation(summary = "책마다 북클럽 리스트 조회(관리자)",description = "책마다 북클럽 리스트를 조회하는 API입니다.")
    public ApiResponse<BookClubResponseDTO.BookClubListDTOForManager> getBookClubPreviewListForManager(@CheckPage @RequestParam(name="page",defaultValue = "1")Integer page){
        Page<BookClub> bookClubList = bookClubService.getBookClubPreViewListForManager(page-1);
        return ApiResponse.onSuccess(BookClubConverter.bookClubListDTOForManager(bookClubList));
    }

    @GetMapping("/{bookClubId}")
    @Operation(summary = "책마다 북클럽 상세 조회(관리자)",description = "특정 책마다 북클럽의 상세 정보를 조회하는 API입니다.")
    public ApiResponse<BookClubResponseDTO.BookClubDTOForManager> getBookClubForManager(@PathVariable(name="bookClubId") Long bookClubId){
        BookClub bookClub = bookClubService.getBookClubForManager(bookClubId);
        return ApiResponse.onSuccess(BookClubConverter.toBookClubDTOForManager(bookClub));
    }

    @GetMapping("/{bookClubId}/details")
    @Operation(summary = "책마다 북클럽 상세 페이지 조회",description = "책마다 북클럽 상세 페이지를 조회하는 API입니다. 책 정보, 사용자 및 권장 완독률, 멤버 리스트를 제공합니다.")
    public ApiResponse<BookClubResponseDTO.getBookClubDetailsResultDTO> getBookClubDetails(@PathVariable(name="bookClubId") Long bookClubId) {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        BookClubResponseDTO.getBookClubDetailsResultDTO bookClub = bookClubQueryService.getBookClubDetails(bookClubId, member);
        return ApiResponse.onSuccess(bookClub);
    }

    @GetMapping("/{bookClubId}/join")
    @Operation(summary = "책마다 북클럽 가입하기 페이지 조회",description = "책마다 북클럽 가입하기 페이지를 조회하는 API입니다. 책 정보, 책마다 북클럽 정보를 제공합니다.")
    public ApiResponse<BookClubResponseDTO.getBookClubJoinPageResultDTO> getBookClubJoinPage(@PathVariable(name="bookClubId") Long bookClubId) {
        // TODO: 로그인한 회원 정보로 변경
        Member member = memberRepository.findById(1L).get();
        BookClubResponseDTO.getBookClubJoinPageResultDTO bookClub = bookClubQueryService.getBookClubJoinPageDTO(bookClubId, member);
        return ApiResponse.onSuccess(bookClub);
    }
}