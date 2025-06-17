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
    /**
     * Repository to perform CRUD operations on the DB
     * Injected via constructor for dependency management
     */
    private final ProductRequestRepository productRequestRepository;
    /**
     * A component used with the list of Requests to start the price comparison process
     */
    private final DailyPriceChecker dailyPriceChecker;

    /**
     * A constructor that take the injected productRequestRepository and dailyPriceChecker
     * @param productRequestRepository the object to interact with the DB
     * @param dailyPriceChecker the object to connect requests from the DB and send them further into checking
     *                          if they are below the threshold and users should be notified
     */
    @Autowired
    public RequestAndPriceCheckService(ProductRequestRepository productRequestRepository, DailyPriceChecker dailyPriceChecker) {
        this.productRequestRepository = productRequestRepository;
        this.dailyPriceChecker = dailyPriceChecker;
    }

    /**
     * The method to add new requets into the DB
     * @param request an object created with each user submission
     * @return returns the object as confirmation of the request getting added into the DB
     */
    public Request signUserEntryToDB(Request request){
        productRequestRepository.save(request);
        return request;
    }

    /**
     * Find all the requests from the DB, makes it into a list so the list can be sent into
     * the {@link DailyPriceChecker} further to process into sending notifications on price alerts via email
     * @return returns a list of requests that were below the price threshold
     */
    public List<Request> getProductNumbersAndMaxPricesFromDB(){
        List<Request> returnedList = productRequestRepository.findAll();
        System.out.println("Fetched requests from DB: " + returnedList);

        dailyPriceChecker.priceFetcher(returnedList);

        return returnedList;
    }
}