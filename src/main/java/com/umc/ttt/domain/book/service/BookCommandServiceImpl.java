package com.umc.ttt.domain.book.service;

import com.umc.ttt.domain.book.converter.BookConverter;
import com.umc.ttt.domain.book.dto.BookFetchDTO;
import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.repository.BookCategoryRepository;
import com.umc.ttt.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCommandServiceImpl implements BookCommandService {

    private final BookRepository bookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${aladin.api.base-url}")
    private String baseUrl;

    @Value("${aladin.api.query-params}")
    private String queryParams;

    @Value("${aladin.api.item-lookup-url}")
    private String itemLookupUrl;

    @Value("${aladin.api.item-query-params}")
    private String itemQueryParams;

    @Value("${aladin.api.ttbkey}")
    private String ttbkey;

    @Override
    @Transactional
    public void fetchBooks() {
        if (ttbkey == null) {
            throw new RuntimeException("환경변수 ALADIN_TTBKEY가 설정되어 있지 않습니다.");
        }

        int maxResults = 50;
        int startPage = 1;

        // 상품 리스트 API 가져오기
        for (BookCategory category : bookCategoryRepository.findAll()) {
            Long categoryId = category.getId();

            String apiUrl = String.format(baseUrl + queryParams + "&CategoryId=%d", ttbkey, maxResults, startPage, categoryId);

            BookFetchDTO response = restTemplate.getForObject(apiUrl, BookFetchDTO.class);

            if (response == null || response.getItem() == null || response.getItem().isEmpty()) {
                System.out.println("CategoryId " + categoryId + "에 대한 데이터가 없습니다.");
                continue;
            }

            // 상품 조회 API 가져오기
            for (BookFetchDTO.Item item : response.getItem()) {
                if (bookRepository.findByIsbn(item.getIsbn()).isPresent()) {
                    continue;
                }

                String lookupUrl = String.format(itemLookupUrl + itemQueryParams, ttbkey, item.getIsbn());
                BookFetchDTO lookupResponse = restTemplate.getForObject(lookupUrl, BookFetchDTO.class);

                if (lookupResponse != null && lookupResponse.getItem() != null && !lookupResponse.getItem().isEmpty()) {
                    BookFetchDTO.Item lookupItem = lookupResponse.getItem().get(0);

                    item.setItemPage(lookupItem.getItemPage());
                    item.setHasEbook(lookupItem.getHasEbook());
                }

                Book bookEntity = BookConverter.toEntity(item, category);
                bookRepository.save(bookEntity);
            }
        }
    }
}
