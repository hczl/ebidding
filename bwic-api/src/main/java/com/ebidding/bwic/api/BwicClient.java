package com.ebidding.bwic.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@FeignClient(name = "bwic-service", path = "api/v1/bwic-service", url= "${ebidding.bwic-endpoint}" )
public interface BwicClient {
    @GetMapping("/bwics/{bwicId}/cusip")
    ResponseEntity<String> getCusip(@PathVariable("bwicId") Long bwicId);

    @PutMapping("/bwics/{bwicId}")
    ResponseEntity<Void> updateBwic(@PathVariable("bwicId") Long bwicId,
                                    @RequestParam("price") double price,
                                    @RequestParam("time") Timestamp time);

    @GetMapping("/bwics/{bwicId}/price")
    ResponseEntity<Double> getPrice(@PathVariable("bwicId") Long bwicId);

}