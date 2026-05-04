import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Salesbyday {
    // MAPPER
    // Input:  each line of sales.txt
    // Format: saleID, date, time, storeID, customerID
    // Output: (date, 1) for each sale
    public static class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        private static final IntWritable ONE = new IntWritable(1);
        private final Text dateKey = new Text();

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();
            if (line.isEmpty()) return;

            String[] parts = line.split(",");
            if (parts.length < 2) return;

            // replace '/' with '-' 
            String date = parts[1].trim().replace("/", "-");
            if (date.isEmpty()) return;

            dateKey.set(date);
            context.write(dateKey, ONE);
        }
    }

    // REDUCER aLso used as Combiner when --combiner flag is passed
    // Input:  (date, [1, 1, 1, ...]
    // Output: (date, total count)
   
    public static class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

        private final IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            int total = 0;
            for (IntWritable value : values) {
                total += value.get();
            }

            result.set(total);
            context.write(key, result);
        }
    }

    
    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println("Usage: Salesbyday <input_path> <output_path> [combiner]");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Sales By Day");

        job.setJarByClass(Salesbyday.class);
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);

    
        boolean useCombiner = (args.length == 3 && args[2].equalsIgnoreCase("combiner"));
        if (useCombiner) {
            job.setCombinerClass(MyReducer.class);
            System.out.println("Combiner enabled.");
        } else {
            System.out.println("Combiner NOT enabled.");
        }

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        long start = System.currentTimeMillis();
        boolean success = job.waitForCompletion(true);
        long end = System.currentTimeMillis();

        System.out.println("MapReduce runtime: " + (end - start) + " ms");

        System.exit(success ? 0 : 1);
    }
}
