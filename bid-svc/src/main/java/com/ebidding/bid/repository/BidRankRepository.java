package com.ebidding.bid.repository;

import com.ebidding.bid.domain.Bid;
import com.ebidding.bid.domain.BidRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface BidRankRepository extends JpaRepository<BidRank, Long> {

    @Query(nativeQuery = true, value = "" +
            "SELECT COUNT(*)+1 \n" +
            "FROM bidrank \n" +
            "WHERE bwic_id = :bwicId \n" +
            "AND account_id != :accountId \n" +
            "AND (price > (SELECT price FROM bidrank WHERE bwic_id = :bwicId AND account_id = :accountId) \n" +
            "OR (price = (SELECT price FROM bidrank WHERE bwic_id = :bwicId AND account_id = :accountId) AND time < (SELECT time FROM bidrank WHERE bwic_id = :bwicId AND account_id = :accountId)))\n")
    Long getRanking(@Param("bwicId") Long bwicId, @Param("accountId") Long accountId);

    @Query(value = "SELECT * FROM bidrank WHERE bwic_id = :bwicId AND account_id = :accountId", nativeQuery = true)
    Optional<BidRank> findByBwicIdAndAccountId(@Param("bwicId") Long bwicId, @Param("accountId") Long accountId);

    @Query(value = "SELECT COUNT(*) FROM bidrank WHERE bwic_id = :bwicId", nativeQuery = true)
    Long countByBwicId(@Param("bwicId") Long bwicId);

    @Query(nativeQuery = true, value = "SELECT price FROM bidrank WHERE bwic_id = :bwicId  ORDER BY price DESC LIMIT 1, 1")
    Optional<Double> getSecondHighestPrice(@Param("bwicId") Long bwicId);

    @Query(nativeQuery = true, value = "SELECT * FROM bidrank WHERE bwic_id = :bwicId  ORDER BY price DESC")
    Optional<List<BidRank>> getListByBwicid(@Param("bwicId") Long bwicId);

    @Query(nativeQuery = true,value="SELECT * FROM bidrank WHERE bwic_id = :bwicId ORDER BY price DESC")
    List<BidRank> getByBwicIdOrderByPriceDesc(@Param("bwicId") Long bwicId);
}


