package com.umc.ttt.domain.bookClub.service;

import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.home.dto.HomeResponseDTO;

import java.util.List;

public interface BookClubQueryService {
    public BookClubResponseDTO.getBookClubDetailsResultDTO getBookClubDetails(Long bookClubId, Member member);

    public BookClubResponseDTO.getBookClubJoinPageResultDTO getBookClubJoinPageDTO(Long bookClubId, Member member);

    public List<HomeResponseDTO.bookClubDTO> getActiveBookClubs(Long memberId);
}
