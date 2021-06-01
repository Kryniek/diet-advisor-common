package pl.dietadvisor.common.dataInitializer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import pl.dietadvisor.common.product.model.dynamodb.Product;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeLog;
import pl.dietadvisor.common.product.repository.dynamodb.ProductRepository;
import pl.dietadvisor.common.product.repository.dynamodb.ProductScrapeJobRepository;
import pl.dietadvisor.common.product.repository.dynamodb.ProductScrapeLogRepository;
import pl.dietadvisor.common.recipe.model.dynamodb.Recipe;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeJob;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeLog;
import pl.dietadvisor.common.recipe.repository.dynamodb.RecipeRepository;
import pl.dietadvisor.common.recipe.repository.dynamodb.RecipeScrapeJobRepository;
import pl.dietadvisor.common.recipe.repository.dynamodb.RecipeScrapeLogRepository;
import pl.dietadvisor.common.shared.config.properties.aws.AwsProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Configuration
@RequiredArgsConstructor
@Profile("dev")
@Log4j2
public class InitializeDataRunner implements ApplicationRunner {
    private final ProductScrapeJobRepository productScrapeJobRepository;
    private final ProductScrapeLogRepository productScrapeLogRepository;
    private final ProductRepository productRepository;
    private final RecipeScrapeJobRepository recipeScrapeJobRepository;
    private final RecipeScrapeLogRepository recipeScrapeLogRepository;
    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper;
    private final AwsProperties awsProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        insertProductScrapeJobs();
        insertProductScrapeLogs();
        insertProducts();
        insertRecipeScrapeJobs();
        insertRecipeScrapeLogs();
        insertRecipes();
        insertRecipesImages();
    }

    private void insertProductScrapeJobs() throws IOException {
        if (productScrapeJobRepository.count() == 0) {
            String rawProductScrapeJobs = getFileContent("product-scrape-jobs");
            List<ProductScrapeJob> productScrapeJobs = objectMapper.readValue(rawProductScrapeJobs,
                    objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, ProductScrapeJob.class));

            Integer insertedProductScrapeJobsSize = ((List<ProductScrapeJob>) productScrapeJobRepository.saveAll(productScrapeJobs)).size();
            log.info("Inserted test product scrape jobs: {}", insertedProductScrapeJobsSize);
        }
    }

    private void insertProductScrapeLogs() throws IOException {
        if (productScrapeLogRepository.count() == 0) {
            String rawProductScrapeLogs = getFileContent("product-scrape-logs");
            List<ProductScrapeLog> productScrapeLogs = objectMapper.readValue(rawProductScrapeLogs,
                    objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, ProductScrapeLog.class));

            Integer insertedProductScrapeLogsSize = ((List<ProductScrapeLog>) productScrapeLogRepository.saveAll(productScrapeLogs)).size();
            log.info("Inserted test product scrape logs: {}", insertedProductScrapeLogsSize);
        }
    }

    private void insertProducts() throws IOException {
        if (productRepository.count() == 0) {
            String rawProducts = getFileContent("products");
            List<Product> products = objectMapper.readValue(rawProducts,
                    objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, Product.class));

            Integer insertedProductsSize = ((List<Product>) productRepository.saveAll(products)).size();
            log.info("Inserted test products: {}", insertedProductsSize);
        }
    }

    private void insertRecipeScrapeJobs() throws IOException {
        if (recipeScrapeJobRepository.count() == 0) {
            String rawJobs = getFileContent("recipe-scrape-jobs");
            List<RecipeScrapeJob> jobs = objectMapper.readValue(rawJobs,
                    objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, RecipeScrapeJob.class));

            Integer insertedJobsSize = ((List<RecipeScrapeJob>) recipeScrapeJobRepository.saveAll(jobs)).size();
            log.info("Inserted test recipe scrape jobs: {}", insertedJobsSize);
        }
    }

    private void insertRecipeScrapeLogs() throws IOException {
        if (recipeScrapeLogRepository.count() == 0) {
            String rawLogs = getFileContent("recipe-scrape-logs");
            List<RecipeScrapeLog> logs = objectMapper.readValue(rawLogs,
                    objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, RecipeScrapeLog.class));

            Integer insertedLogsSize = ((List<RecipeScrapeLog>) recipeScrapeLogRepository.saveAll(logs)).size();
            log.info("Inserted test recipe scrape logs: {}", insertedLogsSize);
        }
    }

    private void insertRecipes() throws IOException {
        if (recipeRepository.count() == 0) {
            String rawRecipes = getFileContent("recipes");
            List<Recipe> recipes = objectMapper.readValue(rawRecipes,
                    objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, Recipe.class));

            Integer insertedRecipesSize = ((List<Recipe>) recipeRepository.saveAll(recipes)).size();
            log.info("Inserted test recipes: {}", insertedRecipesSize);
        }
    }

    private String getFileContent(String fileName) throws IOException {
        File file = new ClassPathResource(format("data/%s.json", fileName)).getFile();
        return Files.readString(file.toPath());
    }

    private void insertRecipesImages() throws IOException {
        File sourceDirectory = new ClassPathResource("data/recipe-scraper/images").getFile();
        List<File> sourceImages = Files.walk(sourceDirectory.toPath())
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(toList());

        Integer copiedImagesCount = 0;
        for (File sourceImage : sourceImages) {
            File destinationImage = new File(String.format("%s/%s", awsProperties.getRecipesImagesAddress(), sourceImage.getName()));
            if (!destinationImage.exists()) {
                FileCopyUtils.copy(sourceImage, destinationImage);
                copiedImagesCount++;
            }
        }
        if (copiedImagesCount > 0) {
            log.info("Recipes images was copied to: '{}'. Copied images count: {}", awsProperties.getRecipesImagesAddress(), copiedImagesCount);
        }
    }
}
