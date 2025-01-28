package com.umc.ttt.domain.bookClub.service;

import com.umc.ttt.domain.book.converter.BookConverter;
import com.umc.ttt.domain.book.dto.BookResponseDTO;
import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.repository.BookRepository;
import com.umc.ttt.domain.bookClub.converter.BookClubConverter;
import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import com.umc.ttt.domain.bookClub.handler.BookClubHandler;
import com.umc.ttt.domain.bookClub.repository.BookClubMemberRepository;
import com.umc.ttt.domain.bookClub.repository.BookClubRepository;
import com.umc.ttt.domain.bookClub.repository.ReadingRecordRepository;
import com.umc.ttt.domain.member.converter.MemberConverter;
import com.umc.ttt.domain.member.dto.MemberResponseDTO;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.scrap.repository.BookScrapRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookClubQueryServiceImpl implements BookClubQueryService {

    private static final Logger log = LoggerFactory.getLogger(BookClubQueryServiceImpl.class);
    private final BookClubRepository bookClubRepository;
    private final BookClubMemberRepository bookClubMemberRepository;
    private final ReadingRecordRepository readingRecordRepository;
    private final BookRepository bookRepository;
    private final BookScrapRepository bookScrapRepository;

    @Override
    public BookClubResponseDTO.getBookClubDetailsResultDTO getBookClubDetails(Long bookClubId, Member member) {
        // BookClub 정보 조회
        BookClub bookClub = bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.BOOK_CLUB_NOT_FOUND));

        // 책 정보 조회
        Book book = bookRepository.findBookByBookClub(bookClub)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.BOOK_NOT_FOUND));
        boolean isScraped = bookScrapRepository.existsByScrapFolderMemberAndBook(member, book);
        BookResponseDTO.BookInfoDTO bookInfoDTO = BookConverter.toBookInfoDTO(book, isScraped);

        // BookClubMember에서 멤버 리스트 조회
        List<BookClubMember> bookClubMembers = bookClubMemberRepository.findByBookClub(bookClub);

        List<MemberResponseDTO.MemberInfoDTO> memberInfoDTOList = MemberConverter.toMemberInfoListDTO(
                bookClubMembers.stream()
                        .map(BookClubMember::getMember)
                        .toList()
        );

        // 사용자 참여 인증 조회
        BookClubMember bookClubMember = bookClubMemberRepository.findByBookClubAndMember(bookClub, member)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.MEMBER_NOT_FOUND_IN_BOOK_CLUB));
        ReadingRecord readingRecord = readingRecordRepository.findByBookClubMember(bookClubMember)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.READING_RECORED_NOT_FOUND));


        // 오늘 날짜 기준 경과 주 계산
        int elapsedWeeks = calculateElapsedWeeks(bookClub.getStartDate(), 4);

        // 완독률 계산
        int myCompletionRate = calculateUserCompletionRate(readingRecord.getCurrentPage(), book.getItemPage());
        int recommendedCompletionRate = calculateRecommendedCompletionRate(book.getItemPage(), 4, bookClub.getStartDate());

        return BookClubConverter.toGetBookClubDetailsResultDTO(bookClub, bookInfoDTO, memberInfoDTOList, elapsedWeeks, myCompletionRate, recommendedCompletionRate);
    }

    public int calculateElapsedWeeks(LocalDate startDate, int totalWeeks) {
        // 오늘 날짜 기준 경과한 주 계산 (최소 1주)
        long elapsedWeeks = Math.max(1, ChronoUnit.WEEKS.between(startDate, LocalDate.now()));

        return (int) Math.min(elapsedWeeks, totalWeeks);
    }

    public int calculateUserCompletionRate(int userReadPages, int bookTotalPages) {
        return (int) (((double) userReadPages / bookTotalPages) * 100);
    }

    public int calculateRecommendedCompletionRate(int bookTotalPages, int totalWeeks, LocalDate startDate) {
        int elapsedWeeks = calculateElapsedWeeks(startDate, totalWeeks);

        return (int) ((((double) bookTotalPages / totalWeeks) / bookTotalPages) * 100 * elapsedWeeks);
    }
}
