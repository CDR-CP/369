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
    public static class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();
            if (line.length() == 0) return;

            String[] parts = line.split(",");

            if (parts.length < 2) return;

            String date = parts[1].trim().replace("/", "-");

            context.write(new Text(date), new IntWritable(1));
        }
    }

    //REDUCER 
    public static class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            int total = 0;

            for (IntWritable value : values) {
                total += value.get();
            }

            context.write(key, new IntWritable(total));
        }
    }

    // DRIVER 
    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.out.println("Usage: Salesbyday <input> <output> [combiner]");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "sales by day");

        job.setJarByClass(Salesbyday.class);

        job.setMapperClass(MyMapper.class);

        // Optional combiner
        if (args.length == 3 && args[2].equals("combiner")) {
            job.setCombinerClass(MyReducer.class);
        }

        job.setReducerClass(MyReducer.class);

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