package com.umc.ttt.domain.bookLetter.controller;

import com.umc.ttt.domain.bookLetter.converter.BookLetterConverter;
import com.umc.ttt.domain.bookLetter.dto.BookLetterRequestDTO;
import com.umc.ttt.domain.bookLetter.dto.BookLetterResponseDTO;
import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import com.umc.ttt.domain.bookLetter.service.BookLetterCommandService;
import com.umc.ttt.domain.bookLetter.validation.annotataion.CheckPage;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.global.annotation.CurrentMember;
import com.umc.ttt.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-letters")
public class BookLetterController {
    private final BookLetterCommandService bookLetterCommandService;

    // 북레터 상세 페이지
    @GetMapping("/{bookLetterId}")
    @Operation(summary = "북레터 상세 조회", description = "특정 북레터의 상세 정보를 조회하는 API입니다.")
    public ApiResponse<BookLetterResponseDTO.BookLetterDTO> getBookLetter(@PathVariable(name = "bookLetterId")Long bookLetterId,
                                                                          @CurrentMember Member member){
        BookLetter bookLetter = bookLetterCommandService.getBookLetter(bookLetterId);
        return ApiResponse.onSuccess(BookLetterConverter.bookLetterDTO(bookLetter,member));
    }
}
