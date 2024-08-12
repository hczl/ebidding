package com.ebidding.bid.api;

//import com.ebidding.bid.domain.Bid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "bid-service", path = "api/v1/bid-service", url = "${ebidding.bid-endpoint}")
public interface BidClient {
    //@GetMapping("/{bwicId}/bid/success")
    //Bid getSuccesBidByBwicid(@PathVariable("bwicId") Long bwicId);

    @GetMapping("/getBidByAccountId")
    List<Long> getBwicIdListByAccountId(@RequestParam(value = "accountId",required = false) Long accountId);


    @GetMapping("/bwics/{bwicId}/accounts/rank")
    ResponseEntity<Long> getUserRank(@PathVariable("bwicId") Long bwicId,
                                     @RequestParam(value = "accountId",required = false) Long accountId);

    @GetMapping("/bwics/{bwicId}/ongoing-part-items")
    List<BidRankItemDataDTO> getBidRankingsByBwicId(@PathVariable("bwicId") Long bwicId);


}

