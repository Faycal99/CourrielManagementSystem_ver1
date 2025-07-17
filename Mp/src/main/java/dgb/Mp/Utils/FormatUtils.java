package dgb.Mp.Utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class FormatUtils {

    public static String formatFileSize(long sizeInBytes) {
        double sizeInKB = sizeInBytes / 1024.0;
        if (sizeInKB < 1024) {
            return String.format("%.0f KB", sizeInKB);  // with KO if < 1MB
        } else {
            double sizeInMB = sizeInKB / 1024.0;
            return String.format("%.0f MB", sizeInMB);  // else with  MB
        }
    }

    static String formatOrgUnitName(String name) {
        if (name == null) return null;
        String[] stopWords = {"de", "des", "du", "la", "le", "les", "pour", "et", "au"};
        String[] words = name.split("\\s+");
        StringBuilder code = new StringBuilder();
        for (String word : words) {
            if (!Arrays.asList(stopWords).contains(word.toLowerCase()) && !word.isBlank()) {
                code.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return "(" + code + ") " + name;
    }

    static String formatDate(Instant timestamp) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault())
                .format(timestamp);
    }
}