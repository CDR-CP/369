import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TopTenProducts {

    public static class Record implements Comparable<Record> {

        private int id;
        private String name;
        private double price;

        public Record(int id, String name, double price) {
            this.id    = id;
            this.name  = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return id + "," + name + "," + String.format("%.2f", price);
        }

        @Override
        public int compareTo(Record other) {
            if (this.price > other.price) return -1;
            if (this.price < other.price) return 1;
            if (this.id > other.id) return 1;
            if (this.id < other.id) return -1;
            return 0;
        }
    }

    private static double parsePrice(String raw) {
        try {
            return Double.parseDouble(raw.trim().replace("$", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static class MyMapper extends Mapper<LongWritable, Text, NullWritable, Text> {

        public static final int DEFAULT_N = 10;
        private int n = DEFAULT_N;
        private TreeSet<Record> top = new TreeSet<>();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            this.n = context.getConfiguration().getInt("N", DEFAULT_N);
        }

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();
            if (line.isEmpty()) return;

            String[] parts = line.split(",", 3);
            if (parts.length < 3) return;

            double price = parsePrice(parts[2]);
            if (price < 0) return;

            top.add(new Record(Integer.parseInt(parts[0].trim()), parts[1].trim(), price));

            if (top.size() > n) top.remove(top.last());
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            for (Record r : top) {
                context.write(NullWritable.get(), new Text(r.toString()));
            }
        }
    }

    public static class MyReducer extends Reducer<NullWritable, Text, NullWritable, Text> {

        private int n = MyMapper.DEFAULT_N;
        private SortedSet<Record> top = new TreeSet<>();

        //taken from slides 
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            this.n = context.getConfiguration().getInt("N", MyMapper.DEFAULT_N);
        }

        @Override
        public void reduce(NullWritable key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            for (Text value : values) {
                String[] parts = value.toString().trim().split(",", 3);
                if (parts.length < 3) continue;

                double price = parsePrice(parts[2]);
                if (price < 0) continue;

                top.add(new Record(Integer.parseInt(parts[0].trim()), parts[1].trim(), price));

                if (top.size() > n) top.remove(top.last());
            }

            for (Record r : top) {
                context.write(NullWritable.get(), new Text(r.toString()));
            }
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println("Usage: TopTenProducts <input_path> <output_path>");
            System.exit(1);
        }
        // top 10 values 
        int n = 10;

        Configuration conf = new Configuration();
        conf.setInt("N", n);

        Job job = Job.getInstance(conf, "Top Ten Products");

        job.setJarByClass(TopTenProducts.class);
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);

        job.setNumReduceTasks(1);

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(NullWritable.class);
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