package com.umc.ttt.home.service;

import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.home.dto.HomeResponseDTO;

public interface HomeService {
    HomeResponseDTO.viewHomeResultDTO getHomeData(Member member);
}
