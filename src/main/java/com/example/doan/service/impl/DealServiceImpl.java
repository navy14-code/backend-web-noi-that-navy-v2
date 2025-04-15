package com.example.doan.service.impl;

import com.example.doan.modal.Deal;
import com.example.doan.modal.HomeCategory;
import com.example.doan.repository.DealRepository;
import com.example.doan.repository.HomeCategoryRepository;
import com.example.doan.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {
    private final DealRepository dealRepository;
    private final HomeCategoryRepository homeCategoryRepository;
    @Override
    public List<Deal> getDeals(Deal deal) {
        return dealRepository.findAll();
    }

    @Override
    public Deal createDeal(Deal deal) {
        HomeCategory category = homeCategoryRepository.findById(deal.getHomeCategory().getId()).orElse(null);

        Deal newDeal = dealRepository.save(deal);

        newDeal.setHomeCategory(category);
        newDeal.setDiscount(deal.getDiscount());
        return dealRepository.save(newDeal);
    }

    @Override
    public Deal updateDeal(Deal deal, Long id) throws Exception {
        Deal existingDeal = dealRepository.findById(id).orElse(null);

        HomeCategory category = homeCategoryRepository.findById(deal.getHomeCategory().getId()).orElse(null);

        if (existingDeal != null) {
            if(deal.getDiscount() != null) {
                existingDeal.setDiscount(deal.getDiscount());
            }
            if(category != null) {
                existingDeal.setHomeCategory(category);
            }
            return dealRepository.save(existingDeal);
        }
        throw new Exception("Không tồn tại khuyến mãi");
    }

    @Override
    public void deleteDeal(Long id) throws Exception {
        Deal deal= dealRepository.findById(id).orElseThrow(()->
                new Exception("Không tìm thấy khuyễn mãi "));
        dealRepository.delete(deal);

    }
}
