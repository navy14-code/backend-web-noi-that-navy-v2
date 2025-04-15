package com.example.doan.service;

import com.example.doan.modal.Deal;
import com.example.doan.repository.DealRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealService  {
    List<Deal> getDeals(Deal deal);
    Deal createDeal(Deal deal);
    Deal updateDeal(Deal deal, Long id) throws Exception;
    void deleteDeal(Long id) throws Exception;
}
