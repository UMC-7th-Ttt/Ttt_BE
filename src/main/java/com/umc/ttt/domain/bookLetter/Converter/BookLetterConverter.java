package com.umc.ttt.domain.bookLetter.Converter;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.bookLetter.dto.BookLetterRequestDTO;
import com.umc.ttt.domain.bookLetter.dto.BookLetterResponseDTO;
import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import com.umc.ttt.domain.bookLetter.entity.BookLetterBook;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class BookLetterConverter {
    public static BookLetterResponseDTO.CRDResultDTO toCRResultDTO(BookLetter bookLetter) {
        return BookLetterResponseDTO.CRDResultDTO.builder()
                .bookLetterId(bookLetter.getId())
                .build();
    }

    public static BookLetter toBookLetter(BookLetterRequestDTO.CRDto request) {
        return BookLetter.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .editor(request.getEditor())
                .content(request.getContent())
                .coverImg(request.getCoverImg())
                .build();
    }

    public static List<BookLetterBook> toBookLetterBook(List<Book> books, BookLetter bookLetter) {
        return books.stream()
                .map(book -> {
                    return BookLetterBook.builder()
                            .bookLetter(bookLetter)
                            .book(book)
                            .build();
                }).collect(Collectors.toList());
    }

    // 북레터 리스트 조회
    public static BookLetterResponseDTO.BookLetterPreViewDTO bookLetterPreViewDTO(BookLetter bookLetter) {
        return BookLetterResponseDTO.BookLetterPreViewDTO.builder()
                .bookLetterId(bookLetter.getId())
                .title(bookLetter.getTitle())
                .editor(bookLetter.getEditor())
                .build();
    }

    public static BookLetterResponseDTO.BookLetterListDTO bookLetterListDTO(Page<BookLetter> bookLetterList) {
        List<BookLetterResponseDTO.BookLetterPreViewDTO> bookLetterPreviewDTOList = bookLetterList.stream()
                .map(BookLetterConverter::bookLetterPreViewDTO).collect(Collectors.toList());

        return BookLetterResponseDTO.BookLetterListDTO.builder()
                .isLastPage(bookLetterList.isLast())
                .isFirstPage(bookLetterList.isFirst())
                .totalPage(bookLetterList.getTotalPages())
                .totalElements(bookLetterList.getTotalElements())
                .listSize(bookLetterPreviewDTOList.size())
                .bookLetterList(bookLetterPreviewDTOList)
                .build();
    }

    // 북레터 상세 보기
    public static BookLetterResponseDTO.BookDTO bookDTO(BookLetterBook book) {
        return BookLetterResponseDTO.BookDTO.builder()
                .bookId(book.getBook().getId())
                .title(book.getBook().getTitle())
                .author(book.getBook().getAuthor())
                .cover(book.getBook().getCover())
                .publisher(book.getBook().getPublisher())
                .itemPage(book.getBook().getItemPage())
                .categoryName(book.getBook().getBookCategory().getCategoryName())
                .hasEbook(book.getBook().getHasEbook())
                .description(book.getBook().getDescription())
                .build();
    }

    public static BookLetterResponseDTO.BookLetterDTO bookLetterDTO(BookLetter bookLetter) {
        List<BookLetterResponseDTO.BookDTO> bookDTOList = bookLetter.getBooks().stream()
                .map(BookLetterConverter::bookDTO).collect(Collectors.toList());

        return BookLetterResponseDTO.BookLetterDTO.builder()
                .title(bookLetter.getTitle())
                .subtitle(bookLetter.getSubtitle())
                .editor(bookLetter.getEditor())
                .isWriter(true) // 추후 관리자 여부 확인 로직되면 수정
                .content(bookLetter.getContent())
                .coverImg(bookLetter.getCoverImg())
                .bookList(bookDTOList)
                .build();
    }
}