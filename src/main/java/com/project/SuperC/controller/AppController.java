package com.project.SuperC.controller;

import com.project.SuperC.models.Request; // DailyPriceChecker is not directly used in the controller, so it's not needed here.
import com.project.SuperC.service.AppService;
import lombok.AllArgsConstructor; // For constructor injection
import lombok.extern.slf4j.Slf4j; // For logging
import org.springframework.http.HttpStatus; // For HTTP status codes
import org.springframework.http.ResponseEntity; // For building HTTP responses
import org.springframework.web.bind.annotation.*; // For REST controller annotations

import java.util.List;

/**
 * REST Controller for handling API requests related to price tracking.
 * Exposes endpoints for adding new price tracking requests and triggering daily price checks.
 * Uses Lombok for logging and constructor-based dependency injection.
 */
@RestController // Marks this class as a REST controller, combining @Controller and @ResponseBody.
@Slf4j // Lombok annotation to generate a logger instance named 'log'.
@AllArgsConstructor // Lombok annotation to generate a constructor with all final fields, enabling dependency injection.
@RequestMapping("/app") // Base path for all endpoints in this controller.
public class AppController {
    /**
     * Injected instance of {@link AppService} to handle business logic.
     * Marked as final because it's injected via constructor.
     */
    private final AppService appService;

    /**
     * Handles POST requests to add a new price tracking request.
     *
     * @param request The {@link Request} object received in the request body (JSON).
     * @return A {@link ResponseEntity} containing the created {@link Request} object
     * and an HTTP status of 201 (Created).
     */
    @PostMapping("/add") // Maps POST requests to /app/add.
    public ResponseEntity <Request> signUp(@RequestBody Request request){
        log.info("Received request to add a new price tracking entry: {}", request); // Log incoming request.
        Request createdRequest = appService.signUserEntryToDB(request); // Delegate to service layer to save.
        // Return the saved request with a 201 Created status.
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    /**
     * Handles GET requests to trigger a daily price check for all tracked products.
     * This endpoint retrieves all configured requests and initiates the web scraping
     * and notification process via the service layer.
     *
     * @return A list of all {@link Request} objects fetched from the database.
     */
    @GetMapping ("/dailyPriceCheck") // Maps GET requests to /app/dailyPriceCheck.
    public List<Request> checkPricesToday(){
        // Log the action.
        log.info("Triggering daily price check for all tracked products.");
        // Delegate to service layer to get products and initiate price checks.
        return appService.getProductNumbersAndMaxPricesFromDB();
    }
}