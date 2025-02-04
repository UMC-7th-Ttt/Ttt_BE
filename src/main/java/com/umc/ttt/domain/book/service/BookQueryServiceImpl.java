package com.umc.ttt.domain.book.service;

import com.umc.ttt.domain.book.converter.BookConverter;
import com.umc.ttt.domain.book.dto.BookResponseDTO;
import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.repository.BookRepository;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.entity.MemberPreferredCategory;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.domain.review.entity.Review;
import com.umc.ttt.domain.review.repository.ReviewRepository;
import com.umc.ttt.domain.scrap.repository.BookScrapRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.handler.BookHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookQueryServiceImpl implements BookQueryService {

    private final BookRepository bookRepository;
    private final BookScrapRepository bookScrapRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

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
        List<BookCategory> preferredCategories = member.getPreferredCategories().stream()
                .map(MemberPreferredCategory::getBookCategory)
                .collect(Collectors.toList());

        if (preferredCategories.isEmpty()) {
            throw new BookHandler(ErrorStatus.MEMBER_PREFERRED_CATEGORY_NOT_FOUND);
        }

        List<Book> books = bookRepository.findBooksByCategories(preferredCategories);

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
    public BookResponseDTO.SuggestBooksResultDTO suggestBooksByEditor(Member member) {
        // TODO: 에디터 픽으로 변경
        List<String> titles = Arrays.asList("이처럼 사소한 것들", "급류", "서랍에 저녁을 넣어 두었다 - 2024 노벨문학상 수상작가", "희랍어 시간 - 2024 노벨문학상 수상작가", "너의 유토피아");

        List<Book> books = titles.stream()
                .map(bookRepository::findBookByTitle)
                .flatMap(Optional::stream)
                .toList();

        List<Long> scrapedBookIds = bookScrapRepository.findScrapedBookIdsByMemberAndBooks(member, books);

        return BookConverter.toSuggestBooksResultDTO(books, scrapedBookIds);
    }

    @Override
    public BookResponseDTO.GetBookDetailResultDTO getBookDetails(long bookId, Member member) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookHandler(ErrorStatus.BOOK_NOT_FOUND));

        // 같은 취향 유저 찾기
        List<BookCategory> preferredBookCategories = member.getPreferredCategories().stream()
                .map(MemberPreferredCategory::getBookCategory)
                .filter(Objects::nonNull)
                .toList();

        List<Member> similarMembers = memberRepository.findAll().stream()
                .filter(otherMember -> {
                    List<BookCategory> otherPreferredCategories = otherMember.getPreferredCategories().stream()
                            .map(MemberPreferredCategory::getBookCategory)
                            .filter(Objects::nonNull)
                            .toList();

                    // 공통 bookCategory 개수 계산
                    long matchCount = preferredBookCategories.stream()
                            .filter(otherPreferredCategories::contains)
                            .count();

                    return matchCount >= 2; // 2개 이상 겹치는 경우만 포함
                })
                .toList();

        // 해당 멤버들이 작성한 리뷰 가져오기
        List<Review> filteredReviews = reviewRepository.findAll().stream()
                .filter(review -> similarMembers.contains(review.getMember()))
                .toList();

        // userRating 계산 (리뷰가 없는 경우 0.0 반환)
        double userRating = filteredReviews.isEmpty() ? 0.0 :
                filteredReviews.stream()
                        .mapToDouble(Review::getBookRanking)
                        .average()
                        .orElse(0.0);

        List<Review> allReviews = reviewRepository.findByBookId(bookId).stream()
                .filter(review -> !review.getIsSecret())
                .toList();
        List<BookResponseDTO.ReviewDTO> reviewDTOList = BookConverter.toReviewDTOList(allReviews);

        boolean isScraped = bookScrapRepository.existsByScrapFolderMemberAndBook(member, book);

        return BookConverter.toGetBookDetailPageResultDTO(book, isScraped, userRating, reviewDTOList);
    }
}
