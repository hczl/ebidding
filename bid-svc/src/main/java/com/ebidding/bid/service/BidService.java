package com.ebidding.bid.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ebidding.account.api.AccountDTO;
import com.ebidding.account.api.AccountClient;
import com.ebidding.bid.api.BidRankItemDataDTO;
import com.ebidding.bwic.api.BwicClient;
import com.ebidding.bid.api.PriceResponseDTO;
import com.ebidding.bid.domain.Bid;
import com.ebidding.bid.domain.BidRank;
import com.ebidding.bid.domain.BidRankPK;
import com.ebidding.bid.repository.BidRankRepository;
import com.ebidding.bid.repository.BidRepository;
import com.ebidding.common.utils.WebSocketMessageUtil;
import com.ebidding.common.websocket.UserIdSessionManager;
import com.ebidding.common.websocket.enums.WebSocketMsgType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.WebSocketSession;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//针对标有 @NonNull 注解的变量和 final 变量进行参数的构造方法。
public class BidService {

    @Autowired
    private final AccountClient accountClient;

    @Autowired
    private BwicClient bwicClient;


    //    @Autowired
//    public BidService(AccountClient accountClient) {
//        this.accountClient = accountClient;
//    }用@RequiredArgsConstructor代替
    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private BidRankRepository bidRankRepository;

    @Autowired
    private ModelMapper modelMapper;

    public AccountDTO getByName(String inputName) {//调用account-api
        return this.accountClient.getAccount(inputName).getBody();
    }

    public Bid getByBidId(Long bidId) {
        return this.bidRepository.findByBidId(bidId).orElse(null);
    }


    public Bid createBid(Bid bid) {
        ResponseEntity<Double> price =  this.bwicClient.getPrice(bid.getBwicId());
        if (price != null && price.getBody() != null) {
            if (bid.getPrice() <= price.getBody()) {
            throw new IllegalArgumentException("Price cannot be lower than previous bid");
        }}
                    // 如果新出价低于或等于现有出价，抛出异常

        // 1. 添加AccountId和BwicId,再添加时间戳
        BidRank bidRank = new BidRank();
        BidRankPK embbedeId = new BidRankPK(bid.getAccountId(), bid.getBwicId());
        bidRank.setId(embbedeId);
        bidRank.setPrice(bid.getPrice());
        Timestamp preTime = new Timestamp(System.currentTimeMillis());
        bidRank.setTime(preTime);
        bid.setTime(preTime);

        //2.现在获取排名
        this.bidRankRepository.save(bidRank);
        Long ranking = bidRankRepository.getRanking(bid.getBwicId(),bid.getAccountId());

        // 3. 更新Bid的排名
        bid.setRanking(ranking);
        this.bidRepository.save(bid);

        //最后还要更新bwic中的bidCounts，last_bid_time，和present_price(如果比present_price高的话)
        //这个方法还要更新bond中的transaction_counts
        bwicClient.updateBwic(bid.getBwicId(),bid.getPrice(),bid.getTime());

        // 通知当前bwicId的用户排名更新
        List<BidRank> bidRankList = bidRankRepository.getListByBwicid(bid.getBwicId()).get();
        for (int i = 0; i < bidRankList.size(); i++) {
            BidRank bidRankInfo = bidRankList.get(i);
            WebSocketSession webSocketSession = UserIdSessionManager.getSession(bidRankInfo.getId().getAccountId().intValue());
            JSONObject msgObj = new JSONObject();
            msgObj.put("msgType", WebSocketMsgType.NOTICE_RANK_CHANGE_MSG.getCode());
            msgObj.put("result", "success");
            if (i == 0) {
                msgObj.put("msg", "Your ranking has been updated to " + (i + 1)+"/"+ bidRankList.size() + "; No.2's price is " + (bidRankList.size() >= 2 ? bidRankList.get(1).getPrice() : "Unavailable"));
            } else {
                msgObj.put("msg", "Your ranking has been updated to " + (i + 1)+"/"+ bidRankList.size());
            }
            WebSocketMessageUtil.sendMsgToOne(webSocketSession, JSONUtil.toJsonStr(msgObj));
        }

        return bid;
    }

    public Long getRankByBwicIdAndAccountId(Long bwicId, Long accountId) {
        // 检查bidRank是否存在
        Long ranking = bidRankRepository.getRanking(bwicId,accountId);
       return ranking;

    }

    public Long getParticipantCount(Long bwicId) {
        return bidRankRepository.countByBwicId(bwicId);
    }

    public PriceResponseDTO getPrice(Long bwicId, Long accountId) {

        PriceResponseDTO response = new PriceResponseDTO();
        BidRank bidRank = this.bidRankRepository.findByBwicIdAndAccountId(bwicId, accountId).orElseThrow(()-> new NoSuchElementException("Record not found"));
        response.setPrice(bidRank.getPrice());
        Long rank = this.getRankByBwicIdAndAccountId(bwicId, accountId);
        response.setRanking(rank);
        if (rank == 1) {
            response.setRanking(this.bidRankRepository.getRanking(bwicId, accountId));
            if (this.getParticipantCount(bwicId) > 1) {
                Double secondPrice = this.bidRankRepository.getSecondHighestPrice(bwicId).orElse(null);
                response.setSecondPrice(secondPrice);
            }
        }
        return response;

    }



//    public BidRank getByBidId(Long bidId){
//        return this.bidRankRepository.findByBidId(bidId).orElse(null);
//    }


    public Bid getSuccesBidByBwicid(Long bwicId) {
        Bid bid = bidRepository.getSuccesBidByBwicid(bwicId);
        WebSocketSession webSocketSession = UserIdSessionManager.getSession(bid.getAccountId().intValue());
        JSONObject msgObj = new JSONObject();
        msgObj.put("msgType", WebSocketMsgType.NOTICE_RESULT_MSG.getCode());
        msgObj.put("result", "success");
        msgObj.put("msg", "恭喜您，此次拍卖竞拍成功！");
        WebSocketMessageUtil.sendMsgToOne(webSocketSession, JSONUtil.toJsonStr(msgObj));

        List<Bid> bidList = bidRepository.getListByBwicid(bwicId);
        bidList.forEach(bidInfo -> {
            if(bidInfo.getAccountId().intValue() != bid.getAccountId().intValue()){
                WebSocketSession webSocketSession1 = UserIdSessionManager.getSession(bidInfo.getAccountId().intValue());
                JSONObject msgObj1 = new JSONObject();
                msgObj1.put("msgType", WebSocketMsgType.NOTICE_RESULT_MSG.getCode());
                msgObj1.put("result", "success");
                msgObj1.put("msg", "抱歉，您此次拍卖竞拍失败！");
                WebSocketMessageUtil.sendMsgToOne(webSocketSession1, JSONUtil.toJsonStr(msgObj1));
            }
        });
        return bid;
    }

    //获取部分bidRanking

    public List<BidRankItemDataDTO> getPartBidRankingsByBwicId(Long bwicId) {
        List<BidRank> bidRanks = bidRankRepository.getByBwicIdOrderByPriceDesc(bwicId);
        List<BidRankItemDataDTO> bidRankItems = new ArrayList<>();

        if(bidRanks.size() > 3) {
            BidRank firstBidRank = bidRanks.get(0);
            BidRank secondBidRank = bidRanks.get(1);
            BidRank lastBidRank = bidRanks.get(bidRanks.size()-1);

            bidRankItems.add(convertBidRankToDTO(firstBidRank, 1L));
            bidRankItems.add(convertBidRankToDTO(secondBidRank, 2L));
            bidRankItems.add(new BidRankItemDataDTO()); // 省略中间部分
            bidRankItems.add(convertBidRankToDTO(lastBidRank, (long) bidRanks.size()));
        } else {
            for (int i = 0; i < bidRanks.size(); i++) {
                bidRankItems.add(convertBidRankToDTO(bidRanks.get(i), (long) (i+1)));
            }
        }

        return bidRankItems;
    }

    private BidRankItemDataDTO convertBidRankToDTO(BidRank bidRank, Long ranking) {
        BidRankItemDataDTO dto = new BidRankItemDataDTO();
        dto.setRanking(ranking);
        dto.setPrice(bidRank.getPrice());
        dto.setTime(bidRank.getTime());

        // 从BidRankPK获取accountId
        //其中getId()获取到的是BidRank对象的BidRankPK实例，然后通过getAccountId()获取到实际的accountId
        Long accountId = bidRank.getId().getAccountId();
        dto.setAccountId(accountId);

        // 使用FeignClient调用远程服务获取账户名
        String accountName = accountClient.getAccountNameByAccountId(accountId);
        dto.setAccountName(accountName);

        return dto;
    }

    public List<BidRankItemDataDTO> getAllBidRankingsByBwicId(Long bwicId) {
        List<BidRank> bidRanks = bidRankRepository.getByBwicIdOrderByPriceDesc(bwicId);
        List<BidRankItemDataDTO> bidRankItems = new ArrayList<>();

        for (int i = 0; i < bidRanks.size(); i++) {
            bidRankItems.add(convertBidRankToDTO(bidRanks.get(i), (long) (i+1)));
        }

        return bidRankItems;
    }


    public List<Bid> getBidByBwicIdAndAccountId(Long bwicId,Long accountId){
        return bidRepository.getBidByBwicIdAndAccountId(bwicId,accountId);
    }

    public List<Long> getBwicIdListByAccountId(Long accountId){
        return bidRepository.getBwicIdListByAccountId(accountId);
    }
}