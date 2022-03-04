package org.pmv.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;

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
         * Esta comunicación se hará con service discovery utilizando EUREKA SERVER en lugar
         * de utilizar RestTemplate, ya que esta opción requiere excesivo mantenimiento a medida
         * que la aplicación crece.
         */
        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
                "http://FRAUD/api/v1/fraud-check/{customerId}",
                FraudCheckResponse.class, customer.getId());

        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster customer");
        }
        // todo: send notification
    }
}
