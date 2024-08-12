package com.ebidding.bwic.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bond")
public class Bond {
    @Id
    @Column(name = "bond_id", nullable = false)
    private String bondId;

    @Column(name = "cusip", nullable = false)
    private String cusip;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "rating")
    private String rating;

    @Column(name = "coupon")
    private String coupon;

    @Column(name = "maturity_date")
    private Timestamp maturityDate;

    @Column(name = "transaction_counts")
    private Long transactionCounts;
}