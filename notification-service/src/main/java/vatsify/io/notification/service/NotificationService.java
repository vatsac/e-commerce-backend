package vatsify.io.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import vatsify.io.notification.order.event.OrderPlacedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "order-placed")
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        log.info("Got Message from order-placed topic {}", orderPlacedEvent);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("springshop@email.com");
            messageHelper.setTo(orderPlacedEvent.getEmail());
            messageHelper.setSubject(
                    String.format("Your Order with Order Number %s is placed successfully", orderPlacedEvent.getOrderNumber()));
            messageHelper.setText(
                    String.format("""
                            Hi %s,

                            Your order with order number %s has been placed successfully.

                            Best Regards,
                            Spring Shop
                            """, orderPlacedEvent.getEmail(), orderPlacedEvent.getOrderNumber())
            );
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Order Notification email sent!!");
        } catch (MailException e) {
            log.error("Exception occurred when sending mail", e);
            throw new RuntimeException("Exception occurred when sending mail to " + orderPlacedEvent.getEmail(), e);
        }
    }
}
