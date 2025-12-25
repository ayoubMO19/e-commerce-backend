package com.vexa.ecommerce.Users;

import com.vexa.ecommerce.Exceptions.ResourceNotFoundException;
import com.vexa.ecommerce.Users.DTOs.UpdateUserRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    UsersRepository usersRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void getAllUsers_ShouldGotUsers() {
        // Preparación de datos
        Users user = new Users( "name", "surname", "email@email.com", true, "password", Role.USER);
        user.setUserId(1);
        Users user2 = new Users( "name2", "surname2", "email2@email.com", false, "password2", Role.ADMIN);
        user2.setUserId(2);
        List<Users> usersList = List.of(user, user2);

        when(usersRepository.findAll()).thenReturn(usersList);
        List<Users> obtainedUsersList = usersService.getAllUsers();

        // Comprobaciones del resultado
        assertEquals(usersList.size(), obtainedUsersList.size());
        assertEquals(usersList.get(0).getUserId(), obtainedUsersList.get(0).getUserId());
    }

    @Test
    void saveNewUser_ShouldSaveUser() {
        // Preparación de datos
        Users user = new Users();
        user.setUserId(1);
        user.setName("name");
        user.setSurname("surname");
        user.setEmail("email@email.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setHasWelcomeDiscount(true);

        // Ejecución de lógica
        when(usersRepository.save(user)).thenReturn(user);
        Users addedUser = usersService.saveNewUser(user);

        // Comprobaciones del resultado
        assertNotNull(addedUser); // Comprobar que el user guardado no sea null
        assertEquals(user.getUserId(), addedUser.getUserId()); // Comprobar que el ID del user guardado y el proporcionado son iguales
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        // Preparación de datos
        Users user = new Users();
        user.setUserId(1);
        Optional<Users> usersOptional = Optional.of(user);

        // Ejecución de lógica
        when(usersRepository.findById(user.getUserId())).thenReturn(usersOptional);
        Users obtainedUser = usersService.getUserById(user.getUserId());

        // Comprobaciones del resultado
        assertNotNull(obtainedUser);
        assertEquals(usersOptional.get().getUserId(), obtainedUser.getUserId());
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {
        // Preparación de datos
        Users user = new Users();
        user.setUserId(1);

        // Ejecución de lógica
        when(usersRepository.findById(user.getUserId())).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, ()-> {
                usersService.getUserById(user.getUserId());
        });

        // Comprobaciones del resultado
        Assertions.assertEquals("User with id 1 not found", resourceNotFoundException.getMessage());
    }

    @Test
    void updateUser_shouldUpdateSuccessfully() {
        // Preparación de datos
        Users user = new Users( "name", "surname", "email@email.com", true, "password", Role.USER);
        user.setUserId(1);
        Optional<Users> usersOptional = Optional.of(user);
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO("nameUpdated", user.getSurname(), user.getEmail(), user.getHasWelcomeDiscount());

        // Ejecución de lógica
        when(usersRepository.findById(user.getUserId())).thenReturn(usersOptional);
        when(usersRepository.existsByEmailAndUserIdNot(dto.email(), user.getUserId())).thenReturn(false);
        when(usersRepository.save(user)).thenReturn(user);
        Users updatedUser = usersService.updateUser(user.getUserId(), dto);

        // Comprobaciones de resultado
        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(user.getUserId(), updatedUser.getUserId());
        Assertions.assertNotEquals("name", updatedUser.getName());
        Assertions.assertEquals("nameUpdated", updatedUser.getName());
    }

    @Test
    void deleteUserById() {
    }
}