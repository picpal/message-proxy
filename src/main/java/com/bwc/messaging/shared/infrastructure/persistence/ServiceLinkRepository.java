package com.bwc.messaging.shared.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceLinkRepository extends JpaRepository<ServiceLinkEntity, String> {

    @Query("SELECT s.vender FROM ServiceLinkEntity s WHERE s.serviceCode = :serviceCode")
    Optional<String> findVenderByServiceCode(@Param("serviceCode") String serviceCode);
}