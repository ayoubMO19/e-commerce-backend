package com.vexa.ecommerce.Users;

import java.util.List;

public interface IUsersService {

    List<Users> getAllUsers();

    Users getUserById(Integer id);

    Users saveNewUser(Users user);

    Users updateUser(Users user);

    void deleteUserById(Integer id);
}
