package com.umc.ttt.domain.book.repository;

import com.umc.ttt.domain.book.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    boolean existsByCategoryName(String categoryName);

    Optional<BookCategory> findByCategoryName(String categoryName);
}
