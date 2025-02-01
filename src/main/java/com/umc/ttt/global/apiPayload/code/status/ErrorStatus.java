package com.umc.ttt.global.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.umc.ttt.global.apiPayload.code.BaseErrorCode;
import com.umc.ttt.global.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 페이지
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "PAGE404", "해당 커서 값의 페이지가 존재하지 않습니다."),

    // 나들이 북클럽
    INVALID_SERVICE_KEY(HttpStatus.UNAUTHORIZED, "PLACE401", "서비스 키가 유효하지 않습니다."),
    SERVICE_URL_UNREACHABLE(HttpStatus.UNAUTHORIZED, "PLACE401", "서비스 주소 호출에 실패했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "PLACE503", "서비스 점검중입니다.(내부 서비스 호출 장애)"),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE404", "장소가 존재하지 않습니다."),

    // 회원
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "MEMBER403", "권한이 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER401", "사용자가 존재하지 않습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER400", "이미 존재하는 사용자입니다."),

    //jwt
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN401", "유효하지 않은 token입니다.."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "LOGIN401", "refreshtoken이 만료되었습니다. 로그인해주세요."),

    //email
    UNAUTHORIZED_EMAIL(HttpStatus.UNAUTHORIZED, "EMAIL401", "본인 인증에 실패했습니다."),

    // 스크랩
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLDER404", "스크랩 폴더가 존재하지 않습니다."),
    INVALID_FOLDER(HttpStatus.BAD_REQUEST, "FOLDER400", "유효하지 않은 폴더 위치입니다."),
    INVALID_FOLDER_TYPE(HttpStatus.BAD_REQUEST, "FOLDER400", "유효하지 않은 폴더 타입입니다."),
    INVALID_FOLDER_MOVE(HttpStatus.FORBIDDEN, "FOLDER403", "동일한 폴더로 이동할 수 없습니다."),
    INVALID_SCRAP_TYPE_FOR_FOLDER(HttpStatus.FORBIDDEN, "FOLDER403", "도서 폴더에는 도서만, 공간 폴더에는 공간만 저장 가능합니다."),
    FOLDER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "FOLDER400", "이미 존재하는 폴더입니다."),
    CANNOT_DELETE_DEFAULT_FOLDER(HttpStatus.FORBIDDEN, "FOLDER403", "기본 폴더는 삭제할 수 없습니다."),
    SCRAP_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "SCRAP400", "이미 스크랩한 도서/공간입니다."),
    SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "SCRAP404", "스크랩 내역이 존재하지 않습니다."),

    // 책 관련 에러
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK404", "책을 찾을 수 없습니다."),

    // 책 카테고리
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKCATEGORY404", "해당 카테고리가 존재하지 않습니다."),

    // 북레터
    BOOKLETTER_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKLETTER401", "북레터를 찾을 수 없습니다."),
    BOOKLETTER_BOOKLIST_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "BOOKLETTER402","북레터의 책은 최대 5권입니다."),
    DUPLICATE_BOOK(HttpStatus.BAD_REQUEST,"BOOKLETTER403", "한 북레터에서 책은 중복될 수 없습니다."),

    // 북레터-책 테이블 연관 에러
    BOOK_LETTER_BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKLETTERBOOK401","존재하지 않는 북레터 북입니다."),
    BOOK_LETTER_BOOK_ALREADY_EXIST(HttpStatus.BAD_REQUEST,"BOOKLETTERBOOK402", "이미 진행되는 북레터 북입니다."),

    // 북클럽 관련
    BOOK_CLUB_NOT_FOUND(HttpStatus.NOT_FOUND,"BOOKCLUB404", "존재하지 않는 북클럽입니다."),

    // 북클럽 멤버
    MEMBER_NOT_FOUND_IN_BOOK_CLUB(HttpStatus.NOT_FOUND,"BOOKCLUB_MEMBER404", "존재하지 않는 북클럽 멤버입니다."),
    BOOK_CLUB_MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"BOOKCLUB_MEMBER401", "이미 가입한 북클럽입니다."),

    // 참여 인증
    READING_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND,"READING_RECORD404", "존재하지 않는 참여 인증입니다."),

    // 참여 인증 댓글 
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"COMMENT404", "존재하지 않는 댓글입니다."),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"COMMENT404", "존재하지 않는 부모 댓글입니다."),
    NOT_AUTHOR_OF_COMMENT(HttpStatus.UNAUTHORIZED,"COMMENT401", "댓글 작성자가 아닙니다."),

    // 페이지 관련
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "PAGE401", "존재하지 않는 페이지입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
