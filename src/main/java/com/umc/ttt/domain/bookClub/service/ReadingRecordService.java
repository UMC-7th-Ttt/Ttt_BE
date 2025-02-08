package com.umc.ttt.domain.bookClub.service;

import com.umc.ttt.domain.bookClub.dto.BookClubResponseDTO;
import com.umc.ttt.domain.bookClub.dto.ReadingRecordRequestDTO;
import com.umc.ttt.domain.bookClub.dto.ReadingRecordResponseDTO;
import com.umc.ttt.domain.bookClub.entity.ReadingRecord;
import com.umc.ttt.domain.member.entity.Member;

public interface ReadingRecordService {
    public ReadingRecord createReadingRecord(Long bookClubId, ReadingRecordRequestDTO.ReadingRecordDTO request, Member member);

    public ReadingRecordResponseDTO.GetReadingRecordListResultDTO getReadingRecordList();

    public ReadingRecordResponseDTO.GetReadingRecordResultDTO getReadingRecord(Long readingRecordId);

    // 북클럽 인증 사진들
    BookClubResponseDTO.bookClubMemberRecordListDTO getBookClubMemberRecords(Long cursor, int limit, Member member);
}
