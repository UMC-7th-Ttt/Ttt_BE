package com.umc.ttt.domain.bookLetter.dto;

import com.umc.ttt.domain.bookLetter.validation.annotataion.DuplicateBooks;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

public class BookLetterRequestDTO {
    @Getter
    public static class CRDto {
        @DuplicateBooks
        @Size(min = 5, max = 5, message = "한 북레터 당 5권을 제공 필수입니다.")
        List<Long> booksId;

        @NotNull(message = "북레터 제목은 필수입니다.")
        String title;

        @NotNull(message = "북레터 부제목은 필수입니다.")
        String subtitle;

        @NotNull(message = "북레터 에디터는 필수입니다.")
        String editor;

        @NotNull(message = "북레터 콘텐츠 인사말은 필수입니다.")
        String content;

        @NotNull(message = "북레터 표지는 필수입니다.")
        String coverImg;

        @Size(min = 1, message = "첫 번째 카테고리 키워드는 최소 1개입니다.")
        List<Long> categoryIdList1;

        List<Long> categortIDList2;
    }
}
