package com.umc.ttt.domain.bookClub.service;

import com.umc.ttt.domain.bookClub.dto.CommentRequestDTO;
import com.umc.ttt.domain.bookClub.dto.CommentResponseDTO;
import com.umc.ttt.domain.member.entity.Member;

public interface CommentService {
    CommentResponseDTO.CommentDTO createComment(Long readingRecordId, CommentRequestDTO.CreateCommentDTO createCommentDTO, Member member);

    CommentResponseDTO.CommentListDTO getComments(Long readingRecordId, Member member);

    void deleteComment(Long readingRecordId, Long commentId, Member member);

    CommentResponseDTO.CommentLikeDTO addLike(Long readingRecordId, Long commentId, Member member);

    void deleteLike(Long readingRecordId, Long commentId, Member member);
}
