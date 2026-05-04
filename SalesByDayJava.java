import java.io.*;
import java.util.*;

public class SalesByDayJava {

   
    // Plain Java equivalent of the MapReduce Sales By Day program.
    //
    // Uses a HashMap to accumulate counts (O(1) average insert/lookup),
    // then loads into a TreeMap to sort by date before writing output.
    //
    // Usage:
    //   java SalesByDayJava <input_file> <output_file>
    //
    // Example (run from the directory containing sales.txt):
    //   java SalesByDayJava sales.txt java_output.txt

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java SalesByDayJava <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile  = args[0];
        String outputFile = args[1];

        
        HashMap<String, Integer> counts = new HashMap<>();

        long start = System.currentTimeMillis();

        // Read and process input (mimics Map)
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;

        while ((line = reader.readLine()) != null) {

            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length < 2) continue;

            // replace '/' with '-' (MapReduce)
            String date = parts[1].trim().replace("/", "-");
            if (date.isEmpty()) continue;

            // Accumulate count for this date (mimics Reduce)
            counts.put(date, counts.getOrDefault(date, 0) + 1);
        }

        reader.close();

        
        TreeMap<String, Integer> sorted = new TreeMap<>(counts);

   
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));

        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            writer.println(entry.getKey() + "\t" + entry.getValue());
        }

        writer.close();

        long end = System.currentTimeMillis();

        System.out.println("Java runtime: " + (end - start) + " ms");
        System.out.println("Output written to: " + outputFile);
    }
}
