package com.example.doan.request;

import com.example.doan.modal.Address;
import lombok.Data;

import java.util.List;

@Data
public class UserRequest {
    private String fullName;
    private String phone;
    private List<Address> address;
}
