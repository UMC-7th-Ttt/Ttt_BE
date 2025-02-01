package com.umc.ttt.domain.bookClub.service;

import com.umc.ttt.domain.bookClub.converter.CommentConverter;
import com.umc.ttt.domain.bookClub.dto.CommentRequestDTO;
import com.umc.ttt.domain.bookClub.dto.CommentResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.bookClub.entity.Comment;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import com.umc.ttt.domain.bookClub.handler.BookClubHandler;
import com.umc.ttt.domain.bookClub.repository.BookClubMemberRepository;
import com.umc.ttt.domain.bookClub.repository.CommentRepository;
import com.umc.ttt.domain.bookClub.repository.ReadingRecordRepository;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final ReadingRecordRepository readingRecordRepository;
    private final BookClubMemberRepository bookClubMemberRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public CommentResponseDTO.CommentDTO createComment(Long readingRecordId, CommentRequestDTO.CreateCommentDTO requestDTO, Member member) {
        ReadingRecord readingRecord = readingRecordRepository.findById(readingRecordId)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.READING_RECORD_NOT_FOUND));

        BookClubMember bookClubMember = bookClubMemberRepository.findByBookClubAndMember(readingRecord.getBookClubMember().getBookClub(), member)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.MEMBER_NOT_FOUND_IN_BOOK_CLUB));

        Comment parentComment = null;
        if (requestDTO.getParentCommentId() != null) {
            parentComment = commentRepository.findById(requestDTO.getParentCommentId())
                    .orElseThrow(() -> new BookClubHandler(ErrorStatus.PARENT_COMMENT_NOT_FOUND));
        }

        Comment comment = Comment.builder()
                .content(requestDTO.getContent())
                .bookClubMember(bookClubMember)
                .readingRecord(readingRecord)
                .replies(new ArrayList<>())
                .build();

        // 답글인 경우 부모 댓글의 replies에 답글 추가
        if (parentComment != null) {
            parentComment.addReplies(comment);
        }

        Comment savedComment = commentRepository.save(comment);

        return CommentConverter.toCommentDTO(savedComment, member);
    }

    @Transactional(readOnly = true)
    @Override
    public CommentResponseDTO.CommentListDTO getComments(Long readingRecordId, Member member) {
        ReadingRecord readingRecord = readingRecordRepository.findById(readingRecordId)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.READING_RECORD_NOT_FOUND));

        List<Comment> comments = commentRepository.findByReadingRecordAndParentIsNull(readingRecord);
        List<CommentResponseDTO.CommentDTO> commentDTOs = CommentConverter.toDtoList(comments, member);

        return CommentResponseDTO.CommentListDTO.builder()
                .comments(commentDTOs)
                .commentCount(commentDTOs.size())
                .build();
    }

    @Transactional
    @Override
    public void deleteComment(Long readingRecordId, Long commentId, Member member) {
        ReadingRecord readingRecord = readingRecordRepository.findById(readingRecordId)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.READING_RECORD_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.COMMENT_NOT_FOUND));

        // 댓글 작성자인지 확인
        if (!comment.getBookClubMember().getMember().equals(member)) {
            throw new BookClubHandler(ErrorStatus.NOT_AUTHOR_OF_COMMENT);
        }

        // 답글인 경우 부모 댓글의 답글 목록에서 제거
        if (comment.getParent() != null) {
            comment.getParent().getReplies().remove(comment);
        }

        commentRepository.delete(comment);
    }
}
