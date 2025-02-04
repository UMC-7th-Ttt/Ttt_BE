package com.umc.ttt.domain.place.service.impl;

import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.place.entity.enums.PlaceCategory;
import com.umc.ttt.domain.place.repository.PlaceRepository;
import com.umc.ttt.domain.place.service.PlaceApiService;
import com.umc.ttt.global.apiPayload.code.status.ErrorStatus;
import com.umc.ttt.global.apiPayload.exception.handler.PlaceHandler;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PlaceApiServiceImpl implements PlaceApiService {

    @Value("${place.api.bookstore.base-url}")
    private String bookstoreBaseUrl;

    @Value("${place.api.bookstore.key}")
    private String bookstoreKey;

    @Value("${place.api.cafe.base-url}")
    private String cafeBaseUrl;

    @Value("${place.api.cafe.key}")
    private String cafeKey;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;


    private final PlaceRepository placeRepository;

    /**
     * 장소 open API 호출
     */
    @Override
    @Transactional
    public void fetchAndSaveOpenApiData() throws Exception {
        fetchDataForServiceKey(bookstoreBaseUrl, bookstoreKey, PlaceCategory.BOOKSTORE);
        fetchDataForServiceKey(cafeBaseUrl, cafeKey, PlaceCategory.CAFE);
    }

    private void fetchDataForServiceKey(String baseUrl, String serviceKey, PlaceCategory placeCategory) throws Exception {
        if (serviceKey == null || serviceKey.isEmpty()) {
            throw new IllegalStateException("serviceKeys is null or empty");
        }

        int pageNo = 1;
        boolean hasMoreData = true;

        while (hasMoreData) {
            String response = callOpenApi(baseUrl, serviceKey, pageNo);

            // 응답이 비어있으면 종료
            if (response == null || response.contains("\"items\":null")) {
                hasMoreData = false;
                break;
            }

            saveApiResponseToDb(response, placeCategory);
            pageNo++;
        }
    }

    private String callOpenApi(String baseUrl, String serviceKey, int pageNo) throws Exception {
        // URL 빌드
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", 20) // 한 페이지당 가져올 데이터 개수
                .build(false)
                .toUriString();

        // HTTP 요청
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept","application/json");

        BufferedReader rd;
        int responseCode = conn.getResponseCode();

        if (responseCode == 401) {
            throw new PlaceHandler(ErrorStatus.INVALID_SERVICE_KEY);
        }

        if(responseCode >= 200 && responseCode <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
            System.out.println(line);
        }
        rd.close();
        conn.disconnect();
        System.out.println(sb.toString());

        return sb.toString();
    }

    private void saveApiResponseToDb(String response, PlaceCategory placeCategory) {
        JSONObject jsonResponse = new JSONObject(response);
        JSONObject responseHeader = jsonResponse.getJSONObject("response").getJSONObject("header");
        String resultCode = responseHeader.getString("resultCode");
        String resultMsg = responseHeader.getString("resultMsg");

        if ("0000".equals(resultCode)) {
            JSONObject body = jsonResponse.getJSONObject("response").getJSONObject("body");

            if (body.has("items")) {
                JSONObject itemsObject = body.getJSONObject("items");

                if (itemsObject.has("item")) {
                    JSONArray itemArray = itemsObject.getJSONArray("item");

                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject item = itemArray.getJSONObject(i);

                        // '만화책' 또는 '만화방'이 포함되어 있으면 저장x
                        String category = item.optString("SUBJECT_KEYWORD", "");
                        if (category.contains("만화책") || category.contains("만화방")) {
                            continue;
                        }

                        String title = item.optString("TITLE", null);
                        String address = item.optString("ADDRESS", null).replaceAll("\\(.*\\)", "").trim();  // 우편번호 제외
                        String phone = item.optString("CONTACT_POINT", null);
                        String coordinates = item.optString("COORDINATES", null);

                        // 위도, 경도로 나누기
                        String normalizedCoordinates = coordinates.replace(",", " ").trim();
                        String[] latLong = normalizedCoordinates.split("\\s+");
                        double latitude = Double.parseDouble(latLong[0].trim());
                        double longitude = Double.parseDouble(latLong[1].trim());

                        // description 파싱
                        String description = item.optString("DESCRIPTION", null);
                        Map<String, String> businessHours = parseDescription(description);

                        // subDescription 파싱
                        String subDescription = item.optString("SUB_DESCRIPTION", null);
                        Map<String, Boolean> features = parseSubDescription(subDescription, placeCategory);

                        // DB에 저장
                        Place place = Place.builder()
                                .title(title)
                                .category(placeCategory)
                                .address(address)
                                .xPos(latitude)
                                .yPos(longitude)
                                .holiday(businessHours.get("holiday"))
                                .weekdaysBusiness(businessHours.get("weekdaysBusiness"))
                                .satBusiness(businessHours.get("satBusiness"))
                                .sunBusiness(businessHours.get("sunBusiness"))
                                .phone(phone)
                                .hasParking(features.get("hasParking"))
                                .hasCafe(features.get("hasCafe"))
                                .hasIndiePub(features.get("hasIndiePub"))
                                .hasBookClub(features.get("hasBookClub"))
                                .hasSpaceRental(features.get("hasSpaceRental"))
                                .rating(0.0)
                                .build();

                        placeRepository.save(place);
                    }
                }
            }
        } else {
            if ("F2013".equals(resultCode)) {
                throw new PlaceHandler(ErrorStatus.SERVICE_URL_UNREACHABLE);
            } else if ("9999".equals(resultCode)) {
                throw new PlaceHandler(ErrorStatus.SERVICE_UNAVAILABLE);
            } else {
                System.err.println("Error: " + resultCode + " - " + resultMsg);
            }
        }
    }

    // description 파싱
    private Map<String, String> parseDescription(String description) {
        Map<String, String> businessHours = new HashMap<>();

        businessHours.put("weekdaysBusiness", null);
        businessHours.put("satBusiness", null);
        businessHours.put("sunBusiness", null);
        businessHours.put("holiday", null);

        if (description == null || description.isEmpty()) {
            return businessHours;
        }

        String weekdaysPattern = "평일개점마감시간\\s*:\\s*(\\d{2}:\\d{2})~(\\d{2}:\\d{2})";
        String satPattern = "토요일개점마감시간\\s*:\\s*(\\d{2}:\\d{2})~(\\d{2}:\\d{2})";
        String sunPattern = "일요일개점마감시간\\s*:\\s*(\\d{2}:\\d{2})~(\\d{2}:\\d{2})";
        String holidayPattern = "휴무일\\s*[:：]\\s*(.*)"; // '휴무일: ' 이후의 모든 텍스트

        // 평일 시간 추출
        Pattern weekdayPattern = Pattern.compile(weekdaysPattern);
        Matcher weekdayMatcher = weekdayPattern.matcher(description);
        if (weekdayMatcher.find()) {
            businessHours.put("weekdaysBusiness", weekdayMatcher.group(1) + " - " + weekdayMatcher.group(2));
        }

        // 토요일 시간 추출
        Pattern satPatternObj = Pattern.compile(satPattern);
        Matcher satMatcher = satPatternObj.matcher(description);
        if (satMatcher.find()) {
            businessHours.put("satBusiness", satMatcher.group(1) + " - " + satMatcher.group(2));
        }

        // 일요일 시간 추출
        Pattern sunPatternObj = Pattern.compile(sunPattern);
        Matcher sunMatcher = sunPatternObj.matcher(description);
        if (sunMatcher.find()) {
            businessHours.put("sunBusiness", sunMatcher.group(1) + " - " + sunMatcher.group(2));
        }

        // 휴무일 추출
        Pattern holidayPatternObj = Pattern.compile(holidayPattern);
        Matcher holidayMatcher = holidayPatternObj.matcher(description);
        if (holidayMatcher.find()) {
            String holiday = holidayMatcher.group(1).replaceAll("[,':：]+$", ""); // 끝에 있는 , ' : 제거
            businessHours.put("holiday", holiday);        }

        return businessHours;
    }

    // subDescription 파싱
    private Map<String, Boolean> parseSubDescription(String subDescription, PlaceCategory placeCategory) {
        Map<String, Boolean> features = new HashMap<>();
        features.put("hasParking", false);
        features.put("hasCafe", false);
        features.put("hasBookClub", false);
        features.put("hasIndiePub", false);
        features.put("hasSpaceRental", false);

        if (subDescription == null || subDescription.isEmpty()) {
            return features;
        }

        // 주차 가능 여부 추출
        Pattern parkingPattern = Pattern.compile("(주차\\s*:?\\s*가능|주차 가능)");
        Matcher parkingMatcher = parkingPattern.matcher(subDescription);
        if (parkingMatcher.find()) {
            features.put("hasParking", true);
        }

        switch (placeCategory) {
            case BOOKSTORE:
                // 카페 여부 추출
                Pattern cafePattern = Pattern.compile("(카페\\s*:?\\s*있음|카페 있음)");
                Matcher cafeMatcher = cafePattern.matcher(subDescription);
                if (cafeMatcher.find()) {
                    features.put("hasCafe", true);
                }

                // 독서모임 여부 추출
                Pattern bookClubPattern = Pattern.compile("(독서모임\\s*:?\\s*가능|독서 모임 가능)");
                Matcher bookClubMatcher = bookClubPattern.matcher(subDescription);
                if (bookClubMatcher.find()) {
                    features.put("hasBookClub", true);
                }

                // 독립출판물 여부 추출
                if (subDescription.contains("독립출판물")) {
                    features.put("hasIndiePub", true);
                }
                break;

            case CAFE:
                // 공간 대여 여부 추출
                Pattern spaceRentalPattern = Pattern.compile("(공간\\s*대여\\s*:?\\s*가능|공간 대여 가능)");
                Matcher spaceRentalMatcher = spaceRentalPattern.matcher(subDescription);
                if (spaceRentalMatcher.find()) {
                    features.put("hasSpaceRental", true);
                }
                break;
        }

        return features;
    }


    /**
     * 이미지 - 네이버 검색 api 호출
     */
    @Override
    @Async
    @Transactional
    public void updateImagesForAllPlaces() {
        List<Place> places = placeRepository.findAll();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        int batchSize = 10;  // 한 번에 처리할 개수(초당 api 10개 호출 가능)
        for (int i = 0; i < places.size(); i += batchSize) {
            int end = Math.min(i + batchSize, places.size());
            List<Place> batch = places.subList(i, end);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Place place : batch) {
                    String imgUrl = searchAndGetImageUrl(place.getTitle(), place.getCategory());
                    if (imgUrl != null) {
                        place.updateImage(imgUrl);
                        // placeRepository.save(place);
                    }
                }
            });
            futures.add(future);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }


    private String searchAndGetImageUrl(String placeName, PlaceCategory placeCategory) {
        String searchKeyword = placeName;

        switch (placeCategory) {
            case BOOKSTORE:
                searchKeyword += placeName + "독립서점";
                break;
            case CAFE:
                searchKeyword += placeName + " 북카페";
        }

        String encodedSearchKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
        String apiURL = "https://openapi.naver.com/v1/search/image?query=" + encodedSearchKeyword;

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String responseBody = get(apiURL, requestHeaders);

        return extractFirstImageFromResponse(responseBody);
    }

    private String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {  // 정상 호출
                String responseBody = readBody(con.getInputStream());
                System.out.println("응답 본문: " + responseBody);
                return responseBody;
            } else if (responseCode == 429) {  // 속도 제한 초과
                System.out.println("속도 제한 초과: 잠시 대기합니다.");
                Thread.sleep(500);
                return get(apiUrl, requestHeaders);
            } else {
                return readBody(con.getErrorStream());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);
        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }

    private String extractFirstImageFromResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray items = jsonResponse.getJSONArray("items");

            if (!items.isEmpty()) {
                JSONObject firstItem = items.getJSONObject(0);
                return firstItem.optString("link");
            }
        } catch (Exception e) {
            System.out.println("응답 파싱 오류: " + e.getMessage());
        }
        return null;
    }

}
