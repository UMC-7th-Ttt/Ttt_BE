package com.umc.ttt.domain.bookLetter.service;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.entity.BookFormatCategory;
import com.umc.ttt.domain.book.repository.BookCategoryRepository;
import com.umc.ttt.domain.book.repository.BookFormatCategoryRepository;
import com.umc.ttt.domain.book.repository.BookRepository;
import com.umc.ttt.domain.bookLetter.Converter.BookLetterConverter;
import com.umc.ttt.domain.bookLetter.bookLetterRepository.BookLetterBookRepository;
import com.umc.ttt.domain.bookLetter.bookLetterRepository.BookLetterCategoryRepository;
import com.umc.ttt.domain.bookLetter.bookLetterRepository.BookLetterRepository;
import com.umc.ttt.domain.bookLetter.dto.BookLetterRequestDTO;
import com.umc.ttt.domain.bookLetter.entity.BookLetter;
import com.umc.ttt.domain.bookLetter.entity.BookLetterBook;
import com.umc.ttt.domain.bookLetter.entity.BookLetterCategory;
import com.umc.ttt.domain.bookLetter.handler.BookLetterBookHandler;
import com.umc.ttt.domain.bookLetter.handler.BookLetterCategoryHandler;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.repository.MemberPreferredCategoryRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.handler.BookHandler;
import com.umc.ttt.global.apiPayload.exception.handler.BookLetterHandler;
import com.umc.ttt.home.converter.HomeConverter;
import com.umc.ttt.home.dto.HomeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookLetterCommandServiceImpl implements BookLetterCommandService {
    private final BookLetterRepository bookLetterRepository;
    private final BookRepository bookRepository;
    private final BookLetterBookRepository bookLetterBookRepository;
    private final MemberPreferredCategoryRepository memberPreferredCategoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookFormatCategoryRepository bookFormatCategoryRepository;
    private final BookLetterCategoryRepository bookLetterCategoryRepository;

    // 북레터 추가
    @Override
    @Transactional
    public BookLetter addBookLetter(BookLetterRequestDTO.CRDto request){
        List<Book> books = request.getBooksId().stream()
                .map(bookId -> {
                    return bookRepository.findById(bookId).orElseThrow(()-> new BookHandler(ErrorStatus.BOOK_NOT_FOUND));
                }).collect(Collectors.toList());

        if(books.size() > 5){
            throw new BookLetterHandler(ErrorStatus.BOOKLETTER_BOOKLIST_LIMIT_EXCEEDED);
        }

        BookLetter bookLetter = BookLetterConverter.toBookLetter(request);
        bookLetterRepository.save(bookLetter);

        List<BookLetterBook> bookLetterBooks = BookLetterConverter.toBookLetterBook(books,bookLetter);
        bookLetterBookRepository.saveAll(bookLetterBooks);

        // 북레터 카테고리 저장
        saveCategory(request.getCategoryIdList1(), request.getCategortIDList2(), bookLetter);

        return bookLetter;
    }

    // 카테고리들 저장
    private void saveCategory(List<Long> bookCategoryIdList, List<Long> bookFormatCategoryIdList, BookLetter bookLetter){
        // 카테고리 1
        List<BookCategory> bookCategoryList = Optional.ofNullable(bookCategoryIdList)
                .orElse(Collections.emptyList())
                .stream()
                .map(categoryId -> {
                    return bookCategoryRepository.findById(categoryId).orElseThrow(()->new BookLetterCategoryHandler(ErrorStatus.CATEGORY_NOT_FOUND));
                }).collect(Collectors.toList());
        List<BookLetterCategory> bookLetterCategoryList = BookLetterConverter.toBookLetterCategory1(bookCategoryList, bookLetter);

        // 카테고리 2
        List<BookFormatCategory> bookFormatCategoryList = Optional.ofNullable(bookFormatCategoryIdList)
                .orElse(Collections.emptyList())
                .stream()
                .map(categoryId -> {
                    return bookFormatCategoryRepository.findById(categoryId).orElseThrow(()->new BookLetterCategoryHandler(ErrorStatus.CATEGORY_NOT_FOUND));
                }).collect(Collectors.toList());
        List<BookLetterCategory> bookLetterFormatCategoryList = BookLetterConverter.toBookLetterCategory2(bookFormatCategoryList, bookLetter);

        // 카테고리 합치고 저장
        List<BookLetterCategory> allCategoryList = Stream.concat(bookLetterCategoryList.stream(), bookLetterFormatCategoryList.stream())
                .collect(Collectors.toList());

        bookLetterCategoryRepository.saveAll(allCategoryList);
    }

    // 북레터 수정
    @Override
    @Transactional
    public BookLetter updateBookLetter(Long bookLetterId ,BookLetterRequestDTO.CRDto request){
        BookLetter bookLetter = bookLetterRepository.findById(bookLetterId)
                .orElseThrow(()->new BookLetterHandler(ErrorStatus.BOOKLETTER_NOT_FOUND));

        List<Book> books = request.getBooksId().stream()
                .map(bookId -> {
                    return bookRepository.findById(bookId).orElseThrow(()-> new BookHandler(ErrorStatus.BOOK_NOT_FOUND));
                }).collect(Collectors.toList());

        if(books.size() > 5){
            throw new BookLetterHandler(ErrorStatus.BOOKLETTER_BOOKLIST_LIMIT_EXCEEDED);
        }

        bookLetter.setBookLetterContens(request.getTitle(), request.getSubtitle(), request.getEditor(), request.getContent(), request.getCoverImg());
        bookLetterRepository.save(bookLetter);

        List<BookLetterBook> bookLetterBooks=bookLetter.getBooks();
        bookLetterBooks.get(0).setBook(books.get(0));
        bookLetterBooks.get(1).setBook(books.get(1));
        bookLetterBooks.get(2).setBook(books.get(2));
        bookLetterBooks.get(3).setBook(books.get(3));
        bookLetterBooks.get(4).setBook(books.get(4));
        bookLetterBookRepository.saveAll(bookLetterBooks);

        // 북레터 카테고리 삭제
        bookLetterCategoryRepository.deleteByBookLetter(bookLetter);
        // 북레터 카테고리 저장
        saveCategory(request.getCategoryIdList1(), request.getCategortIDList2(),bookLetter);

        return bookLetter;
    }

    // 북레터 삭제
    @Override
    @Transactional
    public void deleteBookLetter(Long bookLetterId) {
        if(!bookLetterRepository.existsById(bookLetterId)){
            throw new BookLetterHandler(ErrorStatus.BOOKLETTER_NOT_FOUND);
        }
        bookLetterRepository.deleteById(bookLetterId);
    }

    // 북레터 리스트
    @Override
    @Transactional(readOnly = true)
    public Page<BookLetter> getBookLetterPreViewList(Integer page){
        Page<BookLetter> bookLetterPreviewPage = bookLetterRepository.findAll(PageRequest.of(page,10));
        return bookLetterPreviewPage;
    }

    // 특정 북레터 상세 정보 보기
    @Override
    @Transactional(readOnly = true)
    public BookLetter getBookLetter(Long bookLetterId) {
       BookLetter bookLetter = bookLetterRepository.findById(bookLetterId).orElseThrow(()->new BookLetterHandler(ErrorStatus.BOOKLETTER_NOT_FOUND));
        return bookLetter;
    }

    // 홈 (베너 화면)
    @Override
    @Transactional(readOnly = true)
    public List<HomeResponseDTO.mainBannerDTO> getRecentBookLetters() {
        List<BookLetter> bookLetters = bookLetterRepository.findTop3ByOrderByCreatedAtDesc();

        return bookLetters.stream()
                .map(HomeConverter::toMainBannerDTO).collect(Collectors.toList());
    }

    // 홈 (추천 북레터)
    @Override
    @Transactional(readOnly = true)
    public List<HomeResponseDTO.recommendBookLetterDTO> getRecommendBookLetters(Member member) {
        List<BookCategory> preferredBookCategories = memberPreferredCategoryRepository.findBookCategoriesByMemberId(member.getId());
        List<BookFormatCategory> prefferedBookFormats = memberPreferredCategoryRepository.findBookFormatsByMemberId(member.getId());


        Pageable limit = PageRequest.of(0, 5);
        List<BookLetter> recommendBookLeetters = bookLetterRepository.findRandomBookLettersByPreferredCategory(preferredBookCategories, prefferedBookFormats, limit);

        return recommendBookLeetters.stream()
                .map(HomeConverter::toRecommendBookLetterDTO)
                .collect(Collectors.toList());
    }
}