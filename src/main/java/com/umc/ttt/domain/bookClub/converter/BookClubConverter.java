package com.umc.ttt.domain.bookClub.converter;

import com.umc.ttt.domain.book.dto.BookResponseDTO;
import com.umc.ttt.domain.bookClub.dto.BookClubRequestDTO;
import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookLetter.entity.BookLetterBook;
import com.umc.ttt.domain.member.dto.MemberResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class BookClubConverter {
    public static BookClubResponseDTO.AddUpdateResultDTO addUpdateResultDTO(BookClub bookClub) {
        return BookClubResponseDTO.AddUpdateResultDTO.builder()
                .bookClubId(bookClub.getId())
                .build();
    }

    public static BookClub toBookClub(BookClubRequestDTO.AddUpdateDTO request, BookLetterBook bookLetterBook) {
        return BookClub.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .comment(request.getComment())
                .bookLetterBook(bookLetterBook)
                .recruitNumber(request.getRecruitNumber())
                .build();
    }

    // 북클럽 리스트 조회
    public static BookClubResponseDTO.BookClubPreViewDTOForManager bookClubPreViewDTOForManager(BookClub bookClub) {
        return BookClubResponseDTO.BookClubPreViewDTOForManager.builder()
                .bookClubId(bookClub.getId())
                .title(bookClub.getBookLetterBook().getBook().getTitle())
                .build();
    }

    public static BookClubResponseDTO.BookClubListDTOForManager bookClubListDTOForManager(Page<BookClub> bookClubList) {
        List<BookClubResponseDTO.BookClubPreViewDTOForManager> bookClubPreViewDTOList = bookClubList.stream()
                .map(BookClubConverter::bookClubPreViewDTOForManager).collect(Collectors.toList());

        return BookClubResponseDTO.BookClubListDTOForManager.builder()
                .isLastPage(bookClubList.isLast())
                .isFirstPage(bookClubList.isFirst())
                .totalPage(bookClubList.getTotalPages())
                .totalElements(bookClubList.getTotalElements())
                .listSize(bookClubPreViewDTOList.size())
                .bookClubPreViewList(bookClubPreViewDTOList)
                .build();
    }

    // 북레터 상세 보기(관리자)
    public static BookClubResponseDTO.BookClubDTOForManager toBookClubDTOForManager(BookClub bookClub) {
        return BookClubResponseDTO.BookClubDTOForManager.builder()
                .bookLetterId(bookClub.getBookLetterBook().getBookLetter().getId())
                .bookId(bookClub.getBookLetterBook().getBook().getId())
                .title(bookClub.getBookLetterBook().getBook().getTitle())
                .isWriter(true) // 추후 writer인지 확인
                .startDate(bookClub.getStartDate())
                .endDate(bookClub.getEndDate())
                .comment(bookClub.getComment())
                .recuitNumber(bookClub.getRecruitNumber())
                .build();
    }

    public static BookClubResponseDTO.getBookClubDetailsResultDTO toGetBookClubDetailsResultDTO(
            BookClub bookClub, BookResponseDTO.BookInfoDTO bookInfoDTO, List<MemberResponseDTO.MemberInfoDTO> memberInfoDTOList, int elapsedWeeks, int myCompletionRate, int recommendedCompletionRate) {

        return BookClubResponseDTO.getBookClubDetailsResultDTO.builder()
                .bookClubId(bookClub.getId())
                .elapsedWeeks(elapsedWeeks)
                .myCompletionRate(myCompletionRate)
                .recommendedCompletionRate(recommendedCompletionRate)
                .bookInfo(bookInfoDTO)
                .members(memberInfoDTOList)
                .build();
    }

    public static BookClubResponseDTO.getBookClubJoinPageResultDTO toGetBookClubJoinPageResultDTO(BookClub bookClub, BookResponseDTO.GetBookDetailResultDTO getBookDetailResultDTO) {
        return BookClubResponseDTO.getBookClubJoinPageResultDTO.builder()
                .bookClubId(bookClub.getId())
                .startDate(bookClub.getStartDate())
                .endDate(bookClub.getEndDate())
                .recuitNumber(bookClub.getRecruitNumber())
                .bookInfo(getBookDetailResultDTO)
                .build();
    }
}