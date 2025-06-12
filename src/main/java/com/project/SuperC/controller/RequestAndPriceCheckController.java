    package com.project.SuperC.controller;

    import com.project.SuperC.models.Request;
    import com.project.SuperC.service.AppService;
    import lombok.AllArgsConstructor;
    import lombok.extern.slf4j.Slf4j; // For logging
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    /**
     * REST Controller for handling API requests related to price tracking.
     * Exposes endpoints for adding new price tracking requests and triggering daily price checks.
     */
    @RestController // Marks this class as a REST controller, combining @Controller and @ResponseBody.
    @Slf4j
    @AllArgsConstructor
    @RequestMapping("/app")
    public class AppController {
        /**
         * Injected instance of AppService to handle business logic.
         * Marked as final because it's injected via constructor.
         */
        private final AppService appService;

        /**
         * Handles POST requests to add a new price tracking request.
         */
        @PostMapping("/add")
        public ResponseEntity <Request> signUp(@RequestBody Request request){
            log.info("Received request to add a new price tracking entry: {}", request); // Log incoming request for debugging.
            Request createdRequest = appService.signUserEntryToDB(request);
            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        }

        /**
         * Handles GET requests to trigger a daily price check for all tracked products.
         */
        @GetMapping ("/dailyPriceCheck")
        public List<Request> checkPricesToday(){
            log.info("Triggering daily price check for all tracked products.");
            return appService.getProductNumbersAndMaxPricesFromDB();
        }
    }