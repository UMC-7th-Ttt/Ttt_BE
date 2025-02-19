package com.umc.ttt.domain.bookClub.service;

import com.umc.ttt.domain.bookClub.converter.BookClubConverter;
import com.umc.ttt.domain.bookClub.converter.ReadingRecordConverter;
import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.dto.ReadingRecordRequestDTO;
import com.umc.ttt.domain.bookClub.dto.ReadingRecordResponseDTO;
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
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.aws.s3.entity.Uuid;
import com.umc.ttt.global.aws.s3.repository.UuidRepository;
import com.umc.ttt.global.aws.s3.service.AmazonS3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.io.Files.getFileExtension;

@Service
@RequiredArgsConstructor
public class ReadingRecordServiceImpl implements ReadingRecordService {

    private final ReadingRecordRepository readingRecordRepository;
    private final BookClubRepository bookClubRepository;
    private final BookClubMemberRepository bookClubMemberRepository;

    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    @Override
    public ReadingRecord createReadingRecord(Long bookClubId, ReadingRecordRequestDTO.ReadingRecordDTO request, MultipartFile readingRecordPicture, Member member) {
        BookClub bookClub = bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.BOOK_CLUB_NOT_FOUND));

        String pictureUrl = null;

        if (readingRecordPicture != null && !readingRecordPicture.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());

            String fileExtension = getFileExtension(readingRecordPicture.getOriginalFilename());

            String s3Key = s3Manager.generateProfileKeyName(savedUuid) + "." + fileExtension;

            pictureUrl = s3Manager.uploadFile(s3Key, readingRecordPicture);
        }

        BookClubMember bookClubMember = bookClubMemberRepository.findByBookClubAndMember(bookClub, member)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.MEMBER_NOT_FOUND_IN_BOOK_CLUB));

        ReadingRecord readingRecord = ReadingRecordConverter.toReadingRecord(request, pictureUrl, bookClubMember);

        return readingRecordRepository.save(readingRecord);
    }

    @Override
    public ReadingRecordResponseDTO.GetReadingRecordListResultDTO getReadingRecordList(Member member) {
        // 사용자가 가입한 북클럽 목록 가져오기
        List<BookClubMember> bookClubMembers = bookClubMemberRepository.findByMember(member);

        if (bookClubMembers.isEmpty()) {
            return new ReadingRecordResponseDTO.GetReadingRecordListResultDTO(Collections.emptyList());
        }

        List<Long> bookClubIds = bookClubMembers.stream()
                .map(bookClubMember -> bookClubMember.getBookClub().getId())
                .collect(Collectors.toList());

        // 해당 북클럽들에서 생성된 가장 최근 인증 10개 조회
        Pageable pageable = PageRequest.of(0, 10);
        List<ReadingRecord> readingRecords = readingRecordRepository.findRecent10ByBookClubIds(bookClubIds, pageable);

        if (readingRecords.isEmpty()) {
            throw new BookClubHandler(ErrorStatus.READING_RECORD_NOT_FOUND);
        }

        List<ReadingRecordResponseDTO.ReadingRecordDTO> readingRecordDTOs = readingRecords.stream().map(readingRecord -> {
            BookClubMember bookClubMember = readingRecord.getBookClubMember();
            if (bookClubMember == null) {
                throw new BookClubHandler(ErrorStatus.MEMBER_NOT_FOUND_IN_BOOK_CLUB);
            }

            Member recordOwner = bookClubMember.getMember();
            if (recordOwner == null) {
                throw new BookClubHandler(ErrorStatus.MEMBER_NOT_FOUND);
            }

            return ReadingRecordConverter.toReadingRecordDTO(readingRecord, recordOwner);
        }).collect(Collectors.toList());

        return new ReadingRecordResponseDTO.GetReadingRecordListResultDTO(readingRecordDTOs);
    }

    @Override
    public ReadingRecordResponseDTO.GetReadingRecordResultDTO getReadingRecord(Long readingRecordId) {
        ReadingRecord readingRecord = readingRecordRepository.findById(readingRecordId)
                .orElseThrow(() -> new BookClubHandler(ErrorStatus.READING_RECORD_NOT_FOUND));

        BookClubMember bookClubMember = readingRecord.getBookClubMember();
        if (bookClubMember == null) {
            throw new BookClubHandler(ErrorStatus.MEMBER_NOT_FOUND_IN_BOOK_CLUB);
        }

        Member recordOwner = bookClubMember.getMember();
        if (recordOwner == null) {
            throw new BookClubHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }
        MemberResponseDTO.MemberInfoDTO memberInfoDTO = MemberConverter.toMemberInfoDTO(recordOwner);

        return ReadingRecordConverter.toGetReadingRecordResultDTO(readingRecord, memberInfoDTO);
    }

    // 북클럽 인증 사진들
    @Override
    @Transactional(readOnly = true)
    public BookClubResponseDTO.bookClubMemberRecordListDTO getBookClubMemberRecords(Long cursor, int limit, Member member) {
        Pageable pageable = PageRequest.of(0, limit);
        Slice<ReadingRecord> records = readingRecordRepository.findReadingRecordsWithCursor(member.getId(), cursor, pageable);
        Long nextCursor = records.hasNext()?records.getContent().get(records.getContent().size()-1).getId() : null;
        return BookClubConverter.toBookClubMemberRecordListDTO(records.getContent(), nextCursor, limit, records.hasNext());
    }
}
