package com.ebidding.bwic.service;

import com.ebidding.bid.api.BidClient;
import com.ebidding.bid.api.BidRankItemDataDTO;
import com.ebidding.bwic.api.*;
import com.ebidding.bwic.domain.Bond;
import com.ebidding.bwic.domain.Bwic;



import com.ebidding.bwic.repository.BwicRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class BwicService {
    @Autowired
    private BwicRepository bwicRepository;

    @Autowired
    private BondService bondService;
    @Autowired
    private OnnxModelService onnxModelService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BidClient bidClient;

//    @Autowired
//    private GPTClient gptClient;



    @Autowired
    private ObjectMapper objectMapper;

    public Optional<BwicDTO> saveBwic(String bondId, double startPrice, Timestamp startTime, Timestamp dueTime, double size) {
        Bwic bwic = Bwic.builder()
                .bondId(bondId)
                .startPrice(startPrice)
                .presentPrice(startPrice)
                .startTime(startTime)
                .dueTime(dueTime)
                .lastBidTime(startTime)
                .size(size)
                .bidCounts(0L)   // default value
                .build();

        Bwic savedBwic = bwicRepository.save(bwic);

        BwicDTO savedBwicDTO = BwicDTO.builder()
                .bondId(savedBwic.getBondId())
                .startPrice(savedBwic.getStartPrice())
                .startTime(savedBwic.getStartTime())
                .dueTime(savedBwic.getDueTime())
                .size(savedBwic.getSize())
                .build();

        return Optional.of(savedBwicDTO);
    }

    public double getBwicPrice(Long bwicId) {
        Bwic bwic = this.bwicRepository.findByBwicId(bwicId).orElse(null);
        return bwic.getPresentPrice();
    }

    public boolean isActive(Long bwicId) {
        Bwic bwic = this.bwicRepository.findByBwicId(bwicId).orElse(null);
        if (bwic == null) {
            throw new RuntimeException("Bwic not found");
        }
        LocalDateTime dueTime = bwic.getDueTime().toLocalDateTime();

        if(!dueTime.isAfter(LocalDateTime.now())){
            // 查询竞拍成功的用户并发送通知
            // TODO 这里通过feign调用 bid-svc服务的api/v1/bids/getSuccesBidByBwicid接口即可
        }

        return dueTime.isAfter(LocalDateTime.now());
    }


    // getHistoryRecords method
    public List<BwicRecordResponseDTO> getHistoryRecords(String user_id) {
    List<Bwic> validBwics = bwicRepository.findAll();

    // 获取所有的债券信息，并构建一个 bondId 到 Bond 对象的映射
    List<Bond> bondList = bondService.getAllBonds();
    Map<String, Bond> bondMap = bondList.stream()
            .collect(Collectors.toMap(Bond::getBondId, bond -> bond));
    List<String> bwicIds = validBwics.stream()
            .map(Bwic::getBwicId)
            .map(String::valueOf)
            .map(id -> {
                int bwicId = Integer.parseInt(id);
                // Adjust bwicId if it's out of the desired range
                if (bwicId < 1 || bwicId > 2000) {
                    return "1"; // or any value within the desired range
                }
                return id;
            })
            .collect(Collectors.toList());
    Map<String, Double> scores = onnxModelService.predict(user_id, bwicIds);

    // 创建并返回 BWIC 记录的 DTO 列表，并根据 Score 高低排序
    return validBwics.stream()
            .map(bwic -> {
                BwicRecordResponseDTO dto = modelMapper.map(bwic, BwicRecordResponseDTO.class);
                Bond bond = bondMap.get(bwic.getBondId());
                if (bond != null) {
                    dto.setCusip(bond.getCusip());
                    dto.setIssuer(bond.getIssuer());
                    dto.setRating(bond.getRating());
                    dto.setCoupon(bond.getCoupon());
                    dto.setScore(scores.get(String.valueOf(bwic.getBwicId())));
                }
                dto.setActive(true); // 所有的 BWIC 在这里都是有效的，因为已经经过过滤
                return dto;
            })
            .sorted(Comparator.comparing(BwicRecordResponseDTO::getScore).reversed()) // 根据 Score 高低排序
            .collect(Collectors.toList());
}


    //---------------------------------------------查找正在进行的bwic------------------------------------------------
    public List<BwicOngoingRecordResponseDTO> getOngoingBwics() {

        List<Bwic> ongoingBwics = bwicRepository.findOngoingBwics();
        List<BwicOngoingRecordResponseDTO> responseDTOs = new ArrayList<>();

        for (Bwic bwic : ongoingBwics) {
            BwicOngoingRecordResponseDTO dto = modelMapper.map(bwic, BwicOngoingRecordResponseDTO.class);

            //因为前端展示的table是标的maxPrice，而数据库中的是presentPrice，但含义是一样的，所以这里将presentPrice赋值给maxPrice
            dto.setMaxPrice(bwic.getPresentPrice());

            dto.setCusip(getBondCusip(bwic.getBondId()));
            dto.setIssuer(getBondIssuer(bwic.getBondId()));

            List<BidRankItemDataDTO> bidRankings = bidClient.getBidRankingsByBwicId(bwic.getBwicId());

            // set the children property of dto with the obtained bidRankings
            dto.setChildren(bidRankings);

            responseDTOs.add(dto);
        }

        return responseDTOs;

    }

    //---------------------------------------------查找正在进行的bwic------------------------------------------------


    //---------------------------------------------查找还未开始的bwic------------------------------------------------
    public List<BwicRecordResponseDTO> getUpcomingBwics() {

            List<Bwic> incomingBwics = bwicRepository.findUpcomingBwics();
            List<BwicRecordResponseDTO> responseDTOs = new ArrayList<>();

            for (Bwic bwic : incomingBwics) {
                BwicRecordResponseDTO dto = modelMapper.map(bwic, BwicRecordResponseDTO.class);

                dto.setCusip(getBondCusip(bwic.getBondId()));
                dto.setIssuer(getBondIssuer(bwic.getBondId()));
                responseDTOs.add(dto);
            }
            return responseDTOs;
    }

    //---------------------------------------------查找还未开始的bwic------------------------------------------------



    //---------------------------------------------查找已经结束的bwic------------------------------------------------
    public List<BwicRecordResponseDTO> getEndedBwics() {

                List<Bwic> endedBwics = bwicRepository.findEndedBwics();
                List<BwicRecordResponseDTO> responseDTOs = new ArrayList<>();

                for (Bwic bwic : endedBwics) {
                    BwicRecordResponseDTO dto = modelMapper.map(bwic, BwicRecordResponseDTO.class);

                    dto.setCusip(getBondCusip(bwic.getBondId()));
                    dto.setIssuer(getBondIssuer(bwic.getBondId()));
                    responseDTOs.add(dto);
                }
                return responseDTOs;
    }






    //---------------------------------------------查找bond的属性接口------------------------------------------------
    public String getBondCusip(String bondId) {
        String cusip = bondService.findCuSipByBondId(bondId);
        return cusip;
    }
    //---------------------------------------------查找bond的属性接口------------------------------------------------
    public String getBondIssuer(String bondId) {
        String issuer = bondService.findIssuerByBondId(bondId);
        return issuer;
    }




    public String getCusipByBwicId(Long bwicId) {
        Bwic bwic = this.bwicRepository.findByBwicId(bwicId).orElse(null);
        if (bwic == null) {
            throw new RuntimeException("Bwic not found");
        }
        String bondId = bwic.getBondId();
        return getBondCusip(bondId);
    }

    public String getIssuerByBwicId(Long bwicId) {
        Bwic bwic = this.bwicRepository.findByBwicId(bwicId).orElse(null);
        if (bwic == null) {
            throw new RuntimeException("Bwic not found");
        }
        String bondId = bwic.getBondId();
        return getBondIssuer(bondId);
    }



    //加入Transactional，防止并发
    @Transactional
    public void incrementBidCount(Long bwicId) {
        bwicRepository.incrementBidCount(bwicId);
    }



    public void updateBwicAndBond(Long bwicId, double price, Timestamp time) {
        Bwic bwic = this.bwicRepository.findById(bwicId)
                .orElseThrow(() -> new RuntimeException("Bwic not found"));

        bwic.setBidCounts(bwic.getBidCounts() + 1);
        bwic.setLastBidTime(time);

        if (price  > bwic.getPresentPrice()) {
            bwic.setPresentPrice(price);
        }

        this.bwicRepository.save(bwic);

        //更新bond表
        bondService.incrementTransactionCount(bwic.getBondId());

    }

    public Bwic findByBwicId(Long bwicId){
        return this.bwicRepository.findByBwicId(bwicId).orElse(null);
    }

////    public List<Bwic> findByCusip(String cusip) {
////        return  this.bwicRepository.findByCusip(cusip).orElse(null);
//    }

    public Bwic findByBondId(String bondId){
        return this.bwicRepository.findByBondId(bondId).orElse(null);
    }

public List<BwicRecordResponseDTO> getBwicByAccountId(Long accountId) {
        List<BwicRecordResponseDTO> responseDTOs = new ArrayList<>();

        // 查询 accountId 的 Bid
        List<Long> bwicIdList = bidClient.getBwicIdListByAccountId(accountId);
        Set<Long> bwicIdSet = new HashSet<>(bwicIdList);

        // 从 bwicRepository 获取所有相关的 BWIC 记录
        List<Bwic> bwics = bwicRepository.findAllById(bwicIdSet);

        // 从 bwic 记录中提取 bondId，并去重
        List<String> bondIds = bwics.stream()
                .map(Bwic::getBondId)
                .distinct()
                .collect(Collectors.toList());
        List<Bond> bonds = bondService.findAllById(bondIds);
        // 转换成 Map 方便后续使用
        Map<String, Bond> bondMap = bonds.stream()
                .collect(Collectors.toMap(Bond::getBondId, bond -> bond));

        // 遍历 BWIC 记录并填充 DTO 对象
        for (Bwic bwic : bwics) {
            BwicRecordResponseDTO dto = modelMapper.map(bwic, BwicRecordResponseDTO.class);
            Bond bond = bondMap.get(bwic.getBondId());
            if (bond != null) {
                dto.setCusip(bond.getCusip());
                dto.setIssuer(bond.getIssuer());
                dto.setRating(bond.getRating());
                dto.setCoupon(bond.getCoupon());
            }
            responseDTOs.add(dto);
        }

        return responseDTOs;
    }


    /**
     * 根據bwicId查詢用戶的排名
     * @param bwicId
     * @param accountId
     * @return
     */
    public Long getUserRankByBwicId(Long bwicId,Long accountId) {
        return bidClient.getUserRank(bwicId,accountId).getBody();
    }

    public List<Bwic> getAllBwics(){
        return bwicRepository.findAll();
    }





    public void updateBwicFullRecord(Long bwicId, BwicUpcomingFullRecord record) {
        Bwic bwic = bwicRepository.findById(bwicId)
                .orElseThrow(() -> new RuntimeException("Bwic not found"));

        bwic.setBondId(record.getBondId());
        bwic.setSize(record.getSize());
        bwic.setStartPrice(record.getStartPrice());
        bwic.setStartTime(record.getStartTime());
        bwic.setDueTime(record.getDueTime());

        bwicRepository.save(bwic);
    }

    public void deleteBwic(Long bwicId) {
        if(bwicRepository.existsById(bwicId)) {
            bwicRepository.deleteById(bwicId);
        } else {
            throw new RuntimeException("Bwic not found");
        }
    }

}