package com.umc.ttt.domain.bookClub.dto;

import com.umc.ttt.domain.book.dto.BookResponseDTO;
import com.umc.ttt.domain.member.dto.MemberResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class BookClubResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddUpdateResultDTO {
        Long bookClubId;
    }

    // 북클럽 리스트(관리자)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookClubListDTOForManager {
        List<BookClubPreViewDTOForManager> bookClubPreViewList;
        Integer totalPage;
        Integer listSize;
        Long totalElements;
        Boolean isFirstPage;
        Boolean isLastPage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookClubPreViewDTOForManager {
        Long bookClubId;
        String title;
    }

    // 북클럽 상세 페이지(관리자)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookClubDTOForManager {
        Long bookLetterId;
        Long bookId;
        String title;
        Boolean isWriter;
        LocalDate startDate;
        LocalDate endDate;
        String comment;
        int participantCount; // 모집 현황
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getBookClubDetailsResultDTO {
        Long bookClubId;
        Integer elapsedWeeks;
        Integer myCompletionRate;
        Integer recommendedCompletionRate;
        BookResponseDTO.BookInfoDTO bookInfo;
        List<MemberResponseDTO.MemberInfoDTO> members;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getBookClubJoinPageResultDTO {
        Long bookClubId;
        LocalDate startDate;
        LocalDate endDate;
        int participantCount;
        String comment;
        BookResponseDTO.GetBookDetailResultDTO bookInfo;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class joinBookClubResultDTO {
        Long id;
        Long bookClubId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class bookClubDTO {
        Long bookClubId;
        Long bookId;
        String bookTitle;
        String bookAuthor;
        String bookCategory;
        String bookCover;
        Integer completionRate;  // 완독률
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class bookClubListDTO {
        int currentMonth;
        List<bookClubDTO> bookClubs;
    }
}
