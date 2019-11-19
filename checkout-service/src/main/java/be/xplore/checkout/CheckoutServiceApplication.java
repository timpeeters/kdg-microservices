package be.xplore.checkout;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@SpringBootApplication
public class CheckoutServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckoutServiceApplication.class, args);
    }


    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Builder
@Data
class Order {
    private Long id;
    private Set<OrderLine> lines;
}

@Builder
@Data
class OrderLine {
    private Product product;
    private Integer amount;
}

@Data
class Product {
    private String name;
}

@RestController
@RequestMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
class CheckoutController {

    private final RestTemplate restTemplate;

    CheckoutController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{id}")
    Order getOrder(@PathVariable("id") Long id) {
        log.info("getOrder {}", id);

        return Order.builder()
                .id(id)
                .lines(Set.of(
                        OrderLine.builder()
                                .product(restTemplate.getForObject("http://product-service/products/1", Product.class))
                                .amount(1)
                                .build(),
                        OrderLine.builder()
                                .product(restTemplate.getForObject("http://product-service/products/2", Product.class))
                                .amount(2)
                                .build()
                )).build();
    }
}