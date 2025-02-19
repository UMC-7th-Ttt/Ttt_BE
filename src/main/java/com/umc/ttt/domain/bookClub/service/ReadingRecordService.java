package com.umc.ttt.domain.bookClub.service;

import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.dto.ReadingRecordRequestDTO;
import com.umc.ttt.domain.bookClub.dto.ReadingRecordResponseDTO;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import com.umc.ttt.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

public interface ReadingRecordService {
    public ReadingRecord createReadingRecord(Long bookClubId, ReadingRecordRequestDTO.ReadingRecordDTO request, MultipartFile readingRecordPicture, Member member);

    public ReadingRecordResponseDTO.GetReadingRecordListResultDTO getReadingRecordList(Member member);

    public ReadingRecordResponseDTO.GetReadingRecordResultDTO getReadingRecord(Long readingRecordId);

    // 북클럽 인증 사진들
    BookClubResponseDTO.bookClubMemberRecordListDTO getBookClubMemberRecords(Long cursor, int limit, Member member);
}
