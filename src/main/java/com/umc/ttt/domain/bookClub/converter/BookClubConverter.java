package com.umc.ttt.domain.bookClub.converter;

import com.umc.ttt.domain.book.dto.BookResponseDTO;
import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.bookClub.dto.BookClubRequestDTO;
import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import com.umc.ttt.domain.bookLetter.entity.BookLetterBook;
import com.umc.ttt.domain.member.dto.MemberResponseDTO;
import com.umc.ttt.domain.member.entity.Member;
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
                .participantCount(bookClub.getParticipantCount())
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
                .participantCount(bookClub.getParticipantCount())
                .comment(bookClub.getComment())
                .bookInfo(getBookDetailResultDTO)
                .build();
    }

    public static BookClubMember toJoinBookClub(BookClub bookClub, Member member) {
        return BookClubMember.builder()
                .bookClub(bookClub)
                .member(member)
                .build();
    }

    public static BookClubResponseDTO.joinBookClubResultDTO toJoinBookClubResultDTO(BookClubMember bookClub) {
        return BookClubResponseDTO.joinBookClubResultDTO.builder()
                .id(bookClub.getId())
                .bookClubId(bookClub.getBookClub().getId())
                .build();
    }

    public static BookClubResponseDTO.bookClubDTO toBookClubDTO(BookClub bookClub, int completionRate) {
        return BookClubResponseDTO.bookClubDTO.builder()
                .bookClubId(bookClub.getId())
                .bookId(bookClub.getBookLetterBook().getId())
                .bookTitle(bookClub.getBookLetterBook().getBook().getTitle())
                .bookAuthor(bookClub.getBookLetterBook().getBook().getAuthor())
                .bookCategory(bookClub.getBookLetterBook().getBook().getBookCategory().getCategoryName())
                .bookCover(bookClub.getBookLetterBook().getBook().getCover())
                .completionRate(completionRate)
                .build();
    }

    // 책마다 북클럽 홈 화면
    public static BookClubResponseDTO.getBookClubHomeUserDTO toGetBookClubHomeUserDTO(Long memberId, String profileUrl){
        return BookClubResponseDTO.getBookClubHomeUserDTO.builder()
                .memberId(memberId)
                .profileUrl(profileUrl)
                .build();
    }
    public static BookClubResponseDTO.bookClubMemberRecordDTO toBookClubMemberRecordDTO(ReadingRecord readingRecord) {
        return BookClubResponseDTO.bookClubMemberRecordDTO.builder()
                .recordId(readingRecord.getId())
                .imgUrl(readingRecord.getImgUrl())
                .nickname(readingRecord.getBookClubMember().getMember().getNickname())
                .build();
    }
    public static BookClubResponseDTO.bookClubMemberRecordListDTO toBookClubMemberRecordListDTO(List<ReadingRecord> records, Long nextCursor, int limit, boolean hasNext){
        List<BookClubResponseDTO.bookClubMemberRecordDTO> recordListDTO = records.stream()
                .map(BookClubConverter::toBookClubMemberRecordDTO).collect(Collectors.toList());
        return BookClubResponseDTO.bookClubMemberRecordListDTO.builder()
                .recordList(recordListDTO)
                .nextCursor(nextCursor)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }
    public static BookClubResponseDTO.monthBookClubDTO toMonthBookClubDTO(BookClub bookClub) {
        Book book = bookClub.getBookLetterBook().getBook();
        return BookClubResponseDTO.monthBookClubDTO.builder()
                .bookClubId(bookClub.getId())
                .bookId(book.getId())
                .bookTitle(book.getTitle())
                .author(book.getAuthor())
                .bookCover(book.getCover())
                .bookCategory(book.getBookCategory().getCategoryName())
                .build();
    }
    public static BookClubResponseDTO.getMonthClubListDTO toGetMonthClubResultDTO(int currentMonth, List<BookClub> bookClubs, String nextCursorTitle, int limit, boolean hasNext) {
        List<BookClubResponseDTO.monthBookClubDTO> bookClubListDTO = bookClubs.stream()
                .map(BookClubConverter::toMonthBookClubDTO).collect(Collectors.toList());
        return BookClubResponseDTO.getMonthClubListDTO.builder()
                .currentMonth(currentMonth)
                .bookClubs(bookClubListDTO)
                .nextCursorTitle(nextCursorTitle)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }
}