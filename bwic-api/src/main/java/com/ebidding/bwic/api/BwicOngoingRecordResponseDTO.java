package com.ebidding.bwic.api;
import java.util.List;

import com.ebidding.bid.api.BidRankItemDataDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BwicOngoingRecordResponseDTO {
    private Long bwicId;
    private String bondId;
    private String cusip;
    private String issuer;
    private double size;

    private double startPrice;
    private double maxPrice;

    //加入@JsonFormat注解，可以将Timestamp转换为指定格式的字符串
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp dueTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp lastBidTime;
    private Long bidCounts;

    private List<BidRankItemDataDTO> children;

}