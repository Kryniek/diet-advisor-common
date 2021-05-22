package pl.dietadvisor.common.recipeScraper.config;

import org.springframework.context.annotation.Import;
import pl.dietadvisor.common.product.config.ProductConfig;
import pl.dietadvisor.common.recipe.config.RecipeConfig;
import pl.dietadvisor.common.shared.config.ApplicationConfig;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
        ApplicationConfig.class,
        RecipeScraperConfig.class,
        ProductConfig.class,
        RecipeConfig.class
})
public @interface RecipeScraper {
}
