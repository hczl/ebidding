package com.ebidding.bid.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "bid")
public class Bid {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "bid_id")
    private Long bidId;      // name: bid_id

    @Column(name = "account_id")
    private Long accountId;  // name: account_id

    @Column(name = "price")
    private Double price;    // name: price

    @Column(name = "ranking")
    private Long ranking;    // name: ranking

    @Column(name = "time")
    private Timestamp time;  // name: time

    @Column(name = "bwic_id")
    private Long bwicId;     // name: bwic_id

}