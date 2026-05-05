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

public class SalesByDayDetail {

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

    public static class MyMapper extends Mapper<LongWritable, Text, Text, Text> {

        private final Text dateKey   = new Text();
        private final Text timeValue = new Text();

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();
            if (line.isEmpty()) return;

            String[] parts = line.split(",");
            if (parts.length < 3) return;

            String saleId = parts[0].trim();
            String date   = normalizeDate(parts[1]);
            String time   = normalizeTime(parts[2]);

            dateKey.set(date);
            timeValue.set(time + "," + saleId);
            context.write(dateKey, timeValue);
        }
    }

    public static class MyReducer extends Reducer<Text, Text, Text, Text> {

        private final Text result = new Text();

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            TreeMap<String, String> timeToSale = new TreeMap<>();

            for (Text val : values) {
                String[] parts = val.toString().split(",", 2);
                if (parts.length == 2) {
                    timeToSale.put(parts[0], parts[1]);
                }
            }

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : timeToSale.entrySet()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(entry.getKey()).append(" ").append(entry.getValue());
            }

            result.set(sb.toString());
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println("Usage: SalesByDayDetail <input_path> <output_path>");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Sales By Day Detail");

        job.setJarByClass(SalesByDayDetail.class);
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);

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
