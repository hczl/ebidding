package com.ebidding.bid.repository;

import com.ebidding.bid.domain.Bid;
import com.ebidding.bid.domain.BidRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findByBidId(Long bidId);


    @Query(value = "select * from bid where bwic_id = ?1 order by time desc",nativeQuery = true)
    List<Bid> getListByBwicid(Long bwicid);

    @Query(value = "select * from bid where bwic_id = ?1 order by ranking limit 1",nativeQuery = true)
    Bid getSuccesBidByBwicid(Long bwicid);




    @Query(value = "SELECT * FROM bid WHERE bwic_id = ?1 AND account_id = ?2", nativeQuery = true)
    List<Bid> getBidByBwicIdAndAccountId(Long bwicId,Long accountId);

    @Query(value = "SELECT bwic_id FROM bid WHERE account_id = ?1", nativeQuery = true)
    List<Long> getBwicIdListByAccountId(Long accountId);
}
