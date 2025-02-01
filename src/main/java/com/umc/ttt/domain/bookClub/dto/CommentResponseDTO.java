package com.umc.ttt.domain.bookClub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {

    @Builder
    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class CommentDTO {
        private Long commentId;
        private String content;
        private Long writerId;
        private String writerNickname;
        private LocalDateTime createdAt;
        private boolean isWriter;
        private boolean isLiked;   // 좋아요 여부
        private int likeCount;  // 좋아요 수
        private int replyCount; // 답글 수
        private Long parentId;  // 부모 댓글 id
        private List<CommentDTO> replies;   // 답글
    }

    @Builder
    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class CommentListDTO {
        private List<CommentDTO> comments;
        private int commentCount;
    }
}
