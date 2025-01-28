package com.umc.ttt.domain.book.service;

import com.umc.ttt.domain.book.dto.BookResponseDTO;
import com.umc.ttt.domain.member.entity.Member;

public interface BookQueryService {
    BookResponseDTO.SearchBookResultDTO searchBooks(String keyword, long cursor, int limit, Member member);

    BookResponseDTO.SuggestBooksResultDTO suggestBooksByBookCategory(String categoryName, Member member);

    BookResponseDTO.SuggestBooksResultDTO suggestBooksForUser(Member member);

    BookResponseDTO.GetBookDetailResultDTO getBookDetails(long bookId, Member member);
}