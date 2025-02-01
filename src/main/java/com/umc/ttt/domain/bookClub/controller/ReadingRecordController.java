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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reading-records")
public class ReadingRecordController {

    private final ReadingRecordService readingRecordService;

    @PostMapping("/book-clubs/{bookClubId}")
    @Operation(summary = "책마다 북클럽 서평 인증하기", description = "책마다 북클럽 서평 인증하기 API입니다.")
    public ApiResponse<ReadingRecordResponseDTO.ReadingRecordResultDTO> createReadingRecord(@PathVariable(name = "bookClubId") Long bookClubId,
                                                                                            @RequestBody ReadingRecordRequestDTO.ReadingRecordDTO readingRecordDTO,
                                                                                            @CurrentMember Member member) {
        ReadingRecord readingRecord = readingRecordService.createReadingRecord(bookClubId, readingRecordDTO, member);
        return ApiResponse.onSuccess(ReadingRecordConverter.toReadingRecordResultDTO(readingRecord));
    }

    @GetMapping("/{readingRecordId}")
    @Operation(summary = "책마다 북클럽 서평 인증 상세 조회", description = "책마다 북클럽 서평 인증 상세 조회 API입니다.")
    public ApiResponse<ReadingRecordResponseDTO.GetReadingRecordResultDTO> getReadingRecord(@PathVariable(name = "readingRecordId") Long readingRecordId,
                                                                                            @CurrentMember Member member) {
        ReadingRecordResponseDTO.GetReadingRecordResultDTO readingRecord = readingRecordService.getReadingRecord(readingRecordId);
        return ApiResponse.onSuccess(readingRecord);
    }
}
