package com.project.SuperC.models;

import com.project.SuperC.service.EmailSenderService;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Component responsible for daily price checking of products on Super C's website
 * and sends email notifications if a product's current price is at or below
 * the user's specified maximum desired price.
 */
@Component // Marks this class as a Spring component, allowing it to be injected.
@Data
public class DailyPriceChecker {
    /**
     * Injected instance of EmailSenderService to send price notification emails.
     */
    private final EmailSenderService emailSenderService;

    @Autowired
    public DailyPriceChecker(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    /**
     * Fetches current prices for a list of requested products from the Super C website.
     * If a product's current price is less than or equal to the user's desired maximum price,
     * an email notification is sent to the user.
     * @param sqlList a {@link List} of {@link Request} objects.
     * @throws IOException if an I/O error occurs
     */
    public void priceFetcher(List<Request> sqlList) {

        Pattern pricePattern = Pattern.compile("(\\d+\\.\\d{2})");

        int count = 0; // Simple counter for logging purposes.

        for (Request item : sqlList) {
            String url = "https://www.superc.ca/en/search?filter=" + item.getProductNumber();

            try {
                Document document = Jsoup.connect(url).get();
                Elements products = document.select(".pt__content");

                for (Element product : products) {
                    String title = product.select(".head__title").text();
                    float currentProductPrice = 0.0f;

                    Element priceElement = product.selectFirst(".pricing__sale-price");

                    if (priceElement != null) {

                        String fullPriceText = priceElement.text().trim();

                        Matcher matcher = pricePattern.matcher(fullPriceText);

                        if (matcher.find()) {

                            String numericalPriceString = matcher.group(1);

                            try {
                                currentProductPrice = Float.parseFloat(numericalPriceString);

                            } catch (NumberFormatException e) {

                                System.err.println("Could not parse extracted price: " + numericalPriceString + " for product: " + title);
                            }
                        } else {

                            System.err.println("No numerical price found in: '" + fullPriceText + "' for product: " + title);
                        }
                    }

                    System.out.println("======================================");

                    count++;

                    if(currentProductPrice <= item.getMaxPrice()){

                        System.out.println(item.getEmail() + " Current price for " + title + " is " + currentProductPrice
                                + " they wanted to pay at most " + item.getMaxPrice());

                        emailSenderService.emailNotificationSender(item.getEmail(), title, currentProductPrice);
                    }
                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}