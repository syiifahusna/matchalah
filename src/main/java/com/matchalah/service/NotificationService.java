package com.matchalah.service;

import com.matchalah.model.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final SimpMessagingTemplate messagingTemplate;
    // Track the last time a notification was sent
    private LocalDate lastNotificationDate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.lastNotificationDate = null;
    }

    public void sendNotification(boolean inStock){

        log.info("Product status : {}", inStock ? "Product is in stock. Will sent notification" : "Product is out of stock. No notification will be sent.");

        if(inStock){
            NotificationMessage notification = new NotificationMessage(
                    "Product is in stock. Please check now",
                    "https://horiishichimeien.com/products/matcha-todounomukashi",
                    inStock,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/matcha-notification", notification);
            log.info("Notification is sent to client");
        }

        /**
         *
         * Use this if send notification once a day
         *
        if(inStock){
            LocalDate today = LocalDate.now();

            // Check if we've already sent a notification today
            if (lastNotificationDate == null || !lastNotificationDate.equals(today)) {
                NotificationMessage notification = new NotificationMessage(
                        "Product is in stock. Please check now",
                        "https://horiishichimeien.com/products/matcha-todounomukashi",
                        inStock,
                        System.currentTimeMillis()
                );

                messagingTemplate.convertAndSend("/topic/matcha-notification", notification);
                log.info("Notification sent to client");

                // Update the last notification date
                lastNotificationDate = today;
            } else {
                log.info("Notification already sent today. Skipping.");
            }
        }
        */
    }


}
