package com.umc.ttt.domain.bookClub.converter;

import com.umc.ttt.domain.bookClub.dto.ReadingRecordRequestDTO;
import com.umc.ttt.domain.bookClub.dto.ReadingRecordResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;

public class ReadingRecordConverter {
    public static ReadingRecord toReadingRecord(ReadingRecordRequestDTO.ReadingRecordDTO request, BookClubMember bookClubMember) {
        return ReadingRecord.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .currentPage(request.getCurrentPage())
                .imgUrl(request.getImgUrl())
                .isSecret(request.getIsSecret())
                .bookClubMember(bookClubMember)
                .build();
    }

    public static ReadingRecordResponseDTO.ReadingRecordResultDTO toReadingRecordResultDTO(ReadingRecord readingRecord) {
        return ReadingRecordResponseDTO.ReadingRecordResultDTO.builder()
                .id(readingRecord.getId())
                .build();
    }
}
