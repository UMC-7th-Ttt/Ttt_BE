package com.umc.ttt.domain.bookClub.service;

import com.umc.ttt.domain.bookClub.converter.BookClubConverter;
import com.umc.ttt.domain.bookClub.dto.BookClubRequestDTO;
import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.entity.BookClub;
import com.umc.ttt.domain.bookClub.entity.BookClubMember;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import com.umc.ttt.domain.bookClub.handler.BookClubHandler;
import com.umc.ttt.domain.bookClub.repository.BookClubMemberRepository;
import com.umc.ttt.domain.bookClub.repository.BookClubRepository;
import com.umc.ttt.domain.bookClub.repository.ReadingRecordRepository;
import com.umc.ttt.domain.bookLetter.bookLetterRepository.BookLetterBookRepository;
import com.umc.ttt.domain.bookLetter.entity.BookLetterBook;
import com.umc.ttt.domain.bookLetter.handler.BookLetterBookHandler;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookClubServiceImpl implements BookClubService{
    private final BookClubRepository bookClubRepository;
    private final BookLetterBookRepository bookLetterBookRepository;
    private final BookClubMemberRepository bookClubMemberRepository;
    private final ReadingRecordRepository readingRecordRepository;

    @Override
    @Transactional
    public BookClub addBookClub(BookClubRequestDTO.AddUpdateDTO request) {
        BookLetterBook bookLetterBook = bookLetterBookRepository.findById(request.getBookLetterBookId()).orElseThrow(() -> new BookLetterBookHandler(ErrorStatus.BOOK_LETTER_BOOK_NOT_FOUND));

        boolean existBookClub = bookClubRepository.existsByBookLetterBookId(request.getBookLetterBookId());
        if(existBookClub) {
            throw new BookLetterBookHandler(ErrorStatus.BOOK_LETTER_BOOK_ALREADY_EXIST);
        }
        BookClub bookClub = BookClubConverter.toBookClub(request, bookLetterBook);
        bookClub.setParticipantCount(0);

        return bookClubRepository.save(bookClub);
    }

    @Override
    @Transactional
    public BookClub updateBookClub(Long bookClubId, BookClubRequestDTO.AddUpdateDTO request) {
        BookClub bookClub = bookClubRepository.findById(bookClubId).orElseThrow(()->new BookClubHandler(ErrorStatus.BOOK_CLUB_NOT_FOUND));
        BookLetterBook bookLetterBook = bookLetterBookRepository.findById(request.getBookLetterBookId()).orElseThrow(() -> new BookLetterBookHandler(ErrorStatus.BOOK_LETTER_BOOK_NOT_FOUND));

        boolean existBookLetterBook = bookClubRepository.existsByBookLetterBookId(request.getBookLetterBookId());
        if(existBookLetterBook && !bookClub.getBookLetterBook().getId().equals(request.getBookLetterBookId())) {
            throw new BookLetterBookHandler(ErrorStatus.BOOK_LETTER_BOOK_ALREADY_EXIST);
        }

        if(!bookClub.getBookLetterBook().getId().equals(request.getBookLetterBookId())){
            bookClub.setBookLetterBook(bookLetterBook);
        }
        bookClub.setBookClub(request);

        return bookClubRepository.save(bookClub);
    }

    @Override
    @Transactional
    public void deleteBookClub(Long bookClubId) {
        if(!bookClubRepository.existsById(bookClubId)) {
            throw new BookClubHandler(ErrorStatus.BOOK_CLUB_NOT_FOUND);
        }
        bookClubRepository.deleteById(bookClubId);
    }

    // 북클럽 리스트
    @Override
    @Transactional(readOnly = true)
    public Page<BookClub> getBookClubPreViewListForManager(Integer page) {
        Page<BookClub> bookClub = bookClubRepository.findAll(PageRequest.of(page,10));
        return bookClub;
    }

    // 특정 북클럽 상세 정보 보기
    @Override
    @Transactional(readOnly = true)
    public BookClub getBookClubForManager(Long bookClubId) {
        BookClub bookClub = bookClubRepository.findById(bookClubId).orElseThrow(() -> new BookClubHandler(ErrorStatus.BOOK_CLUB_NOT_FOUND));
        return bookClub;
    }

    @Override
    @Transactional
    public BookClubMember joinBookClub(Long bookClubId, Member member) {
        BookClub bookClub = bookClubRepository.findById(bookClubId).orElseThrow(() -> new BookClubHandler(ErrorStatus.BOOK_CLUB_NOT_FOUND));

        if (bookClubMemberRepository.existsByBookClubAndMember(
                bookClubRepository.getReferenceById(bookClubId), member)) {
            throw new BookClubHandler(ErrorStatus.BOOK_CLUB_MEMBER_ALREADY_EXISTS);
        }

        BookClubMember bookClubMember = BookClubConverter.toJoinBookClub(bookClub, member);
        bookClubMemberRepository.save(bookClubMember);

        int participantCount = bookClubMemberRepository.countByBookClubId(bookClubId);
        bookClub.setParticipantCount(participantCount);
        bookClubRepository.save(bookClub);

        return bookClubMember;
    }

    @Override
    @Transactional(readOnly = true)
    public BookClubResponseDTO.bookClubListDTO myBookClubs(Member member) {
        LocalDate now = LocalDate.now();
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());

        List<BookClub> bookClubs = bookClubRepository.findMyBookClubs(member, now, endOfMonth);

        List<BookClubResponseDTO.bookClubDTO> bookClubDTOs = bookClubs.stream()
                .map(bookClub -> {
                    BookClubMember bookClubMember = bookClubMemberRepository.findByMemberAndBookClub(member, bookClub)
                            .orElseThrow(() -> new BookClubHandler(ErrorStatus.MEMBER_NOT_FOUND_IN_BOOK_CLUB));

                    List<ReadingRecord> readingRecords = readingRecordRepository.findByBookClubMember(bookClubMember);

                    // 가장 최근 참여 인증
                    Optional<ReadingRecord> latestReadingRecord = readingRecords.stream()
                            .max(Comparator.comparing(ReadingRecord::getCreatedAt));

                    // 완독률 계산
                    int completionRate = latestReadingRecord
                            .map(record -> calculateUserCompletionRate(record.getCurrentPage(), bookClub.getBookLetterBook().getBook().getItemPage()))
                            .orElse(0);

                    return BookClubConverter.toBookClubDTO(bookClub, completionRate);
                })
                .collect(Collectors.toList());

        return new BookClubResponseDTO.bookClubListDTO(now.getMonthValue(), bookClubDTOs);
    }

    public int calculateUserCompletionRate(int userReadPages, int bookTotalPages) {
        return (int) (((double) userReadPages / bookTotalPages) * 100);
    }
}
