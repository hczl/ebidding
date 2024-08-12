package com.ebidding.bwic.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bwic")
public class Bwic {
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    @Column(name = "bwic_id", nullable = false)
    private Long bwicId;

    @Column(name = "bond_id", nullable = false)
    private String bondId;

    @Column(name = "size")
    private double size;

    @Column(name = "start_price")
    private double startPrice;

    @Column(name = "present_price")
    private double presentPrice;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "due_time")
    private Timestamp dueTime;

    @Column(name = "last_bid_time")
    private Timestamp lastBidTime;

    @Column(name = "bid_counts")
    private Long bidCounts;

}
