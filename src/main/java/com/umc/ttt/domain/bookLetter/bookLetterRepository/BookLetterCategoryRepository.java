package com.umc.ttt.domain.bookLetter.bookLetterRepository;

import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import com.umc.ttt.domain.bookLetter.entity.BookLetterCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookLetterCategoryRepository extends JpaRepository<BookLetterCategory, Long> {
    void deleteByBookLetter(BookLetter bookLetter);
}
