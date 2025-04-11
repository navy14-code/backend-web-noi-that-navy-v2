package com.example.doan.service.impl;

import com.example.doan.config.JwtProvider;
import com.example.doan.exceptions.UserException;
import com.example.doan.modal.Address;
import com.example.doan.modal.User;
import com.example.doan.repository.AddressRepository;
import com.example.doan.repository.UserRepository;
import com.example.doan.request.UserRequest;
import com.example.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    @Override
    public User getUserProfile(String jwt) throws Exception {

        String email= jwtProvider.getEmailFromJwtToken(jwt);

        return this.getUserByEmail(email);
    }

    @Override
    public User getUserByEmail(String email) throws Exception {
        User user= userRepository.findByEmail(email);
        if(user==null){
            throw new Exception("Không tìm thấy người dùng với email: "+ email);
        }
        return user;
    }

//    @Override
//    public User createUser(User user) throws Exception {
//        // Kiểm tra email đã tồn tại chưa
//        User userExists = userRepository.findByEmail(user.getEmail());
//        if (userExists != null) {
//            throw new Exception("Người dùng đã tồn tại, vui lòng sử dụng email khác.");
//        }
//
//        // Lưu địa chỉ
//        Set<Address> savedAddresses = new HashSet<>();
//        if (user.getAddresses() != null) {
//            for (Address address : user.getAddresses()) {
//                address.setUser(user); // Gán lại quan hệ
//                savedAddresses.add(addressRepository.save(address));
//            }
//        }
//
//        // Tạo user mới
//        User newUser = new User();
//        newUser.setEmail(user.getEmail());
//        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
//        newUser.setFullName(user.getFullName());
//        newUser.setPhone(user.getPhone());
//        newUser.setRole(user.getRole());
//        newUser.setAddresses(savedAddresses);
//
//        // Lưu user
//        return userRepository.save(newUser);
//    }


    @Override
    public User getUserById(Long id) throws UserException {
        return userRepository.findById(id).orElseThrow(()-> new UserException("Người dùng không tồn tại với id" + id));
    }
    

//    @Override
//    public List<User> getAllUsers(AccountStatus status) {
//        return userRepository.findByAccountStatus(status);
//    }

    @Override
    public User updateUser(Long id, UserRequest req) throws Exception {

        User existingUser = this.getUserById(id);
        if(req.getFullName() != null){
            existingUser.setFullName(req.getFullName());
        }
        if(req.getPhone() != null){
            existingUser.setPhone(req.getPhone());
        }
        Address newAddress = req.getAddress().stream().findFirst().orElse(null);
        Address existingAddress = existingUser.getAddresses().stream().findFirst().orElse(null);

        if (newAddress != null && existingAddress != null) {
            if (newAddress.getDetailAddress() != null)
                existingAddress.setDetailAddress(newAddress.getDetailAddress());

            if (newAddress.getCity() != null)
                existingAddress.setCity(newAddress.getCity());

            if (newAddress.getState() != null)
                existingAddress.setState(newAddress.getState());

            if (newAddress.getPhone() != null)
                existingAddress.setPhone(newAddress.getPhone());

            if (newAddress.getLocality() != null)
                existingAddress.setLocality(newAddress.getLocality());

            if (newAddress.getName() != null)
                existingAddress.setName(newAddress.getName());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) throws Exception {
        User user = this.getUserById(id);
        userRepository.delete(user);

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
