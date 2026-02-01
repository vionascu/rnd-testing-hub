package com.rnd.testinghub.adapters.persistence;

import com.rnd.testinghub.domain.ApiSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiSpecRepository extends JpaRepository<ApiSpec, Long> {
}
