package pl.dietadvisor.common.shoppingList.enums;

import java.util.List;

public enum ProductType {
    DAIRY("Nabiał"),
    VEGETABLE("Warzywa"),
    FRUIT("Owoce"),
    SEED_AND_NUT("Nasiona/Orzechy"),
    BREAD("Pieczywo"),
    OTHER("Pozostałe");

    private String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ProductType parse(String name) {
        if (isDairy(name)) {
            return DAIRY;
        }
        if (isVegetable(name)) {
            return VEGETABLE;
        }
        if (isFruit(name)) {
            return FRUIT;
        }
        if (isSeedAndNut(name)) {
            return SEED_AND_NUT;
        }
        if (isBread(name)) {
            return BREAD;
        }

        return OTHER;
    }

    private static boolean isDairy(String name) {
        List<String> keyNames = List.of("jogurt",
                "ser ",
                "Serek",
                "twaróg",
                "twarogi",
                "jaja",
                "jajka",
                "kefir",
                "mleko",
                "tofu");
        return keyNames
                .stream()
                .map(String::toLowerCase)
                .anyMatch(name.toLowerCase()::contains);
    }

    private static boolean isVegetable(String name) {
        List<String> keyNames = List.of("rukola",
                "Rzodkiewka",
                "Pomidor",
                "Cukinia",
                "Papryka",
                "Sałata",
                "Kiełki",
                "Ogórek",
                "Awokado",
                "Szczypiorek",
                "Cebula",
                "Ziemniak",
                "Batat",
                "Bakłażan",
                "Bazylia (świeże liście)",
                "Burak",
                "Dynia",
                "Fasola szparagowa",
                "Koper ogrodowy",
                "Kukurydza",
                "pietruszk",
                "Czosnek",
                "Oliwki",
                "marchew",
                "pieczarki",
                "seler ");
        return keyNames
                .stream()
                .map(String::toLowerCase)
                .anyMatch(name.toLowerCase()::contains);
    }

    private static boolean isFruit(String name) {
        List<String> keyNames = List.of("jabłko",
                "banan",
                "Cytryna",
                "Mango",
                "gruszka",
                "kiwi",
                "Winogron",
                "Nektarynk",
                "Melon",
                "Agrest",
                "mandarynk",
                "pomarańcz");
        return keyNames
                .stream()
                .map(String::toLowerCase)
                .anyMatch(name.toLowerCase()::contains);
    }

    private static boolean isSeedAndNut(String name) {
        List<String> keyNames = List.of("migdały",
                "Orzechy");
        return keyNames
                .stream()
                .map(String::toLowerCase)
                .anyMatch(name.toLowerCase()::contains);
    }

    private static boolean isBread(String name) {
        List<String> keyNames = List.of("bułka",
                "chleb",
                "tortilla");
        return keyNames
                .stream()
                .map(String::toLowerCase)
                .anyMatch(name.toLowerCase()::contains);
    }
}
