package com.ebidding.bwic.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BwicRecordResponseDTO {
    private Long bwicId;
    private String bondId;
    private String cusip;
    private String issuer;
    private double size;
    private String startTime;
    private String dueTime;
    private String lastBidTime;
    private double startPrice;
    private Long bidCounts;
    private Boolean active;
    private String rating;
    private String coupon;
    private Double score;
}