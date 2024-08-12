package com.ebidding.bid.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidCreateRequestDTO {
    private Long bwicId;
    private Double price;
}

