package pl.dietadvisor.common.api.config;

import org.springframework.context.annotation.Import;
import pl.dietadvisor.common.product.config.ProductConfig;
import pl.dietadvisor.common.recipe.config.RecipeConfig;
import pl.dietadvisor.common.shared.config.ApplicationConfig;
import pl.dietadvisor.common.shoppingList.config.ShoppingListConfig;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
        ApiConfig.class,
        ApplicationConfig.class,
        ProductConfig.class,
        RecipeConfig.class,
        ShoppingListConfig.class
})
public @interface Api {
}
