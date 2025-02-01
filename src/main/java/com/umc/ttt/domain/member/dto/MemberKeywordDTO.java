package com.umc.ttt.domain.member.dto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberKeywordDTO {

    @NotNull(message = "첫 번째 카테고리 키워드는 필수입니다.")
    private List<String> preferCategory1;

    @NotNull(message = "두 번째 카테고리 키워드는 필수입니다.")
    private List<String> preferCategory2;

    @NotNull(message = "책 ID는 필수입니다.")
    Long preferBookId;
}
