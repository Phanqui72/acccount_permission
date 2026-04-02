package com.mgr.api.repository;

import com.mgr.api.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    // Các hàm cơ bản đã được JpaRepository và JpaSpecificationExecutor cung cấp
}