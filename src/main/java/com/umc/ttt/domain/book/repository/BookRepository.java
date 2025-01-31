package com.umc.ttt.domain.book.repository;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.bookClub.entity.BookClub;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findBookByTitle(String title);

    @Query("SELECT b FROM Book b " +
            "JOIN BookLetterBook blb ON b.id = blb.book.id " +
            "WHERE blb.bookClub = :bookClub")
    Optional<Book> findBookByBookClub(BookClub bookClub);

    @Query("""
        SELECT b FROM Book b 
        WHERE (b.title LIKE %:keyword% OR b.author LIKE %:keyword% OR b.bookCategory.categoryName LIKE %:keyword%)
        AND b.id > :cursor 
        ORDER BY b.id ASC
    """)
    List<Book> findBooksByKeyword(String keyword, long cursor, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.bookCategory IN :categories")
    List<Book> findBooksByCategories(List<BookCategory> categories);

    @Query("SELECT b FROM Book b WHERE b.bookCategory.categoryName IN :bookCategoryNames")
    List<Book> findBooksByBookCategoryNames(List<String> bookCategoryNames);
}
