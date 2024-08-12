package com.ebidding.bid.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidCreateResponseDTO {

    private Double price;

    private Long ranking;

    private Timestamp time;

    private String cusip;


}
