package com.project.SuperC.service;

import com.project.SuperC.models.DailyPriceChecker;
import com.project.SuperC.models.Request;
import com.project.SuperC.repository.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component; // @Service is typically used for business logic components

import java.util.List;

/**
 * Service layer component for handling business logic related to price tracking requests.
 * It acts as an intermediary between the Controller and the Repository,
 * and orchestrates interactions with the web scraping component (DailyPriceChecker).
 */
@Component // Marks this class as a Spring component (could also use @Service for semantic clarity).
public class AppService {
    /**
     * Injected instance of {@link AppRepository} for database operations on {@link Request} entities.
     */
    private final AppRepository appRepository;

    /**
     * Injected instance of {@link DailyPriceChecker} for performing web scraping and price checks.
     */
    private final DailyPriceChecker dailyPriceChecker;

    /**
     * Constructor for {@code AppService}. Spring automatically injects required dependencies.
     *
     * @param appRepository The repository for Request entities.
     * @param dailyPriceChecker The component for daily price checking.
     */
    @Autowired // Marks this constructor for Spring's dependency injection.
    public AppService(AppRepository appRepository, DailyPriceChecker dailyPriceChecker) {
        this.appRepository = appRepository;
        this.dailyPriceChecker = dailyPriceChecker;
    }

    /**
     * Saves a new price tracking request to the database.
     *
     * @param request The {@link Request} object containing user email, product number, and max price.
     * @return The saved {@link Request} object, typically with its ID populated.
     */
    public Request signUserEntryToDB(Request request){
        appRepository.save(request); // Persists the request entity to the database.
        return request; // Returns the saved request.
    }

    /**
     * Retrieves all price tracking requests from the database and initiates the daily price check.
     * This method fetches all stored requests and then delegates to {@link DailyPriceChecker}
     * to perform the web scraping and send notifications.
     *
     * @return A list of all {@link Request} objects retrieved from the database.
     */
    public List<Request> getProductNumbersAndMaxPricesFromDB(){
        // Fetch all price tracking requests from the database.
        List<Request> returnedList = appRepository.findAll();
        System.out.println("Fetched requests from DB: " + returnedList); // Improved logging

        // Delegate the actual price checking and email sending logic to DailyPriceChecker.
        dailyPriceChecker.priceFetcher(returnedList);

        // Return the list of requests. (Returning the already fetched list is more efficient)
        return returnedList;
    }
}