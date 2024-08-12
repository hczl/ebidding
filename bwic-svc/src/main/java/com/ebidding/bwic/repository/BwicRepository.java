package com.ebidding.bwic.repository;

import com.ebidding.bwic.api.BwicDTO;
import com.ebidding.bwic.domain.Bwic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface BwicRepository extends JpaRepository<Bwic, Long> {
    //SELECT * FROM BOND WHERE CUSIP = ?
//    Optional<Bwic> findByCusip(String cusip);

    //SELECT * FROM BOND WHERE BWIC_ID = ?
    Optional<Bwic> findByBwicId(long bwicId);

    Optional<Bwic> findByBwicId(Long bwicId);
    //
////    @Query(nativeQuery = true,
////            value = "SELECT bwic.* FROM bwic, bond WHERE bond.cusip = :cusip in (SELECT cusip= :cusip from bond, bwic WHERE bwic.bond_id = bond.bond_id)")
////    Optional<List<Bwic>> findByCusip(@Param("cusip") String cusip);
//
    Optional<Bwic> findByBondId(String bondId);
    List<Bwic> findAllByDueTimeAfterOrderByDueTimeAsc(Timestamp time);

    List<Bwic> findAllByDueTimeBeforeOrderByDueTimeDesc(Timestamp time);

    @Modifying  //告诉JPA这是一个update或者delete操作，可能会产生脏数据
    @Query("UPDATE Bwic b SET b.bidCounts = b.bidCounts + 1 WHERE b.bwicId = :bwicId")
    void incrementBidCount(@Param("bwicId") Long bwicId);




    @Query("SELECT b FROM Bwic b WHERE b.startTime <= CURRENT_TIMESTAMP AND b.dueTime >= CURRENT_TIMESTAMP ORDER BY b.startTime ASC")
    List<Bwic> findOngoingBwics();

    @Query("SELECT b FROM Bwic b WHERE b.startTime > CURRENT_TIMESTAMP ORDER BY b.startTime ASC")
    List<Bwic> findUpcomingBwics();

    @Query("SELECT b FROM Bwic b WHERE b.dueTime < CURRENT_TIMESTAMP ORDER BY b.dueTime ASC")
    List<Bwic> findEndedBwics();


}
