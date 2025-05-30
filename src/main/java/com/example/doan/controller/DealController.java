package com.example.doan.controller;

import com.example.doan.modal.Deal;
import com.example.doan.repository.DealRepository;
import com.example.doan.response.ApiResponse;
import com.example.doan.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/deals")
public class DealController {
    private final DealService dealService;

    @PostMapping
    public ResponseEntity<Deal> createDeal(@RequestBody Deal deal) {
        Deal createdDeal = dealService.createDeal(deal);

        return new ResponseEntity<>(createdDeal, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Deal> updateDeal(
            @PathVariable Long id,
            @RequestBody Deal deal) throws Exception {
        Deal updatedDeal =  dealService.updateDeal(deal, id);
        return ResponseEntity.ok(updatedDeal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDeal(@PathVariable Long id) throws Exception {
        dealService.deleteDeal(id);

        ApiResponse res = new ApiResponse();
        res.setMessage("Xóa khuyến mãi thành công ");
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

}
