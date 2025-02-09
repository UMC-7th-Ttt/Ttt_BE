package com.umc.ttt.domain.home.service;

import com.umc.ttt.domain.bookClub.service.BookClubQueryService;
import com.umc.ttt.domain.bookLetter.service.BookLetterCommandService;
import com.umc.ttt.domain.home.converter.HomeConverter;
import com.umc.ttt.domain.home.dto.HomeResponseDTO;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.review.service.ReviewCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService{
    private final BookLetterCommandService bookLetterCommandService;
    private final BookClubQueryService bookClubQueryService;
    private final ReviewCommandService reviewCommandService;

    @Override
    @Transactional(readOnly = true)
    public HomeResponseDTO.viewHomeResultDTO getHomeData(Member member) {
        String nickName = member.getNickname();
        String profileUrl = member.getProfileUrl();

        // 메인 베너 (최근 북레터 3개)
        List<HomeResponseDTO.mainBannerDTO> recentBookLetterDTOList = bookLetterCommandService.getRecentBookLetters();

        // 유저 활동 (활동 중인 모든 북클럽)
        List<HomeResponseDTO.bookClubDTO> activeBookClubDTOList = bookClubQueryService.getActiveBookClubs(member.getId());

        // 맞춤 북레터
        List<HomeResponseDTO.recommendBookLetterDTO> recommentBookLetterDTOList = bookLetterCommandService.getRecommendBookLetters(member);

        // 리마인드 (독서평)
        List<HomeResponseDTO.remindReviewDTO> remindReviewDTOList = reviewCommandService.getRandomReviewsByYear(member);

        return HomeConverter.toViewHomeResultDTO(nickName, profileUrl , recentBookLetterDTOList, activeBookClubDTOList, recommentBookLetterDTOList, remindReviewDTOList);
    }
}
