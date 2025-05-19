package com.matchalah.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class ProductCheckService {

    private static final Logger log = LoggerFactory.getLogger(ProductCheckService.class);

    private static final String PRODUCT_URL = "https://horiishichimeien.com/products/matcha-todounomukashi";
    //private static final String PRODUCT_URL = "https://horiishichimeien.com/en/products/sencha-homarenokaori";

    private static final Random random = new Random();

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @Autowired
    public ProductCheckService(SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    // Array of common user agents
    private static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1"
    };

    // Array of common referrer URLs
    private static final String[] REFERRERS = {
            "https://www.google.com/",
            "https://www.bing.com/",
            "https://www.facebook.com/",
            "https://twitter.com/",
            "https://www.reddit.com/",
            "https://www.instagram.com/",
            "https://www.amazon.com/",
            "https://www.youtube.com/"
    };

    // Array of common accept-language values
    private static final String[] ACCEPT_LANGUAGES = {
            "en-US,en;q=0.9",
            "en-GB,en;q=0.8,en-US;q=0.7",
            "en-CA,en-US;q=0.9,en;q=0.8",
            "es-ES,es;q=0.9,en;q=0.8",
            "fr-FR,fr;q=0.9,en;q=0.8",
            "de-DE,de;q=0.9,en;q=0.8",
            "it-IT,it;q=0.9,en;q=0.8",
            "pt-BR,pt;q=0.9,en;q=0.8"
    };

    /**
     * Get a random referrer URL
     */
    private static String getRandomReferrer() {
        return REFERRERS[random.nextInt(REFERRERS.length)];
    }

    /**
     * Get a random Accept-Language value
     */
    private static String getRandomLanguage() {
        return ACCEPT_LANGUAGES[random.nextInt(ACCEPT_LANGUAGES.length)];
    }

    /**
     * Generate random cookies
     */
    private static Map<String, String> getRandomCookies() {
        Map<String, String> cookies = new HashMap<>();
        // 50% chance to add session cookie
        if (random.nextBoolean()) {
            cookies.put("session_id", generateRandomString(16));
        }
        // Add random tracking cookies
        cookies.put("_ga", "GA1." + (1 + random.nextInt(2)) + "." + random.nextInt(1000000000) + "." + random.nextInt(1000000000));

        // Random preference cookies
        if (random.nextBoolean()) {
            cookies.put("theme", random.nextBoolean() ? "light" : "dark");
        }
        if (random.nextBoolean()) {
            cookies.put("last_visit", String.valueOf(System.currentTimeMillis() - random.nextInt(604800000))); // Within last week
        }

        return cookies;
    }

    /**
     * Generate a random alphanumeric string
     */
    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Scheduled(cron = "*/30 * * * * ?")  //every 3 minutes
    public void checkProductAvailability() {

        log.info("Checking product availability for: {}", PRODUCT_URL);

        try {
            boolean inStock = isProductInStock();
            log.info("Product is currently {} stock", inStock ? "in" : "out of");

             // Notify when the product becomes available or on first check if available
            if (inStock) {
                notificationService.sendNotification(true);
            }else {
                notificationService.sendNotification(false);
            }

        } catch (Exception e) {
            log.error("Error checking stock: {}", e.getMessage(), e);
        }
    }

    private boolean isProductInStock() throws IOException {
        // Get a random user agent
        String userAgent = USER_AGENTS[random.nextInt(USER_AGENTS.length)];

        // Add random jitter to the connection timeout (8-12 seconds)
        int timeout = 8000 + random.nextInt(4000);

        // Generate random values for request attributes
        boolean randomMobile = random.nextBoolean();
        String referrer = getRandomReferrer();
        String acceptLanguage = getRandomLanguage();

        // Make the request with more humanized attributes
        Document document = Jsoup.connect(PRODUCT_URL)
                .userAgent(userAgent)
                .timeout(timeout)
                .referrer(referrer)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", acceptLanguage)
                .header("DNT", random.nextBoolean() ? "1" : null)
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Cache-Control", random.nextBoolean() ? "max-age=0" : "no-cache")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", random.nextBoolean() ? "none" : "same-origin")
                .header("Sec-Fetch-User", "?1")
                .header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"" + (90 + random.nextInt(15)) + "\"")
                .header("sec-ch-ua-mobile", randomMobile ? "?1" : "?0")
                .header("sec-ch-ua-platform", randomMobile ?
                        (random.nextBoolean() ? "\"Android\"" : "\"iOS\"") :
                        (random.nextBoolean() ? "\"Windows\"" : "\"macOS\""))
                .followRedirects(true)
                .maxBodySize(0)
                .ignoreHttpErrors(true)
                .ignoreContentType(false)
                .cookies(getRandomCookies())
                .get();

        // Looking for "sold out" indicators in Japanese or potential elements that indicate out of stock
        // 売り切れ is "sold out" in Japanese
        boolean soldOutTextPresent = document.body().text().contains("売り切れ") ||
                document.body().text().contains("在庫切れ") ||
                document.body().text().contains("在庫なし");

        // Look for specific elements that might indicate sold out status
        boolean soldOutElementsPresent = !document.select("button.disabled, button[disabled], .sold-out, .out-of-stock").isEmpty();

        //if soldOut is present product is not in stock
        if(soldOutTextPresent || soldOutElementsPresent){
            //product not in stock
            return false;
        }else{
            // product is in stock
            return true;
        }
    }

}
