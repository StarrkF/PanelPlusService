package com.example.panelplus.util;

import java.text.Normalizer;
import java.util.Locale;

public  class UtilService {

    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String lower = input.toLowerCase(Locale.of("tr"));
        String normalized = Normalizer.normalize(lower, Normalizer.Form.NFD);
        String slug = normalized
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "") // Aksan işaretlerini sil
                .replaceAll("[^a-z0-9\\s-]", "")     // Harf, sayı, boşluk ve tire dışındakileri sil
                .trim()                               // Baştaki/sondaki boşlukları sil
                .replaceAll("\\s+", "-");             // Boşlukları tireye çevir

        return slug;
    }
}
