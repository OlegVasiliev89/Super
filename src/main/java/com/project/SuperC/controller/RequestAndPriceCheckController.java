    package com.project.SuperC.controller;

    import com.project.SuperC.models.Request;
    import com.project.SuperC.service.RequestAndPriceCheckService;
    import lombok.AllArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    /**
     * REST Controller for handling API requests related to price tracking.
     * Exposes endpoints for adding new price tracking requests and triggering daily price checks.
     */
    @RestController
    @Slf4j
    @AllArgsConstructor
    @RequestMapping("/app")
    public class RequestAndPriceCheckController {
        /**
         * Injected instance of AppService to handle business logic.
         * Marked as final because it's injected via constructor.
         */
        private final RequestAndPriceCheckService requestAndPriceCheckService;

        /**
         * Handles POST requests to add a new price tracking request.
         * @param request The {@link Request} object containing the user's price tracking details.
         * @return A {@link ResponseEntity} containing the created {@link Request} object
         * and an HTTP status of 201 Created.
         */
        @PostMapping("/add")
        public ResponseEntity <Request> signUp(@RequestBody Request request){
            log.info("Received request to add a new price tracking entry: {}", request); // Log incoming request for debugging.
            Request createdRequest = requestAndPriceCheckService.signUserEntryToDB(request);
            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        }

        /**
         * Handles GET requests to trigger a daily price check for all tracked products.
         * This endpoint starts the interaction with the DB to identify and return a list
         * of all products that are currently below their registered maximum price threshold.
         *
         * @return A {@link List} of {@link Request} objects representing products whose
         * current price is at or below the user-defined maximum price threshold in the database.
         */
        @GetMapping ("/dailyPriceCheck")
        public List<Request> checkPricesToday(){
            log.info("Triggering daily price check for all tracked products.");
            return requestAndPriceCheckService.getProductNumbersAndMaxPricesFromDB();
        }
    }