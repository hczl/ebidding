package com.ebidding.bwic.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BwicDTO {
    private String bondId;
    private Double startPrice;
    private Timestamp startTime;
    private Timestamp dueTime;
    private double size;
}
