package com.umc.ttt.global.aws.s3.repository;


import com.umc.ttt.global.aws.s3.entity.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UuidRepository extends JpaRepository<Uuid, Long> {
}
