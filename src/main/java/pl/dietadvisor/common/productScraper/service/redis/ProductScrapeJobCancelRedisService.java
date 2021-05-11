package pl.dietadvisor.common.productScraper.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.productScraper.model.redis.ProductScrapeJobCancel;
import pl.dietadvisor.common.productScraper.repository.redis.ProductScrapeJobCancelRedisRepository;

@Service
@RequiredArgsConstructor
public class ProductScrapeJobCancelRedisService {
    private final ProductScrapeJobCancelRedisRepository repository;

    public ProductScrapeJobCancel cancel(String id) {
        return repository.save(ProductScrapeJobCancel.builder()
                .id(id)
                .build());
    }

    public boolean isCancelled(String id) {
        return repository.existsById(id);
    }
}
