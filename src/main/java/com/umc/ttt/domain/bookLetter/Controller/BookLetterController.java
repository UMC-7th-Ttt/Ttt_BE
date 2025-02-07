package com.umc.ttt.domain.bookLetter.Controller;

import com.umc.ttt.domain.bookLetter.Converter.BookLetterConverter;
import com.umc.ttt.domain.bookLetter.dto.BookLetterRequestDTO;
import com.umc.ttt.domain.bookLetter.dto.BookLetterResponseDTO;
import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import com.umc.ttt.domain.bookLetter.service.BookLetterCommandService;
import com.umc.ttt.domain.bookLetter.validation.annotataion.CheckPage;
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

    // 북레터 작성
    @PostMapping(
            value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @Operation(summary = "북레터 작성(관리자)",description = "작성한 북레터를 저장하는 API입니다.")
    public ApiResponse<BookLetterResponseDTO.CRDResultDTO> addBookLetter(@Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                                             @RequestPart @Valid BookLetterRequestDTO.CUDto request,
                                                                         @RequestPart("bookLetterCover") MultipartFile bookLetterCover) {
        BookLetter bookLetter = bookLetterCommandService.addBookLetter(request, bookLetterCover);
        return ApiResponse.onSuccess(BookLetterConverter.toCRResultDTO(bookLetter));
    }

    // 븍레터 수정
    @PatchMapping("/{bookLetterId}")
    @Operation(summary = "북레터 수정(관리자)",description = "북레터를 수정하는 API입니다.")
    public ApiResponse<BookLetterResponseDTO.CRDResultDTO> modifyBookLetter(
            @PathVariable(name = "bookLetterId") Long bookLetterId,
            @RequestBody @Valid BookLetterRequestDTO.CUDto request){
        BookLetter bookLetter = bookLetterCommandService.updateBookLetter(bookLetterId, request);
        return ApiResponse.onSuccess(BookLetterConverter.toCRResultDTO(bookLetter));
    }

    // 북레터 이미지 수정
    @PatchMapping(
            value = "/{bookLetterId}/coverImg", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @Operation(summary = "북레터 이미지 수정(관리자)",description = "작성한 북레터의 커버 이미지를 수정하는 API입니다.")
    public ApiResponse<BookLetterResponseDTO.CRDResultDTO> modifyCoverImg(@Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                                          @PathVariable(name = "bookLetterId") Long bookLetterId,
                                                                          @RequestPart("bookLetterCover") MultipartFile bookLetterCover){

        BookLetter bookLetter = bookLetterCommandService.updateBookLetterCover(bookLetterId, bookLetterCover);
        return ApiResponse.onSuccess(BookLetterConverter.toCRResultDTO(bookLetter));
    }

    // 북레터 삭제
    @DeleteMapping("/{bookLetterId}")
    @Operation(summary = "북레터 삭제(관리자)",description = "북레터를 삭제하는 API입니다.")
    public ApiResponse<Void> deleteBookLetter(@PathVariable(name = "bookLetterId")Long bookLetterId){
        bookLetterCommandService.deleteBookLetter(bookLetterId);
        return ApiResponse.onSuccess(null);
    }

    // 북레터 리스트
    @GetMapping("/")
    @Operation(summary = "북레터 리스트 조회(관리자)", description = "북레터 리스트를 조회하는 API입니다.")
    public ApiResponse<BookLetterResponseDTO.BookLetterListDTO> getBookLetterPreviewList(@CheckPage @RequestParam(name="page",defaultValue = "1")Integer page){
        Page<BookLetter> bookLetterList = bookLetterCommandService.getBookLetterPreViewList(page-1);
        return ApiResponse.onSuccess(BookLetterConverter.bookLetterListDTO(bookLetterList));
    }

    // 북레터 상세 페이지
    @GetMapping("/{bookLetterId}")
    @Operation(summary = "북레터 상세 조회", description = "특정 북레터의 상세 정보를 조회하는 API입니다.")
    public ApiResponse<BookLetterResponseDTO.BookLetterDTO> getBookLetter(@PathVariable(name = "bookLetterId")Long bookLetterId){
        BookLetter bookLetter = bookLetterCommandService.getBookLetter(bookLetterId);
        return ApiResponse.onSuccess(BookLetterConverter.bookLetterDTO(bookLetter));
    }
}
