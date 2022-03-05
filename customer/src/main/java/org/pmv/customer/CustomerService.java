package org.pmv.customer;

import lombok.AllArgsConstructor;
import org.pmv.clients.fraud.FraudCheckResponse;
import org.pmv.clients.fraud.FraudClient;
import org.pmv.clients.notifications.NotificationClient;
import org.pmv.clients.notifications.NotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email()).build();
        // todo: check if email valid
        // todo: check if email not taken

        // todo: check if fraudster
        customerRepository.saveAndFlush(customer); // It allows us to access customer id
        /**
         * Without using Open Feign.
         */
//        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
//                "http://FRAUD/api/v1/fraud-check/{customerId}",
//                FraudCheckResponse.class, customer.getId());
        /**
         * Better implementation using Open Feign
         */
        FraudCheckResponse response = fraudClient.isFraudster(customer.getId());

        if(response.isFraudster()){
            throw new IllegalStateException("fraudster customer");
        } else {
            // todo: send notification
            // todo: make it async. add to queue
            NotificationRequest notificationRequest =
                    new NotificationRequest(customer.getId(),customer.getEmail(),String.format("Hi %s, welcome!!",customer.getFirstName()));
            notificationClient.sendNotification(notificationRequest);
        }
    }
}
