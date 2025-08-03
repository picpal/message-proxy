package com.bwc.messaging.shared.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageLogRepository extends JpaRepository<MessageLogEntity, Long> {

    Optional<MessageLogEntity> findBySendUidAndServiceCodeAndSendDate(
        String sendUid, String serviceCode, String sendDate);

    Optional<MessageLogEntity> findBySessGuidAndSendUidAndServiceCodeAndVenderAndSendDate(
        String sessGuid, String sendUid, String serviceCode, String vender, String sendDate);

    @Modifying
    @Query("UPDATE MessageLogEntity m SET m.resendCount = m.resendCount + 1, m.lastChngDttm = CURRENT_TIMESTAMP, m.lastChgrNo = :serviceCode " +
           "WHERE m.sessGuid = :sessGuid AND m.sendUid = :sendUid AND m.serviceCode = :serviceCode AND m.vender = :vender AND m.sendDate = :sendDate")
    int updateCheckCount(@Param("sessGuid") String sessGuid,
                        @Param("sendUid") String sendUid,
                        @Param("serviceCode") String serviceCode,
                        @Param("vender") String vender,
                        @Param("sendDate") String sendDate);

    @Modifying
    @Query("UPDATE MessageLogEntity m SET m.status = :status, m.lastChngDttm = CURRENT_TIMESTAMP, m.lastChgrNo = :serviceCode " +
           "WHERE m.sessGuid = :sessGuid AND m.sendUid = :sendUid AND m.serviceCode = :serviceCode AND m.vender = :vender AND m.sendDate = :sendDate")
    int updateSendStatus(@Param("status") String status,
                        @Param("sessGuid") String sessGuid,
                        @Param("sendUid") String sendUid,
                        @Param("serviceCode") String serviceCode,
                        @Param("vender") String vender,
                        @Param("sendDate") String sendDate);
}