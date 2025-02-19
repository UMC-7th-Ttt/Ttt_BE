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
import com.umc.ttt.global.apiPayload.exception.handler.BookClubHandler;
import com.umc.ttt.domain.bookClub.repository.BookClubMemberRepository;
import com.umc.ttt.domain.bookClub.repository.BookClubRepository;
import com.umc.ttt.domain.bookClub.repository.ReadingRecordRepository;
import com.umc.ttt.domain.member.converter.MemberConverter;
import com.umc.ttt.domain.member.dto.MemberResponseDTO;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.scrap.repository.BookScrapRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.domain.home.converter.HomeConverter;
import com.umc.ttt.domain.home.dto.HomeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        List<BookClubResponseDTO.BookClubMemberInfoDTO> bookClubMemberInfoDTOList = bookClubMembers.stream()
                .map(bookClubMember -> {
                    boolean hasReviewed = readingRecordRepository.existsByBookClubMember(bookClubMember);
                    return BookClubConverter.toBookClubMemberInfoDTO(bookClubMember, hasReviewed);
                })
                .collect(Collectors.toList());

        // 사용자 참여 인증 조회
        BookClubMember bookClubMember = bookClubMemberRepository.findByBookClubAndMember(bookClub, member)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.MEMBER_NOT_FOUND_IN_BOOK_CLUB));

        Optional<ReadingRecord> latestReadingRecord = readingRecordRepository.findByBookClubMember(bookClubMember)
                .stream()
                .max(Comparator.comparing(ReadingRecord::getCreatedAt));

        // 오늘 날짜 기준 경과 주 계산
        int elapsedWeeks = calculateElapsedWeeks(bookClub.getStartDate(), 4);

        // 완독률 계산
        int myCompletionRate = latestReadingRecord
                .map(record -> calculateUserCompletionRate(record.getCurrentPage(), book.getItemPage()))
                .orElse(0);
        int recommendedCompletionRate = calculateRecommendedCompletionRate(book.getItemPage(), 4, bookClub.getStartDate());

        return BookClubConverter.toGetBookClubDetailsResultDTO(bookClub, bookInfoDTO, bookClubMemberInfoDTOList, elapsedWeeks, myCompletionRate, recommendedCompletionRate);
    }

    @Override
    public BookClubResponseDTO.getBookClubJoinPageResultDTO getBookClubJoinPageDTO(Long bookClubId, Member member) {
        // BookClub 정보 조회
        BookClub bookClub = bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.BOOK_CLUB_NOT_FOUND));

        // BookClub 모집 현황 조회
        int numberOfMember = bookClubMemberRepository.countByBookClubId(bookClubId);

        // 책 정보 조회
        Book book = bookRepository.findBookByBookClub(bookClub)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.BOOK_NOT_FOUND));
        boolean isScraped = bookScrapRepository.existsByScrapFolderMemberAndBook(member, book);
        BookResponseDTO.GetBookDetailResultDTO getBookDetailResultDTO = BookConverter.toGetBookDetailResultDTO(book, isScraped);

        return BookClubConverter.toGetBookClubJoinPageResultDTO(bookClub, getBookDetailResultDTO);
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

    // 홈 화면에 나의 활동(북클럽)
    public List<HomeResponseDTO.bookClubDTO> getActiveBookClubs(Long memberId){
        List<BookClubMember> bookClubsMember = bookClubMemberRepository.findActiveBookClubsByMember(memberId);

        return bookClubsMember.stream()
                .map(bookClubMember -> {
                    Optional<ReadingRecord> latestReadingRecord = readingRecordRepository.findByBookClubMember(bookClubMember)
                            .stream()
                            .max(Comparator.comparing(ReadingRecord::getCreatedAt));

                    BookClub bookClub = bookClubMember.getBookClub();

                    // 완독률 계산
                    int completionRate = latestReadingRecord
                            .map(record -> calculateUserCompletionRate(record.getCurrentPage(), bookClub.getBookLetterBook().getBook().getItemPage()))
                            .orElse(0);

                    return HomeConverter.toBookClubDTO(bookClub, completionRate);
                }).collect(Collectors.toList());
    }
}