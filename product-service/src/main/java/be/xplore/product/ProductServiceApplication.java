package be.xplore.product;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.List;

@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner generateTestData(ProductRepository repository) {
        return args -> {
            repository.save(new Product("iPhone XS"));
            repository.save(new Product("Samsung Galaxy"));
            repository.save(new Product("Google Pixel"));
        };
    }

}

@Data
@Entity
@Table(name = "product_tbl")
class Product {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    Product() {
    }

    Product(String name) {
        this.name = name;
    }
}

interface ProductRepository extends JpaRepository<Product, Long> {
}

@RestController
@RequestMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
class ProductController {
    private final ProductRepository productRepository;

    ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    List<Product> getProducts() {
        log.info("getProducts");

        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    Product getProduct(@PathVariable("id") Long id) {
        log.info("getProduct {}", id);

        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));
    }
}


