/**
 * REST controller for handling user-related operations.
 * This class provides endpoints for managing user data.
 */
package com.project.SuperC.controller;

import com.project.SuperC.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private UserService userService;

    /**
     * Constructs a UserController with the necessary UserService.
     * @param userService The service responsible for user management logic.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

}
