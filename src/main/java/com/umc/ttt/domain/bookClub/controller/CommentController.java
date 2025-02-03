package com.umc.ttt.domain.bookClub.controller;

import com.umc.ttt.domain.bookClub.dto.CommentRequestDTO;
import com.umc.ttt.domain.bookClub.dto.CommentResponseDTO;
import com.umc.ttt.domain.bookClub.service.CommentService;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.global.annotation.CurrentMember;
import com.umc.ttt.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reading-records/{readingRecordId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "댓글/답글 작성", description = "책마다 북클럽 서평에 댓글 또는 답글을 작성합니다.\n\n" +
            "답글을 작성하는 경우에만 parentCommentId를 전달해주세요.")
    public ApiResponse<CommentResponseDTO.CommentDTO> createComment(@PathVariable(name = "readingRecordId") Long readingRecordId,
                                                                    @RequestBody CommentRequestDTO.CreateCommentDTO createCommentDTO,
                                                                    @CurrentMember Member member) {
        return ApiResponse.onSuccess(commentService.createComment(readingRecordId, createCommentDTO, member));
    }

    @GetMapping
    @Operation(summary = "댓글/답글 목록 조회", description = "책마다 북클럽 서평에 달린 댓글 또는 답글을 조회합니다.")
    public ApiResponse<CommentResponseDTO.CommentListDTO> getComments(@PathVariable(name = "readingRecordId") Long readingRecordId, @CurrentMember Member member) {
        return ApiResponse.onSuccess(commentService.getComments(readingRecordId, member));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글/답글 삭제", description = "책마다 북클럽 서평에 달린 댓글 또는 답글을 삭제합니다.\n\n" +
            "댓글 삭제 시 해당 댓글의 답글도 모두 삭제됩니다.")
    public ApiResponse<String> deleteComment(@PathVariable(name = "readingRecordId") Long readingRecordId,
                                             @PathVariable(name = "commentId") Long commentId,
                                             @CurrentMember Member member) {
        commentService.deleteComment(readingRecordId, commentId, member);
        return ApiResponse.onSuccess("댓글/답글이 삭제되었습니다.");
    }

    @PostMapping("/{commentId}/likes")
    @Operation(summary = "댓글/답글 좋아요 추가")
    public ApiResponse<CommentResponseDTO.CommentLikeDTO> addLike(@PathVariable(name = "readingRecordId") Long readingRecordId,
                                                                  @PathVariable(name = "commentId") Long commentId,
                                                                  @CurrentMember Member member) {
        return ApiResponse.onSuccess(commentService.addLike(readingRecordId, commentId, member));
    }

    @DeleteMapping("/{commentId}/likes")
    @Operation(summary = "댓글/답글 좋아요 취소")
    public ApiResponse<Void> deleteLike(@PathVariable(name = "readingRecordId") Long readingRecordId,
                                        @PathVariable(name = "commentId") Long commentId,
                                        @CurrentMember Member member) {
        commentService.deleteLike(readingRecordId, commentId, member);
        return ApiResponse.onSuccess(null);
    }
}
