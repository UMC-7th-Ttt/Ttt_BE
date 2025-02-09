package com.umc.ttt.domain.bookLetter.repository;

import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.entity.BookFormatCategory;
import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookLetterRepository extends JpaRepository<BookLetter, Long> {
    @Query("SELECT bl FROM BookLetter bl ORDER BY FUNCTION('RAND') LIMIT 1")
    Optional<BookLetter> findRandomBookLetter();

    List<BookLetter> findTop3ByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT blc.bookLetter FROM BookLetterCategory blc " +
            "WHERE blc.bookCategory IN :preferredBookCategories " +
            "OR blc.bookFormatCategory IN :preferredBookFormats " +
            "ORDER BY FUNCTION('RAND')")
    List<BookLetter> findRandomBookLettersByPreferredCategory(@Param("preferredBookCategories") List<BookCategory> preferredBookCategories,
                                                              @Param("preferredBookFormats") List<BookFormatCategory> preferredBookFormats,
                                                              Pageable pageable);
}
