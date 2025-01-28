package com.umc.ttt.domain.book.service;

import com.umc.ttt.domain.book.converter.BookConverter;
import com.umc.ttt.domain.book.dto.BookResponseDTO;
import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.repository.BookRepository;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.entity.MemberPreferedCategory;
import com.umc.ttt.domain.scrap.repository.BookScrapRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.handler.BookHandler;
import com.umc.ttt.global.apiPayload.exception.handler.PlaceHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookQueryServiceImpl implements BookQueryService {

    private final BookRepository bookRepository;
    private final BookScrapRepository bookScrapRepository;

    @Override
    public BookResponseDTO.SearchBookResultDTO searchBooks(String keyword, long cursor, int limit, Member member) {
        Pageable pageable = PageRequest.of(0, limit + 1);
        List<Book> books = bookRepository.findBooksByKeyword(keyword, cursor, pageable);

        if (books.isEmpty() && cursor != 0) {
            throw new BookHandler(ErrorStatus.PAGE_NOT_FOUND);
        }

        long nextCursor = books.isEmpty() ? null : books.get(books.size() - 1).getId();
        boolean hasNext = books.size() > limit;
        List<Book> paginatedBooks = hasNext ? books.subList(0, limit) : books;
        List<Long> scrapedBookIds = bookScrapRepository.findScrapedBookIdsByMemberAndBooks(member, books);

        return BookConverter.toSearchBooksResultDTO(paginatedBooks, nextCursor, limit, hasNext, scrapedBookIds);
    }

    @Override
    public BookResponseDTO.SuggestBooksResultDTO suggestBooksByBookCategory(String categoryName, Member member) {
        // 카테고리 매핑 정의
        Map<String, List<String>> categoryMapping = Map.of(
                "koreanLiterature", Arrays.asList("판타지", "미스터리", "로맨스", "소설", "시"),
                "humanities", Arrays.asList("역사", "철학", "예술", "인문"),
                "essayAndTravel", Arrays.asList("힐링", "여행", "에세이", "자기계발"),
                "selfDevelopment", Arrays.asList("성장", "트렌드"),
                "socialAndNaturalSciences", Arrays.asList("비즈니스", "심리학", "과학", "공학", "사회과학"),
                "worldLiterature", Arrays.asList("고전", "세계사", "외국문학")
        );

        List<String> bookCategoryNames = categoryMapping.getOrDefault(categoryName, List.of());

        if (bookCategoryNames.isEmpty()) {
            throw new BookHandler(ErrorStatus.CATEGORY_NOT_FOUND);
        }

        List<Book> books = bookRepository.findBooksByBookCategoryNames(bookCategoryNames);

        // 최대 10권을 랜덤으로 선택
        List<Book> randomBooks = books.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        collected -> {
                            Collections.shuffle(collected);
                            return collected.stream().limit(10).toList();
                        }
                ));

        List<Long> scrapedBookIds = bookScrapRepository.findScrapedBookIdsByMemberAndBooks(member, books);

        return BookConverter.toSuggestBooksResultDTO(randomBooks, scrapedBookIds);
    }

    @Override
    public BookResponseDTO.SuggestBooksResultDTO suggestBooksForUser(Member member) {
        List<BookCategory> preferedCategories = member.getPreferedCategories().stream()
                .map(MemberPreferedCategory::getBookCategory)
                .collect(Collectors.toList());

        if (preferedCategories.isEmpty()) {
            throw new BookHandler(ErrorStatus.CATEGORY_NOT_FOUND);
        }

        List<Book> books = bookRepository.findBooksByCategories(preferedCategories);

        // 최대 10권을 랜덤으로 선택
        List<Book> randomBooks = books.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        collected -> {
                            Collections.shuffle(collected);
                            return collected.stream().limit(10).toList();
                        }
                ));

        List<Long> scrapedBookIds = bookScrapRepository.findScrapedBookIdsByMemberAndBooks(member, books);

        return BookConverter.toSuggestBooksResultDTO(randomBooks, scrapedBookIds);
    }

    @Override
    public BookResponseDTO.GetBookDetailResultDTO getBookDetails(long bookId, Member member) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new PlaceHandler(ErrorStatus.BOOK_NOT_FOUND));

        boolean isScraped = bookScrapRepository.existsByScrapFolderMemberAndBook(member, book);

        return BookConverter.toGetBookDetailResultDTO(book, isScraped);
    }
}
