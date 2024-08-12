package com.ebidding.bid.controller;

import com.ebidding.account.api.AccountDTO;
import com.ebidding.bid.api.BidCreateRequestDTO;
import com.ebidding.bid.api.BidCreateResponseDTO;
import com.ebidding.bid.api.BidRankItemDataDTO;
import com.ebidding.bid.api.PriceResponseDTO;
import com.ebidding.bid.domain.Bid;
import com.ebidding.bid.domain.BidRank;
import com.ebidding.bid.domain.BidRankPK;
import com.ebidding.bid.domain.chat.ChatRequestDTO;
import com.ebidding.bid.domain.chat.SingleMessageDTO;
import com.ebidding.bid.service.BidService;
import com.ebidding.bid.service.GPTService;
import com.ebidding.bwic.api.BwicClient;
import com.ebidding.common.auth.AuthConstant;
import com.ebidding.common.auth.Authorize;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/bid-service")
public class BidController {
    @Autowired
    private BidService bidService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BwicClient bwicClient;

    @Autowired
    private GPTService gptService;


//
//    @RequestHeader(AuthConstant.X_JWT_ID_HEADER) String userId;
//
//    @GetMapping("/accounts")//默认"api/v1/bids"
//    @Authorize(AuthConstant.TRADER)
//    public ResponseEntity<AccountDTO> getBid(@RequestParam("name") String name) {
//        return ResponseEntity.ok(this.bidService.getByName(name));
//    }

    @Authorize(AuthConstant.TRADER)
    @GetMapping("/bids/{bidId}")
    public ResponseEntity<Bid> getBid(@PathVariable("bidId") Long bidId){
        return ResponseEntity.ok(this.bidService.getByBidId(bidId));
    }

    @PostMapping("/bids")
    public ResponseEntity<BidCreateResponseDTO> createBid(@RequestBody BidCreateRequestDTO bidCreateRequestDTO, HttpServletRequest request){
        //获取header里面的bid_id
        String currentAccountId = request.getHeader(AuthConstant.X_JWT_ID_HEADER);
        Long accountId = Long.valueOf(currentAccountId);


        //创建新的Bid
        Bid bid = new Bid();
        //设置id
        bid.setAccountId(accountId);
        bid.setPrice(bidCreateRequestDTO.getPrice());
        bid.setBwicId(bidCreateRequestDTO.getBwicId());

        // 现在的bid只有输入的price和bwicId（在BidRankPK中），以及从请求头获取的accountId。但是还缺少bidTime和bidRank
        Bid createdBid = bidService.createBid(bid);

        //将Bid对象转换为BidCreateResponseDTO对象
        BidCreateResponseDTO responseDTO = new BidCreateResponseDTO();
        responseDTO.setPrice(createdBid.getPrice());
        responseDTO.setRanking(createdBid.getRanking());
        responseDTO.setTime(createdBid.getTime());
                    //下面将createdBid的bwicId转化为cusip。
        ResponseEntity<String> response = bwicClient.getCusip(createdBid.getBwicId());
        String cusip = response.getBody();
        responseDTO.setCusip(cusip);


        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @GetMapping("/bwics/{bwicId}/accounts/rank")
    public ResponseEntity<Long> getUserRank(HttpServletRequest request,
                                            @PathVariable("bwicId") Long bwicId,
                                            @RequestParam(value = "accountId",required = false) Long accountId) {
        if(accountId == null || accountId <= 0) {
            String currentAccountId = request.getHeader(AuthConstant.X_JWT_ID_HEADER);
            accountId = Long.valueOf(currentAccountId);
        }
        //通过bidrank表获取到某个用户现在的排名
        Long rank = this.bidService.getRankByBwicIdAndAccountId(bwicId, Long.valueOf(accountId));
        return ResponseEntity.ok(rank);

    }




    @GetMapping("/bwics/{bwicId}/bids/count")
    public ResponseEntity<Long> getParticipantCount(@PathVariable("bwicId") Long bwicId) {
        Long participantCount = bidService.getParticipantCount(bwicId);
        return ResponseEntity.ok(participantCount);
    }


    @GetMapping("/bwics/{bwicId}/account/price-info")
    //这是用户界面的接口，用户界面需要知道自己的price，排名以及第二名的price（如果自己是第一名的话）
    public ResponseEntity<PriceResponseDTO> getPrice(@PathVariable("bwicId") Long bwicId, @RequestHeader(AuthConstant.X_JWT_ID_HEADER) String accountId) {
        PriceResponseDTO response = this.bidService.getPrice(bwicId, Long.valueOf(accountId));
        return ResponseEntity.ok(response);
    }


    @GetMapping("api/v1/bid-service/{bwicId}/bid/success")
    public Bid getSuccesBidByBwicid(@PathVariable Long bwicId) {
        return bidService.getSuccesBidByBwicid(bwicId);
    }


    //传入某一个bwicId，返回这个bwicId下部分的bidRankItemDataDTO
    @GetMapping("/bwics/{bwicId}/ongoing-part-items")
    List<BidRankItemDataDTO> getPartBidRankingsByBwicId(@PathVariable("bwicId") Long bwicId){
        return bidService.getPartBidRankingsByBwicId(bwicId);
    }

    //传入某一个bwicId，返回这个bwicId下所有的bidRankItemDataDTO
    @GetMapping("/bwics/{bwicId}/ongoing-all-items")
    List<BidRankItemDataDTO> getAllBidRankingsByBwicId(@PathVariable("bwicId") Long bwicId){
        return bidService.getAllBidRankingsByBwicId(bwicId);
    }



    /**
     * accountid和bwicid查bid
     * @param request
     * @param bwicId
     * @return
     */
    @GetMapping("/getBidByBwicIdAndAccountId/{bwicId}")
    public List<Bid> getBidByBwicIdAndAccountId(HttpServletRequest request, @PathVariable Long bwicId) {
        String currentAccountId = request.getHeader(AuthConstant.X_JWT_ID_HEADER);
        Long accountId =Long.valueOf(currentAccountId);
        return bidService.getBidByBwicIdAndAccountId(bwicId,accountId);
    }

    @GetMapping("/getBidByAccountId")
    public List<Long> getBwicIdListByAccountId(HttpServletRequest request,
           @RequestParam(value = "accountId",required = false) Long accountId){
        if(accountId == null){
            String currentAccountId = request.getHeader(AuthConstant.X_JWT_ID_HEADER);
            accountId =Long.valueOf(currentAccountId);
        }
        return bidService.getBwicIdListByAccountId(accountId);
    }


    @GetMapping(value = "/bids/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> chat(@RequestParam String message, HttpSession httpSession) {
        String role = "user";  // or another role based on your logic

        // Get the message history from session
        List<SingleMessageDTO> history = (List<SingleMessageDTO>) httpSession.getAttribute("history");
        if (history == null) {
            history = new ArrayList<>();
        }

        // Create a new request and add the new message
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessages(history);
        request.addMessage(role, message);

        // Save the message to the history
        httpSession.setAttribute("history", request.getMessages());

        // Send the request and return the response stream
        return gptService.chatWithGPT(request);
    }





//    @GetMapping("/bidRanks")
//    public  ResponseEntity<BidRank> getBidRank(@RequestParam("bid_id") Long bidId){
//        return ResponseEntity.ok(this.bidService.getBybidId(bidId));
//    }
}
