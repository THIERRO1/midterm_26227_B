package com.academic.integrity.repository;

import com.academic.integrity.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {

    Optional<Province> findByProvinceCode(String provinceCode);

    Optional<Province> findByProvinceNameIgnoreCase(String provinceName);

    boolean existsByProvinceCode(String provinceCode);
}
