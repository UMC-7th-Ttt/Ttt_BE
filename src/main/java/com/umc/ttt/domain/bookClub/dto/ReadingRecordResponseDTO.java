package com.umc.ttt.domain.bookClub.dto;

import com.umc.ttt.domain.member.dto.MemberResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public class ReadingRecordResponseDTO {

    @Builder
    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class ReadingRecordResultDTO {
        private Long id;
    }

    @Builder
    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class GetReadingRecordListResultDTO {
        private List<ReadingRecordDTO> readingRecords;
    }

    @Builder
    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class ReadingRecordDTO {
        private Long id;
        private String imgUrl;
        private String memberNickName;
    }

    @Builder
    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class GetReadingRecordResultDTO {
        private Long id;
        private String title;
        private String content;
        private int currentPage;
        private String imgUrl;
        private boolean isSecret;
        private MemberResponseDTO.MemberInfoDTO memberInfo;
    }
}
