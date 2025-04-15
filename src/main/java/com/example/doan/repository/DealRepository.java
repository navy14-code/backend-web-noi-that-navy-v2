package com.example.doan.repository;

import com.example.doan.modal.Deal;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;


import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Long> {
}
