package co.edu.unbosque.foresta.utils;

import java.text.Normalizer;

public final class AsciiSanitizer {
    private AsciiSanitizer() {}

    public static String asciiPrintable(String input) {
        if (input == null) return null;
        String norm = Normalizer.normalize(input, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "");
        String ascii = norm.replaceAll("[^\\x20-\\x7E]", "");
        return ascii.replaceAll("\\s+", " ").trim();
    }
}
