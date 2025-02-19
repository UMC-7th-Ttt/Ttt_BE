package com.umc.ttt.domain.bookClub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReadingRecordRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadingRecordDTO {
        @NotNull(message = "제목은 필수입니다.")
        private String title;
        @NotNull(message = "내용은 필수입니다.")
        private String content;
        @NotNull(message = "현재 쪽수는 필수입니다.")
        private int currentPage;
        private boolean isSecret;
    }
}
