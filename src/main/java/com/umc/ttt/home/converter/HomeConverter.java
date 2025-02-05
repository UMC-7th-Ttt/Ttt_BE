package com.umc.ttt.home.converter;

import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import com.umc.ttt.domain.bookLetter.entity.BookLetterBook;
import com.umc.ttt.domain.review.entity.Review;
import com.umc.ttt.home.dto.HomeResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class HomeConverter {
//    메인 베너
    public static HomeResponseDTO.mainBannerDTO toMainBannerDTO(BookLetter bookLetter) {
        return HomeResponseDTO.mainBannerDTO.builder()
                .bookLetterId(bookLetter.getId())
                .title(bookLetter.getTitle())
                .subTitle(bookLetter.getSubtitle())
                .editor(bookLetter.getEditor())
                .coverImg(bookLetter.getCoverImg())
                .build();
    }

//    유저 활동 (북클럽)
    public static HomeResponseDTO.bookClubDTO toBookClubDTO(BookClub bookClub, Integer completionRate) {
        return HomeResponseDTO.bookClubDTO.builder()
                .bookClubId(bookClub.getId())
                .bookId(bookClub.getBookLetterBook().getBook().getId())
                .bookTitle(bookClub.getBookLetterBook().getBook().getTitle())
                .bookCover(bookClub.getBookLetterBook().getBook().getCover())
                .completionRate(completionRate)
                .build();
    }

//    맞춤 북레터
    // 북레터에 담긴 책
    public static HomeResponseDTO.bookLetterBookDTO toBookLetterBookDTO(BookLetterBook bookLetterBook) {
        return HomeResponseDTO.bookLetterBookDTO.builder()
                .bookCoverImg(bookLetterBook.getBook().getCover())
                .build();
    }

    // 북레터
    public static HomeResponseDTO.recommendBookLetterDTO toRecommendBookLetterDTO(BookLetter bookLetter) {
        List<BookLetterBook> bookList = bookLetter.getBooks();
        List<HomeResponseDTO.bookLetterBookDTO> bookListDTO = bookList.stream().map(HomeConverter::toBookLetterBookDTO).collect(Collectors.toList());

        return HomeResponseDTO.recommendBookLetterDTO.builder()
                .bookLetterId(bookLetter.getId())
                .bookLetterTitle(bookLetter.getTitle())
                .bookList(bookListDTO)
                .build();
    }

//    리마인드 (독서평)
    public static HomeResponseDTO.remindReviewDTO toRemindReviewDTO(Review review) {
        return HomeResponseDTO.remindReviewDTO.builder()
                .bookId(review.getBook().getId())
                .bookTitle(review.getBook().getTitle())
                .bookCover(review.getBook().getCover())
                .writeDate(review.getWriteDate())
                .content(truncateReview(review.getContent()))
                .build();
    }

    // 30자 제한
    private static String truncateReview(String content){
        return (content.length()>30)? content.substring(0, 30)+"..." : content;
    }

    public static HomeResponseDTO.viewHomeResultDTO toViewHomeResultDTO(String nickName, String profileUrl, List<HomeResponseDTO.mainBannerDTO> mainBannerList,
                                                                        List<HomeResponseDTO.bookClubDTO> bookClubList,
                                                                        List<HomeResponseDTO.recommendBookLetterDTO> recommendBookLetterList,
                                                                        List<HomeResponseDTO.remindReviewDTO> remindReviewList) {
        return HomeResponseDTO.viewHomeResultDTO.builder()
                .nickname(nickName)
                .profileUrl(profileUrl)
                .mainBannerList(mainBannerList)
                .bookClubList(bookClubList)
                .bookLetterList(recommendBookLetterList)
                .remindReviewList(remindReviewList)
                .build();
    }
}
