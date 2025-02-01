package com.umc.ttt.domain.scrap.converter;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.scrap.dto.ScrapResponseDTO;
import com.umc.ttt.domain.scrap.entity.BookScrap;
import com.umc.ttt.domain.scrap.entity.PlaceScrap;
import com.umc.ttt.domain.scrap.entity.ScrapFolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ScrapConverter {

    public static ScrapResponseDTO.PlaceScrapDTO toPlaceScrapDTO(Place place, Member member, boolean isScraped) {
        return ScrapResponseDTO.PlaceScrapDTO.builder()
                .memberId(member.getId())
                .placeId(place.getId())
                .isScraped(isScraped)
                .build();
    }

    public static ScrapResponseDTO.BookScrapDTO toBookScrapDTO(Book book, Member member, boolean isScraped) {
        return ScrapResponseDTO.BookScrapDTO.builder()
                .memberId(member.getId())
                .bookId(book.getId())
                .isScraped(isScraped)
                .build();
    }

    public static ScrapResponseDTO.ScrapFolderDTO toScrapFolderDTO(ScrapFolder scrapFolder) {
        List<Map.Entry<String, LocalDateTime>> imagesWithUpdatedAt = new ArrayList<>();

        if (!scrapFolder.getName().equals("도서")) {
            Optional.ofNullable(scrapFolder.getPlaceScraps()).orElse(Collections.emptyList()).stream()
                    .sorted(Comparator.comparing(PlaceScrap::getUpdatedAt))
                    .limit(4)
                    .forEach(placeScrap -> {
                        String imageUrl = placeScrap.getPlace().getImage();
                        if (imageUrl != null) {
                            imagesWithUpdatedAt.add(new AbstractMap.SimpleEntry<>(imageUrl, placeScrap.getUpdatedAt()));
                        }
                    });
        }

        if (!scrapFolder.getName().equals("공간")) {
            Optional.ofNullable(scrapFolder.getBookScraps()).orElse(Collections.emptyList()).stream()
                    .sorted(Comparator.comparing(BookScrap::getUpdatedAt))
                    .limit(4)
                    .forEach(bookScrap -> {
                        String imageUrl = bookScrap.getBook().getCover();
                        if (imageUrl != null) {
                            imagesWithUpdatedAt.add(new AbstractMap.SimpleEntry<>(imageUrl, bookScrap.getUpdatedAt()));
                        }
                    });
        }

        // 이미지 목록을 updatedAt을 기준으로 정렬, 최대 4개 반환
        List<String> sortedImages = imagesWithUpdatedAt.stream()
                .sorted(Comparator.comparing(Map.Entry<String, LocalDateTime>::getValue))
                .limit(4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return ScrapResponseDTO.ScrapFolderDTO.builder()
                .folderId(scrapFolder.getId())
                .name(scrapFolder.getName())
                .images(sortedImages)
                .build();
    }

    public static ScrapResponseDTO.ScrapFolderListDTO toScrapFolderDTOList(List<ScrapFolder> scrapFolders) {
        List<ScrapResponseDTO.ScrapFolderDTO> scrapFolderDTOList = scrapFolders.stream()
                .map(ScrapConverter::toScrapFolderDTO)
                .collect(Collectors.toList());

        int folderCount = scrapFolderDTOList.size();

        return ScrapResponseDTO.ScrapFolderListDTO.builder()
                .folderCount(folderCount)
                .folders(scrapFolderDTOList)
                .build();
    }

    // 폴더의 스크랩 내역 조회
    public static ScrapResponseDTO.ScrapDTO toScrapDTO(BookScrap bookScrap) {
        return ScrapResponseDTO.ScrapDTO.builder()
                .id(bookScrap.getId())
                .title(bookScrap.getBook().getTitle())
                .authorOrAddress(bookScrap.getBook().getAuthor())
                .image(bookScrap.getBook().getCover())
                .type("BOOK")
                .createdAt(bookScrap.getCreatedAt())
                .build();
    }

    public static ScrapResponseDTO.ScrapDTO toScrapDTO(PlaceScrap placeScrap) {
        return ScrapResponseDTO.ScrapDTO.builder()
                .id(placeScrap.getId())
                .title(placeScrap.getPlace().getTitle())
                .authorOrAddress(placeScrap.getPlace().getAddress())
                .image(placeScrap.getPlace().getImage())
                .type("PLACE")
                .createdAt(placeScrap.getCreatedAt())
                .build();
    }

    public static ScrapResponseDTO.ScrapListDTO toScrapListDTO(List<ScrapResponseDTO.ScrapDTO> scraps, Long nextBookCursor, Long nextPlaceCursor, int limit, boolean hasNext) {
        return ScrapResponseDTO.ScrapListDTO.builder()
                .scraps(scraps)
                .nextBookCursor(nextBookCursor)
                .nextPlaceCursor(nextPlaceCursor)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }
}
