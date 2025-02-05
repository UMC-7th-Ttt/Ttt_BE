package com.umc.ttt.domain.bookLetter.service;

import com.umc.ttt.domain.bookLetter.dto.BookLetterRequestDTO;
import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.home.dto.HomeResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookLetterCommandService {
    BookLetter addBookLetter(BookLetterRequestDTO.CRDto request);
    BookLetter updateBookLetter(Long bookLetterId ,BookLetterRequestDTO.CRDto request);
    void deleteBookLetter(Long bookLetterId);
    Page<BookLetter> getBookLetterPreViewList(Integer page);
    BookLetter getBookLetter(Long bookLetterId);
    List<HomeResponseDTO.mainBannerDTO> getRecentBookLetters();
    List<HomeResponseDTO.recommendBookLetterDTO> getRecommendBookLetters(Member member);
}
