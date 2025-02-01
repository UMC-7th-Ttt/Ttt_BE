package com.umc.ttt.domain.bookClub.converter;

import com.umc.ttt.domain.bookClub.dto.ReadingRecordRequestDTO;
import com.umc.ttt.domain.bookClub.dto.ReadingRecordResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import com.umc.ttt.domain.member.dto.MemberResponseDTO;
import com.umc.ttt.domain.member.entity.Member;

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

    public static ReadingRecordResponseDTO.ReadingRecordDTO toReadingRecordDTO(ReadingRecord readingRecord, Member member) {
        return ReadingRecordResponseDTO.ReadingRecordDTO.builder()
                .id(readingRecord.getId())
                .imgUrl(readingRecord.getImgUrl())
                .memberNickName(member.getNickname())
                .build();
    }

    public static ReadingRecordResponseDTO.GetReadingRecordResultDTO toGetReadingRecordResultDTO(ReadingRecord readingRecord, MemberResponseDTO.MemberInfoDTO memberInfoDTO) {
        return ReadingRecordResponseDTO.GetReadingRecordResultDTO.builder()
                .id(readingRecord.getId())
                .title(readingRecord.getTitle())
                .content(readingRecord.getContent())
                .currentPage(readingRecord.getCurrentPage())
                .imgUrl(readingRecord.getImgUrl())
                .isSecret(readingRecord.getIsSecret())
                .memberInfo(memberInfoDTO)
                .build();
    }
}
