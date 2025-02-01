package com.umc.ttt.global.config;

import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.repository.BookCategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BookCategoryInitializer {

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @PostConstruct
    public void initializeCategories() {
        List<CategoryMapping> categoryMappings = Arrays.asList(
                CategoryMapping.builder().name("과학").id(987L).build(),
                CategoryMapping.builder().name("공학").id(51038L).build(),
                CategoryMapping.builder().name("사회과학").id(798L).build(),
                CategoryMapping.builder().name("여행").id(1196L).build(),
                CategoryMapping.builder().name("에세이").id(55889L).build(),
                CategoryMapping.builder().name("외국문학").id(50955L).build(),
                CategoryMapping.builder().name("시").id(50940L).build(),
                CategoryMapping.builder().name("자기계발").id(336L).build(),
                CategoryMapping.builder().name("인문").id(656L).build(),
                CategoryMapping.builder().name("소설").id(1L).build(),
                CategoryMapping.builder().name("판타지").id(50928L).build(),
                CategoryMapping.builder().name("미스터리").id(50926L).build(),
                CategoryMapping.builder().name("고전").id(2105L).build(),
                CategoryMapping.builder().name("성장").id(51235L).build(),
                CategoryMapping.builder().name("심리학").id(51395L).build(),
                CategoryMapping.builder().name("비즈니스").id(2172L).build(),
                CategoryMapping.builder().name("세계사").id(169L).build(),
                CategoryMapping.builder().name("로맨스").id(50935L).build(),
                CategoryMapping.builder().name("힐링").id(70236L).build(),
                CategoryMapping.builder().name("철학").id(51387L).build(),
                CategoryMapping.builder().name("예술").id(517L).build()
        );

        for (CategoryMapping mapping : categoryMappings) {
            if (!bookCategoryRepository.existsByCategoryName(mapping.getName())) {
                BookCategory category = BookCategory.builder()
                        .categoryName(mapping.getName())
                        .id(mapping.getId())
                        .build();
                bookCategoryRepository.save(category);
            }
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    private static class CategoryMapping {
        private final String name;
        private final Long id;
    }
}