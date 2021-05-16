package pl.dietadvisor.common.api.config;

import org.springframework.context.annotation.Import;
import pl.dietadvisor.common.shared.config.ApplicationConfig;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
        ApiConfig.class,
        ApplicationConfig.class
})
public @interface Api {
}
