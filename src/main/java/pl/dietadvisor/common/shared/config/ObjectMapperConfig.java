package pl.dietadvisor.common.shared.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new JsonObjectMapper();
    }

    private static class JsonObjectMapper extends ObjectMapper {
        private static final long serialVersionUID = 1L;

        public JsonObjectMapper() {
            this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            this.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
            this.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
            this.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            this.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            this.registerModule(new JavaTimeModule());
        }
    }
}
