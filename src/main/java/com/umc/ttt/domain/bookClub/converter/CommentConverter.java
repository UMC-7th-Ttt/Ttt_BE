package com.umc.ttt.domain.bookClub.converter;

import com.umc.ttt.domain.bookClub.dto.CommentResponseDTO;
import com.umc.ttt.domain.bookClub.entity.Comment;
import com.umc.ttt.domain.bookClub.entity.CommentLike;
import com.umc.ttt.domain.bookClub.repository.CommentLikeRepository;
import com.umc.ttt.domain.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

public class CommentConverter {

    public static CommentResponseDTO.CommentDTO toCommentDTO(Comment comment, Member currentMember, boolean isLiked) {
        return CommentResponseDTO.CommentDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .writerId(comment.getBookClubMember().getId())
                .writerNickname(comment.getBookClubMember().getMember().getNickname())
                .writerProfileImg(comment.getBookClubMember().getMember().getProfileUrl())
                .createdAt(comment.getCreatedAt())
                .isWriter(comment.getBookClubMember().getId().equals(currentMember.getId()))
                .isLiked(isLiked)
                .likeCount(comment.getLikeCount())
                .replyCount(comment.getReplies().size())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .replies(comment.getReplies().stream()
                        .map(reply -> toCommentDTO(reply, currentMember, isLiked))
                        .collect(Collectors.toList()))
                .build();
    }

    public static List<CommentResponseDTO.CommentDTO> toDtoList(
            List<Comment> comments, Member currentMember, CommentLikeRepository commentLikeRepository) {

        return comments.stream()
                .map(comment -> {
                    boolean isLiked = commentLikeRepository.existsByCommentAndBookClubMember(comment,
                            comment.getBookClubMember());
                    return toCommentDTO(comment, currentMember, isLiked);
                })
                .collect(Collectors.toList());
    }

    public static CommentResponseDTO.CommentLikeDTO toCommentLikeDTO(Comment comment, CommentLike commentLike) {
        return CommentResponseDTO.CommentLikeDTO.builder()
                .likeId(commentLike.getId())
                .commentId(comment.getId())
                .bookClubMemberId(commentLike.getBookClubMember().getId())
                .build();
    }
}
