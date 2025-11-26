package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsersService implements IUsersService {
    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public List<Users> getAllUsers() {
        return this.usersRepository.findAll();
    }

    @Override
    public Users getUserById(Integer id) {
        return this.usersRepository.findById(id).orElseThrow(() -> {
            return new ResourceNotFoundException("User", id);
        });
    }

    @Override
    public Users saveNewUser(Users user) {
        return this.usersRepository.save(user);
    }

    @Override
    public Users updateUser(Users user) {
        this.usersRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", user.getUserId()));

        return this.usersRepository.save(user);
    }

    @Override
    public void deleteUserById(Integer id) {
        if(!this.usersRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        this.usersRepository.deleteById(id);
    }

}
