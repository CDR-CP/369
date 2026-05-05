import java.io.*;
import java.util.*;

public class SalesByDayDetailJava {

    private static String normalizeDate(String raw) {
        return raw.trim().replace("/", "-");
    }

    private static String normalizeTime(String raw) {
        raw = raw.trim();

        if (raw.toLowerCase().endsWith("hrs")) {
            String digits = raw.substring(0, raw.toLowerCase().indexOf("hrs")).trim();
            if (digits.length() == 4) {
                return digits.substring(0, 2) + ":" + digits.substring(2, 4) + ":00";
            }
        }

        String[] parts = raw.split(":");
        if (parts.length == 2) return parts[0] + ":" + parts[1] + ":00";
        if (parts.length == 3) return raw;

        return raw;
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java SalesByDayDetailJava <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile  = args[0];
        String outputFile = args[1];

        // outer HashMap for O(1) date lookup, inner TreeMap keeps sales sorted by time
        HashMap<String, TreeMap<String, String>> salesByDay = new HashMap<>();

        long start = System.currentTimeMillis();

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length < 3) continue;

            String saleId = parts[0].trim();
            String date   = normalizeDate(parts[1]);
            String time   = normalizeTime(parts[2]);

            salesByDay.computeIfAbsent(date, k -> new TreeMap<>()).put(time, saleId);
        }

        reader.close();

        // wrap in TreeMap to sort dates before writing
        TreeMap<String, TreeMap<String, String>> sorted = new TreeMap<>(salesByDay);

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));

        for (Map.Entry<String, TreeMap<String, String>> dayEntry : sorted.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> saleEntry : dayEntry.getValue().entrySet()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(saleEntry.getKey()).append(" ").append(saleEntry.getValue());
            }
            writer.println(dayEntry.getKey() + "\t" + sb.toString());
        }

        writer.close();

        long end = System.currentTimeMillis();

        System.out.println("Java runtime: " + (end - start) + " ms");
        System.out.println("Output written to: " + outputFile);
    }
}