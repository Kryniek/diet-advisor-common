package pl.dietadvisor.common.recipeScraper.config;

import org.springframework.context.annotation.Import;
import pl.dietadvisor.common.shared.config.ApplicationConfig;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
        ApplicationConfig.class,
        RecipeScraperConfig.class
})
public @interface RecipeScraper {
}
