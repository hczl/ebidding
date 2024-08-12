package com.ebidding.bwic.service;


import com.ebidding.bwic.domain.Bond;
import com.ebidding.bwic.repository.BondRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BondService {

    @Autowired
    private BondRepository bondRepository;


    public String findCuSipByBondId(String bondId) {
        return  this.bondRepository.findByBondId(bondId).orElse(null).getCusip();
    }

    public String findIssuerByBondId(String bondId) {
        return  this.bondRepository.findByBondId(bondId).orElse(null).getIssuer();
    }

    public String getBondid(String cusip) {
        return  this.bondRepository.getBondid(cusip).orElse(null);
    }

    @Transactional
    public void incrementTransactionCount(String bondId) {
        this.bondRepository.findByBondId(bondId).orElseThrow(() -> new RuntimeException("Bond not found: " + bondId));
        this.bondRepository.incrementTransactionCount(bondId);
    }

//    public List<Bond> findAllById(List<String> bondIds) {
//        // Convert bondIds from List<String> to List<Long>
//        List<Long> bondIdsLong = bondIds.stream()
//                                       .map(Long::valueOf)
//                                       .collect(Collectors.toList());
//
//        // Call the repository method with the converted bondIds
//        return bondRepository.findAllByBondIdIn(bondIdsLong);
//    }

    public List<Bond> getAllBonds() {
        return this.bondRepository.findAll();
    }

    public Optional<Bond> findById(String bondId) {
        return this.bondRepository.findByBondId(bondId);
    }

    public List<Bond> findAllById(List<String> bondIds) {
        return this.bondRepository.getAllById(bondIds);
    }
}
