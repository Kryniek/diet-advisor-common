package pl.dietadvisor.common.productScraper.config;

import org.springframework.context.annotation.Import;
import pl.dietadvisor.common.product.config.ProductConfig;
import pl.dietadvisor.common.shared.config.ApplicationConfig;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
        ApplicationConfig.class,
        ProductScraperConfig.class,
        ProductConfig.class
})
public @interface ProductScraper {
}
