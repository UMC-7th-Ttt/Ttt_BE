package com.umc.ttt.domain.member.service;

import com.umc.ttt.domain.book.entity.Book;
import com.umc.ttt.domain.book.entity.BookCategory;
import com.umc.ttt.domain.book.entity.BookFormatCategory;
import com.umc.ttt.domain.book.repository.BookCategoryRepository;
import com.umc.ttt.domain.book.repository.BookFormatCategoryRepository;
import com.umc.ttt.domain.book.repository.BookRepository;
import com.umc.ttt.domain.member.dto.MemberKeywordDTO;
import com.umc.ttt.domain.member.dto.MemberSignUpDTO;
import com.umc.ttt.domain.member.entity.Member;
import com.umc.ttt.domain.member.entity.MemberPreferredCategory;
import com.umc.ttt.domain.member.entity.enums.ProviderType;
import com.umc.ttt.domain.member.entity.enums.Role;
import com.umc.ttt.domain.member.repository.MemberPreferredCategoryRepository;
import com.umc.ttt.domain.member.repository.MemberRepository;
import com.umc.ttt.domain.place.entity.enums.PlaceCategory;
import com.umc.ttt.domain.scrap.entity.ScrapFolder;
import com.umc.ttt.domain.scrap.repository.ScrapFolderRepository;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.GeneralException;
import com.umc.ttt.global.apiPayload.exception.handler.BookHandler;
import com.umc.ttt.global.apiPayload.exception.handler.JwtHandler;
import com.umc.ttt.global.apiPayload.exception.handler.MemberHandler;
import com.umc.ttt.global.jwt.entity.RefreshToken;
import com.umc.ttt.global.jwt.repository.RefreshTokenRepository;
import com.umc.ttt.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository tokenRepository;
    private final ScrapFolderRepository scrapFolderRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final MemberPreferredCategoryRepository preferredCategoryRepository;
    private final BookRepository bookRepository;
    private final BookFormatCategoryRepository bookFormatCategoryRepository;

    @Override
    public Member signUp(MemberSignUpDTO memberSignUpDto) throws Exception {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberSignUpDto.getEmail());

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (member.getProviderType() == ProviderType.GOOGLE) {
                throw new Exception("이미 존재하는 이메일입니다. 구글 로그인으로 로그인해주세요.");
            } else {
                throw new Exception("이미 존재하는 이메일입니다.");
            }
        }

        if (memberRepository.findByNickname(memberSignUpDto.getNickname()).isPresent()) {
            throw new Exception("이미 존재하는 닉네임입니다.");
        }

        Member member = Member.builder()
                .email(memberSignUpDto.getEmail())
                .password(memberSignUpDto.getPassword())
                .nickname(memberSignUpDto.getNickname())
                .profileUrl(memberSignUpDto.getProfileUrl())
                .providerType(ProviderType.EMAIL)
                .role(Role.GUEST)
                .build();

        member.passwordEncode(passwordEncoder);
        memberRepository.save(member);

        // 기본 스크랩 폴더 생성
        List<ScrapFolder> scrapFolders = Arrays.asList(
                ScrapFolder.builder().name("공간").member(member).build(),
                ScrapFolder.builder().name("도서").member(member).build()
        );

        scrapFolderRepository.saveAll(scrapFolders);

        return member;
    }

    @Override
    public void signOut(Optional<String> userEmail) throws Exception {
        log.info("회원탈퇴 이메일 : {}", userEmail.orElse("이메일 없음"));

        String email = userEmail.get();
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            memberRepository.deleteById(member.get().getId());
        } else {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }
    }

    @Override
    public String refreshAccessToken(String accessToken) throws JwtHandler {
        // 액세스 토큰으로 Refresh 토큰 객체를 조회
        Optional<RefreshToken> refreshTokenOpt = tokenRepository.findByAccessToken(accessToken);

        if (refreshTokenOpt.isEmpty()) {
            throw new JwtHandler(ErrorStatus.INVALID_TOKEN);
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        // RefreshToken 검증
        if (!jwtService.isTokenValid(refreshToken.getRefreshToken())) {
            throw new JwtHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // 새 AccessToken 생성
        String newAccessToken = jwtService.generateAccessToken(refreshToken.getId());

        // AccessToken 업데이트
        refreshToken.updateAccessToken(newAccessToken);
        tokenRepository.save(refreshToken);

        return newAccessToken;
    }

    @Override
    public void isEmailDuplicate(String email) throws MemberHandler {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberHandler(ErrorStatus.MEMBER_ALREADY_EXISTS);
        }
    }

    @Override
    public void isNicknameDuplicate(String nickname) throws Exception {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new MemberHandler(ErrorStatus.NICKNAME_ALREADY_EXISTS);
        }
    }


    /**
     * 장르 키워드 저장 (1,2,4 질문)
     **/
    @Override
    public void saveGenreKeyword(Long memberId, List<String> keywords, Long bookId) throws Exception{
        List<String> selectedCategories = new ArrayList<>();

        // 예를 들어, 책 이름을 이용한 처리가 있을 경우 여기에 추가.
        Book book = bookRepository.findBookById(bookId)
                .orElseThrow(() -> new BookHandler(ErrorStatus.BOOK_NOT_FOUND));

        // preferCategory1에서 상위 2개 선택
        selectedCategories.addAll(extractTopCategories(keywords));
        if (book.getBookCategory() != null && book.getBookCategory().getCategoryName() != null) {
            selectedCategories.add(book.getBookCategory().getCategoryName());
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        for (String categoryName : selectedCategories) {
                BookCategory bookCategory = bookCategoryRepository.findByCategoryName(categoryName)
                        .orElseThrow(() -> new BookHandler(ErrorStatus.CATEGORY_NOT_FOUND));

                if(!preferredCategoryRepository.existsMemberPreferredCategoriesByBookCategoryAndMember_Id(bookCategory,memberId)){
                    MemberPreferredCategory preferredCategory = MemberPreferredCategory.builder()
                            .member(member)
                            .bookCategory(bookCategory)
                            .build();

                    preferredCategoryRepository.save(preferredCategory);
                }
        }


        if(!preferredCategoryRepository.existsMemberPreferredCategoriesByBookCategoryAndMember_Id(book.getBookCategory(),memberId)){
            MemberPreferredCategory preferredCategory = MemberPreferredCategory.builder()
                    .member(member)
                    .bookCategory(book.getBookCategory())
                    .build();

            preferredCategoryRepository.save(preferredCategory);
        }
    }

    /**
     * 분량, 장소 키워드 저장(3번 질문)
     **/
    @Override
    public void saveFormatKeyword(Long memberId, List<String> keywords) throws Exception {
        List<String> selectedCategories = new ArrayList<>();

        // preferCategory1에서 상위 2개 선택
        selectedCategories.addAll(extractTopCategories(keywords));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        for (String categoryName : selectedCategories) {
                BookFormatCategory bookFormatCategory = bookFormatCategoryRepository.findByCategoryName(categoryName)
                        .orElseThrow(() -> new BookHandler(ErrorStatus.CATEGORY_NOT_FOUND));

                if(!preferredCategoryRepository.existsByBookFormatCategoryAndMember_Id(bookFormatCategory,memberId)){
                    MemberPreferredCategory preferredCategory = MemberPreferredCategory.builder()
                            .member(member)
                            .bookFormatCategory(bookFormatCategory)
                            .build();

                    preferredCategoryRepository.save(preferredCategory);
                }

        }

    }

    public List<String> extractTopCategories(List<String> categories) throws Exception{
        Map<String, Integer> frequencyMap = new HashMap<>();

        for (String category : categories) {
            frequencyMap.put(category, frequencyMap.getOrDefault(category, 0) + 1);
        }

        return frequencyMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // 빈도수 정렬
                .limit(2) // 상위 2개 선택
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}

