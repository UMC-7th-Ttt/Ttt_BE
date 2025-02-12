package com.umc.ttt.domain.book.service;

import com.umc.ttt.domain.book.converter.BookConverter;
import com.umc.ttt.domain.book.dto.BookResponseDTO;
import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.repository.BookRepository;
import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import com.umc.ttt.domain.bookLetter.repository.BookLetterBookRepository;
import com.umc.ttt.domain.bookLetter.repository.BookLetterRepository;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookQueryServiceImpl implements BookQueryService {

    private final BookRepository bookRepository;
    private final BookScrapRepository bookScrapRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final BookLetterRepository bookLetterRepository;
    private final BookLetterBookRepository bookLetterBookRepository;

    private final AtomicReference<BookResponseDTO.SuggestBookQuotesDTO> cachedBookQuote = new AtomicReference<>();
    private final Random random = new Random();

    @Override
    public BookResponseDTO.SearchBookResultDTO searchBooks(String keyword, long cursor, int limit, Member member) {
        Pageable pageable = PageRequest.of(0, limit + 1);
        List<Book> books = bookRepository.findBooksByKeyword(keyword, cursor, pageable);

        if (books.isEmpty() && cursor != 0) {
            throw new BookHandler(ErrorStatus.PAGE_NOT_FOUND);
        }

        if (books.isEmpty()) {
            return BookConverter.toSearchBooksResultDTO(Collections.emptyList(), -1, limit, false, Collections.emptyList());
        }

        long nextCursor = books.isEmpty() ? null : books.get(books.size() - 1).getId();
        boolean hasNext = books.size() > limit;
        List<Book> paginatedBooks = hasNext ? books.subList(0, limit) : books;
        List<Long> scrapedBookIds = bookScrapRepository.findScrapedBookIdsByMemberAndBooks(member, books);

        return BookConverter.toSearchBooksResultDTO(paginatedBooks, nextCursor, limit, hasNext, scrapedBookIds);
    }

    @Override
    public BookResponseDTO.GetBestSellersResultDTO getBestSellers(Member member) {
        List<Long> bookIds = List.of(11L, 215L, 302L, 372L, 454L, 828L);

        List<Book> books = bookIds.stream()
                .map(id -> bookRepository.findBookById(id))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        return BookConverter.toGetBestSellersResultDTO(books);
    }

    @Override
    public BookResponseDTO.SuggestBookQuotesDTO suggestBookQuotes(Member member) {
        return cachedBookQuote.get();
    }

    // 랜덤으로 책을 선택하여 캐싱
    public void updateRandomBookQuote() {
        List<Long> bookIds = List.of(11L, 215L, 302L, 372L, 454L, 828L);
        Long randomBookId = bookIds.get(random.nextInt(bookIds.size()));

        Book book = bookRepository.findBookById(randomBookId)
                .orElseThrow(() -> new BookHandler(ErrorStatus.BOOK_NOT_FOUND));

        cachedBookQuote.set(BookConverter.toSuggestBookQuotesDTO(book));
    }

    @Override
    public BookResponseDTO.SuggestBooksByBookCategoryResultDTO suggestBooksByBookCategory(String categoryName, Member member) {
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

        List<Long> scrapedBookIds = bookScrapRepository.findScrapedBookIdsByMemberAndBooks(member, randomBooks);

        return BookConverter.toSuggestBooksByBookCategoryResultDTO(randomBooks, scrapedBookIds);
    }

    @Override
    public BookResponseDTO.SuggestBooksForUserResultDTO suggestBooksForUser(Member member) {
        List<MemberPreferredCategory> preferredCategories = member.getPreferredCategories();

        if (preferredCategories.isEmpty()) {
            throw new BookHandler(ErrorStatus.MEMBER_PREFERRED_CATEGORY_NOT_FOUND);
        }

        List<Long> categoryIds = new ArrayList<>();
        List<Long> formatCategoryIds = new ArrayList<>();

        for (MemberPreferredCategory mpc : preferredCategories) {
            if (mpc.getBookCategory() != null) {
                categoryIds.add(mpc.getBookCategory().getId());
            }
            if (mpc.getBookFormatCategory() != null) {
                formatCategoryIds.add(mpc.getBookFormatCategory().getId());
            }
        }

        List<Book> filteredBooks = new ArrayList<>();

        if (!categoryIds.isEmpty()) {
            filteredBooks.addAll(bookRepository.findBooksByCategory(categoryIds));
        }

        List<Book> formatFilteredBooks = new ArrayList<>();

        for (Long formatId : formatCategoryIds) {
            if (formatId == 1) { // 200 페이지 미만
                formatFilteredBooks.addAll(bookRepository.findBooksByPageCount(0, 200));
            } else if (formatId == 2) { // 200 페이지 이상
                formatFilteredBooks.addAll(bookRepository.findBooksByPageCount(200, Integer.MAX_VALUE));
            }
        }

        // bookCategory와 formatCategory 겹치는 경우만 남기기
        if (!categoryIds.isEmpty() && !formatCategoryIds.isEmpty()) {
            filteredBooks.retainAll(formatFilteredBooks);
        } else if (formatCategoryIds.isEmpty()) {
            filteredBooks = formatFilteredBooks;
        }

        // 최대 10권을 랜덤으로 선택
        Collections.shuffle(filteredBooks);
        List<Book> randomBooks = filteredBooks.stream().limit(10).toList();

        List<Long> scrapedBookIds = bookScrapRepository.findScrapedBookIdsByMemberAndBooks(member, randomBooks);

        return BookConverter.toSuggestBooksForUserResultDTO(randomBooks, scrapedBookIds, member);
    }

    @Override
    public BookResponseDTO.SuggestBooksByEditorResultDTO suggestBooksByEditor(Member member) {
        BookLetter randomBookLetter = bookLetterRepository.findRandomBookLetter()
                .orElseThrow(() -> new BookHandler(ErrorStatus.BOOKLETTER_NOT_FOUND));

        List<Book> books = bookLetterBookRepository.findByBookLetterId(randomBookLetter.getId());

        List<Long> scrapedBookIds = bookScrapRepository.findScrapedBookIdsByMemberAndBooks(member, books);

        return BookConverter.toSuggestBooksByEditorResultDTO(books, scrapedBookIds, randomBookLetter);
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
