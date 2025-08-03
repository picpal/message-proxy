package com.bwc.messaging.shared.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_MG_SERVICE_LNK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceLinkEntity {

    @Id
    @Column(name = "SERVICE_CODE", length = 40)
    private String serviceCode;

    @Column(name = "VENDER", length = 20)
    private String vender;
}