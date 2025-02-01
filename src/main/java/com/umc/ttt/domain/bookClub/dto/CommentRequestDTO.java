package com.umc.ttt.domain.bookClub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCommentDTO {
        @NotNull(message = "내용은 필수입니다.")
        private String content;

        private Long parentCommentId;
    }

}
