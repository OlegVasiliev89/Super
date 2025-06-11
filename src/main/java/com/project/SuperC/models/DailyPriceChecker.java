package com.project.SuperC.models; // Consider moving to `com.project.SuperC.util` or `com.project.SuperC.component` for clarity

import com.project.SuperC.service.EmailSenderService;
import lombok.Data;
import org.jsoup.Jsoup; // Jsoup library for web scraping
import org.jsoup.nodes.Document; // Represents an HTML document
import org.jsoup.nodes.Element; // Represents an HTML element
import org.jsoup.select.Elements; // Represents a list of HTML elements
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException; // For handling network/IO errors during web scraping
import java.util.List;
import java.util.regex.Matcher; // For matching regular expressions
import java.util.regex.Pattern; // For compiling regular expressions

/**
 * Component responsible for daily price checking of products on Super C's website.
 * It scrapes product information (title, price) based on product numbers
 * and sends email notifications if a product's current price is at or below
 * the user's specified maximum desired price.
 */
@Component // Marks this class as a Spring component, allowing it to be injected.
@Data // Lombok annotation for boilerplate code (though mostly useful for data classes).
public class DailyPriceChecker {
    /**
     * Injected instance of {@link EmailSenderService} to send price notification emails.
     * Marked as final as it's initialized via constructor injection.
     */
    private final EmailSenderService emailSenderService;

    /**
     * Constructor for {@code DailyPriceChecker}. Spring automatically injects the
     * {@link EmailSenderService} dependency.
     *
     * @param emailSenderService The service used for sending emails.
     */
    @Autowired // Marks this constructor for Spring's dependency injection.
    public DailyPriceChecker(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    /**
     * Fetches current prices for a list of requested products from the Super C website.
     * If a product's current price is less than or equal to the user's desired maximum price,
     * an email notification is sent to the user.
     *
     * @param sqlList A list of {@link Request} objects, each containing product number,
     * user email, and maximum desired price.
     */
    public void priceFetcher(List<Request> sqlList) {

        // Regex pattern to extract a price in "XX.YY" format.
        // Group 1 captures the numerical part.
        Pattern pricePattern = Pattern.compile("(\\d+\\.\\d{2})");

        int count = 0; // Simple counter for logging purposes.

        // Iterate through each price tracking request from the database.
        for (Request item : sqlList) {
            // Construct the URL for searching the product on Super C's website.
            String url = "https://www.superc.ca/en/search?filter=" + item.getProductNumber();

            try {
                // Connect to the URL and parse the HTML document.
                Document document = Jsoup.connect(url).get();
                // Select elements representing individual product listings on the page.
                Elements products = document.select(".pt__content");

                // Loop through each found product element to extract details.
                for (Element product : products) {
                    // Extract the product title.
                    String title = product.select(".head__title").text();
                    float currentProductPrice = 0.0f; // Default price if not found or parse fails.

                    // Select the element containing the sale price.
                    Element priceElement = product.selectFirst(".pricing__sale-price");

                    // Check if a price element was found.
                    if (priceElement != null) {
                        String fullPriceText = priceElement.text().trim(); // Get text and remove leading/trailing whitespace.

                        // Attempt to find the price using the defined regex pattern.
                        Matcher matcher = pricePattern.matcher(fullPriceText);
                        if (matcher.find()) {
                            String numericalPriceString = matcher.group(1); // Extract the matched price string.
                            try {
                                // Convert the extracted string price to a float.
                                currentProductPrice = Float.parseFloat(numericalPriceString);
                            } catch (NumberFormatException e) {
                                // Log an error if the extracted string cannot be parsed as a float.
                                System.err.println("Could not parse extracted price: " + numericalPriceString + " for product: " + title);
                            }
                        } else {
                            // Log if no numerical price pattern was found in the text.
                            System.err.println("No numerical price found in: '" + fullPriceText + "' for product: " + title);
                        }
                    }

                    System.out.println("======================================");
                    count++;

                    // Check if the current scraped price meets the user's desired max price.
                    if(currentProductPrice <= item.getMaxPrice()){
                        // Log the price match and send an email notification.
                        System.out.println(item.getEmail() + " Current price for " + title + " is " + currentProductPrice
                                + " they wanted to pay at most " + item.getMaxPrice());
                        // Use the injected emailSenderService to send the notification.
                        emailSenderService.emailNotificationSender(item.getEmail(), title, currentProductPrice);
                    }
                }
            } catch (IOException e) {
                // Catch and print stack trace for network or I/O issues during web scraping.
                // In a production app, you might want more sophisticated error handling/logging.
                e.printStackTrace();
            }
        }
    }
}