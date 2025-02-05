package com.umc.ttt.domain.bookClub.repository;

import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.bookClub.entity.Comment;
import com.umc.ttt.domain.bookClub.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndBookClubMember(Comment comment, BookClubMember bookClubMember);

    boolean existsByCommentAndBookClubMember(Comment comment, BookClubMember bookClubMember);
}
