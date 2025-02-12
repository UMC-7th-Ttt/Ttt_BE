package com.umc.ttt.domain.book.converter;

import com.umc.ttt.domain.book.dto.BookFetchDTO;
import com.umc.ttt.domain.book.dto.BookResponseDTO;
import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import com.umc.ttt.domain.member.converter.MemberConverter;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.review.entity.Review;

import java.util.List;
import java.util.stream.Collectors;

public class BookConverter {

    public static Book toEntity(BookFetchDTO.Item item, BookCategory bookCategory) {
        return Book.builder()
                .title(item.getTitle())
                .author(item.getAuthor())
                .isbn(item.getIsbn())
                .cover(item.getCover())
                .description(item.getDescription())
                .publisher(item.getPublisher())
                .bestRank(item.getBestRank())
                .link(item.getLink())
                .itemPage(item.getItemPage())
                .hasEbook(item.getHasEbook())
                .bookCategory(bookCategory)
                .build();
    }

    public static BookResponseDTO.GetBestSellersResultDTO toGetBestSellersResultDTO(List<Book> books) {
        List<BookResponseDTO.BestSellerDTO> BestSellerList = books.stream()
                .map(book -> toBestSellerDTO(book))
                .collect(Collectors.toList());

        return BookResponseDTO.GetBestSellersResultDTO.builder()
                .books(BestSellerList)
                .build();
    }

    public static BookResponseDTO.BestSellerDTO toBestSellerDTO(Book book) {
        return BookResponseDTO.BestSellerDTO.builder()
                .id(book.getId())
                .cover(book.getCover())
                .title(book.getTitle())
                .author(book.getAuthor())
                .category(book.getBookCategory().getCategoryName())
                .publisher(book.getPublisher())
                .mainSentences(book.getMainSentences())
                .build();
    }

    public static BookResponseDTO.SearchBookResultDTO toSearchBooksResultDTO(List<Book> books, long nextCursor, int limit, boolean hasNext, List<Long> scrapedBookIds) {
        List<BookResponseDTO.BookInfoDTO> bookInfoList = books.stream()
                .map(book -> toBookInfoDTO(book, scrapedBookIds.contains(book.getId())))
                .collect(Collectors.toList());

        return BookResponseDTO.SearchBookResultDTO.builder()
                .books(bookInfoList)
                .nextCursor(nextCursor)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }

    public static BookResponseDTO.SuggestBooksByBookCategoryResultDTO toSuggestBooksByBookCategoryResultDTO(List<Book> books, List<Long> scrapedBookIds) {
        List<BookResponseDTO.BookInfoDTO> bookInfoList = books.stream()
                .map(book -> toBookInfoDTO(book, scrapedBookIds.contains(book.getId())))
                .collect(Collectors.toList());

        return BookResponseDTO.SuggestBooksByBookCategoryResultDTO.builder()
                .books(bookInfoList)
                .build();
    }

    public static BookResponseDTO.SuggestBooksForUserResultDTO toSuggestBooksForUserResultDTO(List<Book> books, List<Long> scrapedBookIds, Member member) {
        List<BookResponseDTO.BookInfoDTO> bookInfoList = books.stream()
                .map(book -> toBookInfoDTO(book, scrapedBookIds.contains(book.getId())))
                .collect(Collectors.toList());

        return BookResponseDTO.SuggestBooksForUserResultDTO.builder()
                .memberNickname(member.getNickname())
                .books(bookInfoList)
                .build();
    }

    public static BookResponseDTO.SuggestBooksByEditorResultDTO toSuggestBooksByEditorResultDTO(List<Book> books, List<Long> scrapedBookIds, BookLetter bookLetter) {
        List<BookResponseDTO.BookInfoDTO> bookInfoList = books.stream()
                .map(book -> toBookInfoDTO(book, scrapedBookIds.contains(book.getId())))
                .collect(Collectors.toList());

        return BookResponseDTO.SuggestBooksByEditorResultDTO.builder()
                .bookLetterTitle(bookLetter.getTitle())
                .books(bookInfoList)
                .build();
    }

    public static BookResponseDTO.BookInfoDTO toBookInfoDTO(Book book, boolean isScraped) {
        return BookResponseDTO.BookInfoDTO.builder()
                .id(book.getId())
                .cover(book.getCover())
                .title(book.getTitle())
                .author(book.getAuthor())
                .category(book.getBookCategory().getCategoryName())
                .publisher(book.getPublisher())
                .itemLink(book.getLink())
                .isScraped(isScraped)
                .build();
    }

    public static BookResponseDTO.GetBookDetailResultDTO toGetBookDetailResultDTO(Book book, boolean isScraped) {
        return BookResponseDTO.GetBookDetailResultDTO.builder()
                .id(book.getId())
                .cover(book.getCover())
                .title(book.getTitle())
                .author(book.getAuthor())
                .category(book.getBookCategory().getCategoryName())
                .publisher(book.getPublisher())
                .itemPage(book.getItemPage())
                .description(book.getDescription())
                .hasEbook(book.getHasEbook())
                .itemLink(book.getLink())
                .isScraped(isScraped)
                .totalRating(book.getRating())
                .build();
    }

    public static BookResponseDTO.GetBookDetailResultDTO toGetBookDetailPageResultDTO(Book book, boolean isScraped, double userRating, List<BookResponseDTO.ReviewDTO> reviews) {
        return BookResponseDTO.GetBookDetailResultDTO.builder()
                .id(book.getId())
                .cover(book.getCover())
                .title(book.getTitle())
                .author(book.getAuthor())
                .category(book.getBookCategory().getCategoryName())
                .publisher(book.getPublisher())
                .itemPage(book.getItemPage())
                .description(book.getDescription())
                .hasEbook(book.getHasEbook())
                .isScraped(isScraped)
                .userRating(userRating)
                .totalRating(book.getRating())
                .reviews(reviews)
                .build();
    }

    public static List<BookResponseDTO.ReviewDTO> toReviewDTOList(List<Review> allReviews) {
        return allReviews.stream()
                .map(review -> BookResponseDTO.ReviewDTO.builder()
                        .id(review.getId())
                        .content(review.getContent())
                        .rating(review.getBookRanking())
                        .createdAt(review.getCreatedAt())
                        .memberInfo(MemberConverter.toMemberInfoDTO(review.getMember()))
                        .build())
                .toList();
    }

}
