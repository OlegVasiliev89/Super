package com.project.SuperC.service;

import com.project.SuperC.models.DailyPriceChecker;
import com.project.SuperC.models.Request;
import com.project.SuperC.repository.ProductRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service layer component for handling business logic related to price tracking requests.
 */
@Component
public class RequestAndPriceCheckService {
    private final ProductRequestRepository productRequestRepository;
    private final DailyPriceChecker dailyPriceChecker;

    @Autowired // Marks this constructor for Spring's dependency injection.
    public RequestAndPriceCheckService(ProductRequestRepository productRequestRepository, DailyPriceChecker dailyPriceChecker) {
        this.productRequestRepository = productRequestRepository;
        this.dailyPriceChecker = dailyPriceChecker;
    }

    /**
     * Saves a new price tracking request to the database.
     */
    public Request signUserEntryToDB(Request request){
        productRequestRepository.save(request);
        return request;
    }

    /**
     * Retrieves all price tracking requests from the database and initiates the daily price check.
     */
    public List<Request> getProductNumbersAndMaxPricesFromDB(){
        List<Request> returnedList = productRequestRepository.findAll();
        System.out.println("Fetched requests from DB: " + returnedList);

        dailyPriceChecker.priceFetcher(returnedList);

        return returnedList;
    }
}