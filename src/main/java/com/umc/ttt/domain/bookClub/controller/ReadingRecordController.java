package com.umc.ttt.domain.bookClub.controller;

import com.umc.ttt.domain.bookClub.converter.ReadingRecordConverter;
import com.umc.ttt.domain.bookClub.dto.ReadingRecordRequestDTO;
import com.umc.ttt.domain.bookClub.dto.ReadingRecordResponseDTO;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import com.umc.ttt.domain.bookClub.service.ReadingRecordService;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.global.annotation.CurrentMember;
import com.umc.ttt.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reading-records")
public class ReadingRecordController {

    private final ReadingRecordService readingRecordService;

    @PostMapping(value = "/book-clubs/{bookClubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "책마다 북클럽 서평 인증하기", description = "책마다 북클럽 서평 인증하기 API입니다.")
    public ApiResponse<ReadingRecordResponseDTO.ReadingRecordResultDTO> createReadingRecord(
            @PathVariable(name = "bookClubId") Long bookClubId,
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @Valid @RequestPart ReadingRecordRequestDTO.ReadingRecordDTO readingRecordDTO,
            @RequestPart(value = "readingRecordPicture", required = true) MultipartFile readingRecordPicture,
            @CurrentMember Member member) {
        ReadingRecord readingRecord = readingRecordService.createReadingRecord(bookClubId, readingRecordDTO, readingRecordPicture, member);
        return ApiResponse.onSuccess(ReadingRecordConverter.toReadingRecordResultDTO(readingRecord));
    }

    @GetMapping
    @Operation(summary = "책마다 북클럽 서평 인증 목록 조회", description = "책마다 북클럽 서평 인증 목록 조회 API입니다.")
    public ApiResponse<ReadingRecordResponseDTO.GetReadingRecordListResultDTO> getReadingRecordList(@CurrentMember Member member) {
        ReadingRecordResponseDTO.GetReadingRecordListResultDTO readingRecord = readingRecordService.getReadingRecordList(member);
        return ApiResponse.onSuccess(readingRecord);
    }

    @GetMapping("/{readingRecordId}")
    @Operation(summary = "책마다 북클럽 서평 인증 상세 조회", description = "책마다 북클럽 서평 인증 상세 조회 API입니다.")
    public ApiResponse<ReadingRecordResponseDTO.GetReadingRecordResultDTO> getReadingRecord(@PathVariable(name = "readingRecordId") Long readingRecordId,
                                                                                            @CurrentMember Member member) {
        ReadingRecordResponseDTO.GetReadingRecordResultDTO readingRecord = readingRecordService.getReadingRecord(readingRecordId);
        return ApiResponse.onSuccess(readingRecord);
    }
}
