package com.umc.ttt.domain.book.repository;

import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.entity.BookFormatCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookFormatCategoryRepository extends JpaRepository<BookFormatCategory, Long> {
    boolean existsByCategoryName(String categoryName);

    Optional<BookFormatCategory> findByCategoryName(String categoryName);
}
