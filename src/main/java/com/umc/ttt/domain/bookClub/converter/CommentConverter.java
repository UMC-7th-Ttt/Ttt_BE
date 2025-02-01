package com.umc.ttt.domain.bookClub.converter;

import com.umc.ttt.domain.bookClub.dto.CommentResponseDTO;
import com.umc.ttt.domain.bookClub.entity.Comment;
import com.umc.ttt.domain.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

public class CommentConverter {

    public static CommentResponseDTO.CommentDTO toCommentDTO(Comment comment, Member currentMember) {
        return CommentResponseDTO.CommentDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .writerId(comment.getBookClubMember().getId())
                .writerNickname(comment.getBookClubMember().getMember().getNickname())
                .createdAt(comment.getCreatedAt())
                .isWriter(comment.getBookClubMember().getId().equals(currentMember.getId()))
                .isLiked(false) // TODO: 좋아요 구현 후 수정
                .likeCount(0)  // TODO: 좋아요 구현 후 수정
                .replyCount(comment.getReplies().size())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .replies(comment.getReplies().stream()
                        .map(reply -> toCommentDTO(reply, currentMember))
                        .collect(Collectors.toList()))
                .build();
    }

    public static List<CommentResponseDTO.CommentDTO> toDtoList(List<Comment> comments, Member currentMember) {
        return comments.stream()
                .map(comment -> toCommentDTO(comment, currentMember))
                .collect(Collectors.toList());
    }
}
