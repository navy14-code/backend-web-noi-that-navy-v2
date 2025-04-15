package com.example.doan.service;

import com.example.doan.modal.Home;
import com.example.doan.modal.HomeCategory;


import java.util.List;

public interface HomeService {
    public Home createHomePageData(List<HomeCategory> allCategories);
}
