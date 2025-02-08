package com.umc.ttt.domain.home.service;

import com.umc.ttt.domain.home.dto.HomeResponseDTO;
import com.umc.ttt.domain.member.entity.Member;

public interface HomeService {
    HomeResponseDTO.viewHomeResultDTO getHomeData(Member member);
}
