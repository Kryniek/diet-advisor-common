package pl.dietadvisor.common.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.product.model.dynamodb.Product;
import pl.dietadvisor.common.product.repository.dynamodb.ProductRepository;
import pl.dietadvisor.common.shared.exception.custom.BadRequestException;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static pl.dietadvisor.common.product.enums.ProductSource.USER;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public List<Product> get() {
        return (List<Product>) repository.findAll();
    }

    @Cacheable(value = "products", key = "#id")
    public Product getById(String id) {
        return repository.findById(id).orElseThrow();
    }

    public Product create(Product product) {
        if (nonNull(getByName(product.getName()))) {
            throw new BadRequestException("Product: %s already exist.", product.getName());
        }

        return repository.save(Product.builder()
                .source(USER)
                .name(product.getName())
                .kcal(product.getKcal())
                .proteins(product.getProteins())
                .carbohydrates(product.getCarbohydrates())
                .fats(product.getFats())
                .createdAt(now())
                .build());
    }

    private Product getByName(String name) {
        return repository.findByName(name);
    }

    public List<Product> create(List<Product> products) {
        products.forEach(product -> {
            product.setId(null);
            product.setCreatedAt(now());
        });

        return (List<Product>) repository.saveAll(products);
    }

    @CachePut(value = "products", key = "#product.id")
    public Product update(Product product) {
        Product existingProduct = getById(product.getId());
        if (isNotBlank(product.getName())) {
            existingProduct.setName(product.getName());
        }
        if (nonNull(product.getKcal())) {
            existingProduct.setKcal(product.getKcal());
        }
        if (nonNull(product.getProteins())) {
            existingProduct.setProteins(product.getProteins());
        }
        if (nonNull(product.getCarbohydrates())) {
            existingProduct.setCarbohydrates(product.getCarbohydrates());
        }
        if (nonNull(product.getFats())) {
            existingProduct.setFats(product.getFats());
        }
        existingProduct.setUpdatedAt(now());
        existingProduct.setSource(USER);

        return repository.save(existingProduct);
    }

    public List<Product> getByNames(List<String> names) {
        return repository.findByNameIn(names);
    }
}
