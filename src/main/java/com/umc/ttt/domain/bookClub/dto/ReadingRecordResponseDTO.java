package com.umc.ttt.domain.bookClub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ReadingRecordResponseDTO {

    @Builder
    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class ReadingRecordResultDTO {
        private Long id;
    }
}
