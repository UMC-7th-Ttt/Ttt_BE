package com.umc.ttt.domain.place.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingService {
    @Value("${kakao.api-url}")
    private String KAKAO_API_URL;

    @Value("${kakao.api-key}")
    private String KAKAO_API_KEY;

    public String getAddressFromCoordinates(Double lat, Double lon) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        String url = UriComponentsBuilder.fromUriString(KAKAO_API_URL)
                .queryParam("x", lon)
                .queryParam("y", lat)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");

            if (documents.isArray() && !documents.isEmpty()) {
                return documents.get(0).path("address").path("region_2depth_name").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // 주소 변환 실패 시 null 반환
    }
}