import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TopTenProducts {

    // strip $ and parse price, returns -1 if it fails
    private static double parsePrice(String raw) {
        try {
            return Double.parseDouble(raw.trim().replace("$", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // mapper emits a constant key so all records go to one reducer
    // value is "price,productID,name"
    public static class MyMapper extends Mapper<LongWritable, Text, Text, Text> {

        private final Text constantKey = new Text("top10");
        private final Text outValue    = new Text();

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();
            if (line.isEmpty()) return;

            // split into 3 parts max since name may contain commas
            String[] parts = line.split(",", 3);
            if (parts.length < 3) return;

            String productId = parts[0].trim();
            String name      = parts[1].trim();
            double price     = parsePrice(parts[2]);

            if (price < 0) return;

            outValue.set(price + "," + productId + "," + name);
            context.write(constantKey, outValue);
        }
    }

    // reducer gets all products, keeps top 10 by price using a TreeMap
    public static class MyReducer extends Reducer<Text, Text, Text, Text> {

        private final Text outKey   = new Text();
        private final Text outValue = new Text();

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            // TreeMap sorted by price ascending, we trim to top 10
            TreeMap<Double, String> topTen = new TreeMap<>();

            for (Text val : values) {
                String[] parts = val.toString().split(",", 3);
                if (parts.length < 3) continue;

                double price     = Double.parseDouble(parts[0]);
                String productId = parts[1];
                String name      = parts[2];

                topTen.put(price, productId + "\t" + name);

                // only keep top 10
                if (topTen.size() > 10) topTen.pollFirstEntry();
            }

            // write in descending order
            List<Map.Entry<Double, String>> entries = new ArrayList<>(topTen.entrySet());
            for (int i = entries.size() - 1; i >= 0; i--) {
                double price     = entries.get(i).getKey();
                String[] product = entries.get(i).getValue().split("\t", 2);
                outKey.set(product[0] + "\t" + product[1]);
                outValue.set(String.format("%.2f", price));
                context.write(outKey, outValue);
            }
        }
    }

    // Usage:
    //   hadoop jar TopTenProducts.jar TopTenProducts <input_path> <output_path>
    //
    // Example:
    //   hadoop jar TopTenProducts.jar TopTenProducts /user/lubo/input/products /user/lubo/output
    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println("Usage: TopTenProducts <input_path> <output_path>");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Top Ten Products");

        job.setJarByClass(TopTenProducts.class);
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);

        // single reducer so output is globaly sorted
        job.setNumReduceTasks(1);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        long start = System.currentTimeMillis();
        boolean success = job.waitForCompletion(true);
        long end = System.currentTimeMillis();

        System.out.println("MapReduce runtime: " + (end - start) + " ms");

        System.exit(success ? 0 : 1);
    }
}