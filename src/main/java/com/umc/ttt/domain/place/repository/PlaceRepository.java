package com.umc.ttt.domain.place.repository;

import com.umc.ttt.domain.place.entity.Place;
import com.umc.ttt.domain.place.entity.enums.PlaceCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    // 카테고리별 추천 순 정렬
    @Query(value = "SELECT p.* FROM Place p WHERE p.category = :category " +
            "ORDER BY COALESCE(p.rating, 0) DESC LIMIT :limit", nativeQuery = true)
    List<Place> findFirstPageByCategoryOrderByRecommendation(@Param("category") String category, @Param("limit") int limit);

    @Query(value = "SELECT p.* FROM Place p WHERE p.category = :category " +
            "AND (COALESCE(p.rating, 0) < (SELECT COALESCE(rating, 0) FROM Place WHERE place_id = :cursor) " +
            "OR (COALESCE(p.rating, 0) = (SELECT COALESCE(rating, 0) FROM Place WHERE place_id = :cursor) " +
            "AND p.place_id > :cursor)) " +
            "ORDER BY COALESCE(p.rating, 0) DESC LIMIT :limit", nativeQuery = true)
    List<Place> findByCategoryOrderByRecommendationWithCursor(@Param("category") String category, @Param("cursor") Long cursor, @Param("limit") int limit);

    // 추천 순 정렬
    @Query(value = "SELECT p.* FROM Place p " +
            "ORDER BY COALESCE(p.rating, 0) DESC LIMIT :limit", nativeQuery = true)
    List<Place> findFirstPageOrderByRecommendation(@Param("limit") int limit);

    @Query(value = "SELECT p.* FROM Place p " +
            "WHERE (COALESCE(p.rating, 0) < (SELECT COALESCE(rating, 0) FROM Place WHERE place_id = :cursor) " +
            "OR (COALESCE(p.rating, 0) = (SELECT COALESCE(rating, 0) FROM Place WHERE place_id = :cursor) " +
            "AND p.place_id > :cursor)) " +
            "ORDER BY COALESCE(p.rating, 0) DESC LIMIT :limit", nativeQuery = true)
    List<Place> findOrderByRecommendationWithCursor(@Param("cursor") Long cursor, @Param("limit") int limit);

    // 카테고리별 거리 순 정렬
    @Query(value = "SELECT s1.* FROM place s1 JOIN (SELECT s2.place_id, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(s2.x_pos)) * cos(radians(s2.y_pos) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.x_pos)))) AS distance " +
            "FROM place s2 WHERE s2.category = :category) AS places ON s1.place_id = places.place_id " +
            "ORDER BY places.distance ASC LIMIT :limit", nativeQuery = true)
    List<Place> findFirstPageByCategoryOrderByDistance(@Param("latitude") double latitude, @Param("longitude") double longitude,
                                                       @Param("category") String category, @Param("limit") int limit);

    @Query(value = "SELECT s1.* FROM place s1 JOIN (SELECT s2.place_id, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(s2.x_pos)) * cos(radians(s2.y_pos) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.x_pos)))) AS distance " +
            "FROM place s2 WHERE s2.category = :category) AS places ON s1.place_id = places.place_id " +
            "WHERE places.distance > (SELECT (6371 * acos(cos(radians(:latitude)) * cos(radians(s2.x_pos)) * cos(radians(s2.y_pos) - radians(:longitude)) " +
            "+ sin(radians(:latitude)) * sin(radians(s2.x_pos)))) AS distance FROM place s2 WHERE s2.place_id = :cursor) " +
            "ORDER BY places.distance ASC LIMIT :limit", nativeQuery = true)
    List<Place> findByCategoryOrderByDistanceWithCursor(@Param("latitude") double latitude, @Param("longitude") double longitude,
                                                        @Param("category") String category, @Param("cursor") Long cursor, @Param("limit") int limit);

    // 거리 순 정렬
    @Query(value = "SELECT s1.* FROM place s1 JOIN (SELECT s2.place_id, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(s2.x_pos)) * cos(radians(s2.y_pos) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.x_pos)))) AS distance " +
            "FROM place s2) AS places ON s1.place_id = places.place_id " +
            "ORDER BY places.distance ASC LIMIT :limit", nativeQuery = true)
    List<Place> findFirstPageOrderByDistance(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("limit") int limit);

    @Query(value = "SELECT s1.* FROM place s1 JOIN (SELECT s2.place_id, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(s2.x_pos)) * cos(radians(s2.y_pos) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.x_pos)))) AS distance " +
            "FROM place s2) AS places ON s1.place_id = places.place_id " +
            "WHERE places.distance > (SELECT (6371 * acos(cos(radians(:latitude)) * cos(radians(s2.x_pos)) * cos(radians(s2.y_pos) - radians(:longitude)) " +
            "+ sin(radians(:latitude)) * sin(radians(s2.x_pos)))) AS distance FROM place s2 WHERE s2.place_id = :cursor) ORDER BY places.distance ASC LIMIT :limit", nativeQuery = true)
    List<Place> findOrderByDistanceWithCursor(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("cursor") Long cursor, @Param("limit") int limit);

    // 검색
    @Query("""
        SELECT p FROM Place p 
        WHERE (p.title LIKE %:keyword% OR p.address LIKE %:keyword%)
        AND p.id > :cursor 
        ORDER BY p.id ASC
    """)
    List<Place> findPlacesByKeyword(@Param("keyword") String keyword, @Param("cursor") long cursor, Pageable pageable);

    List<Place> findPlacesByCategory(PlaceCategory placeCategory);
    List<Place> findAllByIdGreaterThanEqual(long l);

    Place findPlaceByTitle(String title);
}