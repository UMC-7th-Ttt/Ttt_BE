package com.umc.ttt.domain.bookClub.service;

import com.umc.ttt.domain.bookClub.dto.BookClubRequestDTO;
import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface BookClubService {
    public BookClub addBookClub(BookClubRequestDTO.AddUpdateDTO request);
    public BookClub updateBookClub(Long bookClubId, BookClubRequestDTO.AddUpdateDTO request);
    public void deleteBookClub(Long bookClubId);
    public Page<BookClub> getBookClubPreViewListForManager(Integer page);
    public BookClub getBookClubForManager(Long bookClubId);
    public BookClubMember joinBookClub(Long bookClubId, Member member);
    BookClubResponseDTO.bookClubListDTO myBookClubs(Member member);
}
