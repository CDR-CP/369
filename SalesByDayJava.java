import java.io.*;
import java.util.*;

public class SalesByDayJava {

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("Usage: java SalesByDayJava <inputFile> <outputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        HashMap<String, Integer> counts = new HashMap<>();

        long start = System.currentTimeMillis();

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;

        while ((line = reader.readLine()) != null) {

            line = line.trim();
            if (line.length() == 0) continue;

            String[] parts = line.split(",");

            if (parts.length < 2) continue;

            String date = parts[1].trim().replace("/", "-");

            counts.put(date, counts.getOrDefault(date, 0) + 1);
        }

        reader.close();

        // sort by date
        TreeMap<String, Integer> sorted = new TreeMap<>(counts);

        PrintWriter writer = new PrintWriter(new FileWriter(outputFile));

        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            writer.println(entry.getKey() + "\t" + entry.getValue());
        }

        writer.close();

        long end = System.currentTimeMillis();

        System.out.println("Java runtime: " + (end - start) + " ms");
    }
}
