package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Users.DTOs.UpdateUserRequestDTO;

import java.util.List;

public interface IUsersService {

    List<Users> getAllUsers();

    Users getUserById(Integer id);

    Users saveNewUser(Users user);

    Users updateUser(Integer userId, UpdateUserRequestDTO userRequestDTO);

    void deleteUserById(Integer id);
}
