package com.umc.ttt.domain.bookClub.service;

import com.umc.ttt.domain.bookClub.dto.BookClubRequestDTO;
import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface BookClubService {
    BookClub addBookClub(BookClubRequestDTO.AddUpdateDTO request);
    BookClub updateBookClub(Long bookClubId, BookClubRequestDTO.AddUpdateDTO request);
    void deleteBookClub(Long bookClubId);
    Page<BookClub> getBookClubPreViewListForManager(Integer page);
    BookClub getBookClubForManager(Long bookClubId);
    BookClubMember joinBookClub(Long bookClubId, Member member);
    BookClubResponseDTO.bookClubListDTO myBookClubs(Member member);
    BookClubResponseDTO.getMonthClubListDTO getMonthClubResults(String cursor, int limit);
}
