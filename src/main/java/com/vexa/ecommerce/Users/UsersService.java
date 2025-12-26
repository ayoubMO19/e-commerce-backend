package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import com.vexa.ecommerce.Users.DTOs.UpdateUserRequestDTO;
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
        if (usersRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email is already registered.");
        }

        return this.usersRepository.save(user);
    }

    @Override
    public Users updateUser(Integer userId, UpdateUserRequestDTO dto) {
        Users user = this.usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.surname() != null) {
            user.setSurname(dto.surname());
        }

        if (dto.email() != null) {
            if (usersRepository.existsByEmailAndUserIdNot(dto.email(), userId)) {
                throw new BadRequestException("Email is already registered");
            }
            user.setEmail(dto.email());
        }

        if (dto.hasWelcomeDiscount() != null) {
            user.setHasWelcomeDiscount(dto.hasWelcomeDiscount());
        }

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
