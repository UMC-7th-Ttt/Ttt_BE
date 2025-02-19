package com.umc.ttt.domain.book.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.ttt.domain.book.converter.BookConverter;
import com.umc.ttt.domain.book.dto.BookFetchDTO;
import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.repository.BookCategoryRepository;
import com.umc.ttt.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    @Value("${kakao-books.api.base-url}")
    private String kakaoBaseUrl;

    @Value("${kakao-books.api.query-params}")
    private String kakaoQueryParams;

    @Value("${kakao-books.api.ttbkey}")
    private String kakaoTtbkey;

    @Override
    @Transactional
    public void fetchBooks() {
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
                bookRepository.findByIsbn(item.getIsbn()).ifPresentOrElse(
                        existingBook -> {
                            // 기존 ISBN을 ISBN13으로 업데이트
                            existingBook.setIsbn(item.getIsbn13());
                            bookRepository.save(existingBook);
                        },
                        () -> {
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
                );
            }
        }
    }

    @Override
    @Transactional
    public void fetchBooksImage() {
        List<Book> books = bookRepository.findAll();

        if (books.isEmpty()) {
            System.out.println("DB에 저장된 도서가 없습니다.");
            return;
        }

        String kakaoApiBaseUrl = kakaoBaseUrl + kakaoQueryParams;
        String authorizationHeader = "KakaoAK " + kakaoTtbkey;

        for (Book book : books) {
            String isbn = book.getIsbn();
            if (isbn == null || isbn.isEmpty()) {
                continue;
            }

            // 카카오 API 호출
            String apiUrl = kakaoApiBaseUrl + isbn;
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", authorizationHeader);
                headers.set("Content-Type", "application/json");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode root = objectMapper.readTree(response.getBody());

                    JsonNode documents = root.path("documents");
                    if (documents.isArray() && documents.size() > 0) {
                        String thumbnail = documents.get(0).path("thumbnail").asText();
                        if (thumbnail != null && !thumbnail.isEmpty()) {
                            book.setCover(thumbnail);
                            bookRepository.save(book);
                        }
                    } else {
                        System.out.println("카카오 API 응답에 도서 정보가 없습니다: " + book.getTitle());
                    }
                } else {
                    System.out.println("카카오 API 호출 실패: " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.out.println("카카오 API 호출 중 오류 발생 (ISBN: " + isbn + "): " + e.getMessage());
            }
        }
    }
}
