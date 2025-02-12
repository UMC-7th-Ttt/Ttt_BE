package com.umc.ttt.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BookScheduler {

    private final BookQueryServiceImpl bookQueryService;

    @Scheduled(initialDelay = 5000, fixedRate = 12 * 60 * 60 * 1000) // 5초 후 실행, 이후 12시간마다 실행
    @Transactional(readOnly = true)
    public void updateRandomBookQuote() {
        bookQueryService.updateRandomBookQuote();
    }
}
