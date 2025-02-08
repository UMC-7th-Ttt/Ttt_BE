package com.umc.ttt.domain.bookLetter.repository;

import com.umc.ttt.domain.bookLetter.entity.BookLetterBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookLetterBookRepository extends JpaRepository<BookLetterBook,Long> {
}
