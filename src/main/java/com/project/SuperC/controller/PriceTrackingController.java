    package com.project.SuperC.controller;

    import com.project.SuperC.models.PriceTrackingRequest;
    import com.project.SuperC.service.PriceTrackingService;
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
    public class PriceTrackingController {
        /**
         * Injected instance of AppService to handle business logic
         */
        private final PriceTrackingService priceTrackingService;

        /**
         * Handles POST requests to add a new price tracking request.
         * @param priceTrackingRequest The {@link PriceTrackingRequest} object containing the user's price tracking details.
         * @return A {@link ResponseEntity} containing the created {@link PriceTrackingRequest} object
         * and an HTTP status of 201 Created.
         */
        @PostMapping("/add")
        public ResponseEntity <PriceTrackingRequest> signUp(@RequestBody PriceTrackingRequest priceTrackingRequest){ // TODO: need to change the name of "signUp"
            log.info("Received request to add a new price tracking entry: {}", priceTrackingRequest);
            PriceTrackingRequest createdPriceTrackingRequest = priceTrackingService.signUserEntryToDB(priceTrackingRequest);
            return new ResponseEntity<>(createdPriceTrackingRequest, HttpStatus.CREATED);
        }

        /**
         * Triggers a price check for all tracked products.
         * This endpoint starts the interaction with the DB to identify and return a list
         * of all products that are currently below their registered maximum price threshold.
         *
         * @return A {@link List} of {@link PriceTrackingRequest} objects representing products whose
         * current price is at or below the user-defined maximum price threshold in the database.
         */
        @GetMapping ("/dailyPriceCheck")
        public List<PriceTrackingRequest> checkPricesToday(){
            log.info("Triggering daily price check for all tracked products.");
            return priceTrackingService.getProductNumbersAndMaxPricesFromDB();
            //TODO: call fetchPrice here
            //TODO: call email sending here, EmailSendingService
        }
    }