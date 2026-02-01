package com.rnd.testinghub.adapters.persistence;

import com.rnd.testinghub.domain.Practice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PracticeRepository extends JpaRepository<Practice, Long> {
    Optional<Practice> findBySlug(String slug);
    @Query("SELECT p FROM Practice p WHERE p.tags LIKE %:tag%")
    List<Practice> findByTag(@Param("tag") String tag);
    @Query("SELECT p FROM Practice p WHERE p.title LIKE %:query% OR p.content LIKE %:query%")
    List<Practice> search(@Param("query") String query);
}
