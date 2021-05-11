package pl.dietadvisor.Common.productScraper.config;

import org.springframework.context.annotation.Import;
import pl.dietadvisor.Common.shared.config.ApplicationConfig;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
        ApplicationConfig.class,
        ProductScraperConfig.class
})
public @interface ProductScraper {
}
