package com.umc.ttt.domain.bookLetter.repository;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.bookLetter.entity.BookLetterBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookLetterBookRepository extends JpaRepository<BookLetterBook,Long> {
    @Query("SELECT blb.book FROM BookLetterBook blb WHERE blb.bookLetter.id = :bookLetterId")
    List<Book> findByBookLetterId(Long bookLetterId);
}
