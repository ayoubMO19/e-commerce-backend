package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Exceptions.BadRequestException;
import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import com.vexa.ecommerce.Users.DTOs.UpdateUserRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import static com.vexa.ecommerce.Utils.SecurityUtils.hideSecureEmail;

@Service
@Slf4j
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
        return this.usersRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("User with id {} not found", id);
                return new ResourceNotFoundException("User", id);
            });
    }

    @Override
    public Users saveNewUser(Users user) {
        if (usersRepository.existsByEmail(user.getEmail())) {
            log.warn("Email {} already exists. The user could not be saved", hideSecureEmail(user.getEmail()));
            throw new BadRequestException("Email is already registered.");
        }

        log.info("User with email {} has been created", hideSecureEmail(user.getEmail()));
        return this.usersRepository.save(user);
    }

    @Override
    public Users updateUser(Integer userId, UpdateUserRequestDTO dto) {
        Users user = this.usersRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("User with id {} not found. The user could not be updated", userId);
                return new ResourceNotFoundException("User", userId);
            });

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.surname() != null) {
            user.setSurname(dto.surname());
        }

        if (dto.email() != null) {
            if (usersRepository.existsByEmailAndUserIdNot(dto.email(), userId)) {
                log.warn("Email {} already registered. The user could not be updated", hideSecureEmail(dto.email()));
                throw new BadRequestException("Email is already registered");
            }
            user.setEmail(dto.email());
        }

        if (dto.hasWelcomeDiscount() != null) {
            user.setHasWelcomeDiscount(dto.hasWelcomeDiscount());
        }

        log.info("User with id {} has been updated", userId);
        return this.usersRepository.save(user);
    }

    @Override
    public void deleteUserById(Integer id) {
        if(!this.usersRepository.existsById(id)) {
            log.warn("User id {} not found. The user could not be deleted", id);
            throw new ResourceNotFoundException("User", id);
        }
        this.usersRepository.deleteById(id);
        log.info("User with id {} has been deleted", id);
    }
}
