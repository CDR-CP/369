import java.io.*;
import java.util.*;

public class TopTenProductsJava {

    // strip $ and parse price, returns -1 if it fails
    private static double parsePrice(String raw) {
        try {
            return Double.parseDouble(raw.trim().replace("$", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Usage:
    //   java TopTenProductsJava <input_file> <output_file>
    //
    // Example:
    //   java TopTenProductsJava /home/lubo/input/data/products/products.txt java_output.txt
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java TopTenProductsJava <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile  = args[0];
        String outputFile = args[1];

        // TreeMap keyed by price keeps entires sorted, easy to trim to top 10
        TreeMap<Double, String> topTen = new TreeMap<>();

        long start = System.currentTimeMillis();

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // split into 3 parts max since name may contain commas
            String[] parts = line.split(",", 3);
            if (parts.length < 3) continue;

            String productId = parts[0].trim();
            String name      = parts[1].trim();
            double price     = parsePrice(parts[2]);

            if (price < 0) continue;

            topTen.put(price, productId + "\t" + name);

            // only keep top 10
            if (topTen.size() > 10) topTen.pollFirstEntry();
        }

        reader.close();

        // write in descending order
        List<Map.Entry<Double, String>> entries = new ArrayList<>(topTen.entrySet());

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));

        for (int i = entries.size() - 1; i >= 0; i--) {
            double price     = entries.get(i).getKey();
            String[] product = entries.get(i).getValue().split("\t", 2);
            writer.println(product[0] + "\t" + product[1] + "\t" + String.format("%.2f", price));
        }

        writer.close();

        long end = System.currentTimeMillis();

        System.out.println("Java runtime: " + (end - start) + " ms");
        System.out.println("Output written to: " + outputFile);
    }
}