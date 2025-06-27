package com.project.SuperC.service;

import com.project.SuperC.models.PriceTrackingRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Component responsible for daily price checking of products on Super C's website
 * and sends email notifications if a product's current price is at or below
 * the user's specified maximum desired price.
 */
@Component
@Data
@Slf4j
public class DailyPriceChecker {

    private final EmailSenderService emailSenderService;


    public DailyPriceChecker(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    /**
     * Fetches current prices for a list of requested products from the Super C website.
     * If a product's current price is less than or equal to the user's desired maximum price,
     * an email notification is prepared.
     *
     * @param sqlList a {@link List} of {@link PriceTrackingRequest} objects.
     * @throws IOException .
     */
    public void fetchPrices(List<PriceTrackingRequest> sqlList) {

        Pattern pricePattern = Pattern.compile("(\\d+\\.\\d{2})");

        Map<String, List<String>> userPriceAlerts = new HashMap<>();

        for (PriceTrackingRequest item : sqlList) {
            String url = "https://www.superc.ca/en/search?filter=" + item.getProductNumber();
            String userEmail = item.getUser().getEmail();

            try {
                Document document = Jsoup.connect(url).get();
                Elements products = document.select(".pt__content");

                if (products.isEmpty()) {
                    log.warn("No products found for product number: {} at URL: {}", item.getProductNumber(), url);
                    continue;
                }

                for (Element product : products) {
                    String title = product.select(".head__title").text();
                    double currentProductPrice = 0.0;
                    Element priceElement = product.selectFirst(".pricing__sale-price");

                    if (priceElement != null) {
                        String fullPriceText = priceElement.text().trim();
                        Matcher matcher = pricePattern.matcher(fullPriceText);

                        if (matcher.find()) {
                            String numericalPriceString = matcher.group(1);
                            try {
                                currentProductPrice = Double.parseDouble(numericalPriceString);
                            } catch (NumberFormatException e) {
                                log.error("Could not parse extracted price: '{}' for product: '{}' from URL: {}", numericalPriceString, title, url, e);
                            }
                        } else {
                            log.warn("No numerical price found in: '{}' for product: '{}' from URL: {}", fullPriceText, title, url);
                        }
                    } else {
                        log.warn("Price element not found for product number: {} at URL: {}", item.getProductNumber(), url);
                    }

                    log.info("======================================");
                    log.info("Product: '{}', Current Price: {}, Max Desired Price: {}", title, currentProductPrice, item.getMaxPrice());


                    if (currentProductPrice > 0 && currentProductPrice <= item.getMaxPrice()) {
                        log.info("ALERT for {}: Current price for {} is {} (below or at desired {})", userEmail, title, currentProductPrice, item.getMaxPrice());
                        userPriceAlerts.computeIfAbsent(userEmail, k -> new ArrayList<>())
                                .add(String.format("- %s: Current price $%.2f (Desired: $%.2f)", title, currentProductPrice, item.getMaxPrice()));
                    }
                }
            } catch (IOException e) {
                log.error("Error fetching prices for product number {} from URL {}: {}", item.getProductNumber(), url, e.getMessage(), e);
            }
        }

        log.info("Sending price alert emails to users...");
        userPriceAlerts.forEach((email, alerts) -> {
            String subject = "Super C Price Alerts!";
            StringBuilder body = new StringBuilder("Dear Super C shopper,\n\n");
            body.append("Great news! Some products you're tracking are now at or below your desired price:\n\n");
            alerts.forEach(alert -> body.append(alert).append("\n"));
            body.append("\nHappy shopping!\nYour Super C Price Tracker");
            emailSenderService.sendNotificationsEmail(email, subject, body.toString());
            log.info("Sent consolidated email to {} with {} alerts.", email, alerts.size());
        });
        log.info("Finished sending price alert emails.");
    }
}