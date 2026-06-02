import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SalesByDayDetail {

    public static class DateTimeKey implements WritableComparable<DateTimeKey> {

        private String date;
        private String time;

        public DateTimeKey() {
            this.date = "";
            this.time = "";
        }

        public DateTimeKey(String date, String time) {
            this.date = date;
            this.time = time;
        }

        public String getDate() { return date; }
        public String getTime() { return time; }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(date);
            out.writeUTF(time);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            date = in.readUTF();
            time = in.readUTF();
        }

        @Override
        public int compareTo(DateTimeKey other) {
            int cmp = this.date.compareTo(other.date);
            if (cmp != 0) return cmp;
            return this.time.compareTo(other.time);
        }

        @Override
        public String toString() {
            return date + "\t" + time;
        }
    }

    public static class DatePartitioner extends Partitioner<DateTimeKey, Text> {

        @Override
        public int getPartition(DateTimeKey key, Text value, int numPartitions) {
            return Math.abs(key.getDate().hashCode()) % numPartitions;
        }
    }

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

    public static class MyMapper extends Mapper<LongWritable, Text, DateTimeKey, Text> {

        private final Text outValue = new Text();

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();
            if (line.isEmpty()) return;

            String[] parts = line.split(",");
            if (parts.length < 3) return;

            String id   = parts[0].trim();
            String date = normalizeDate(parts[1]);
            String time = normalizeTime(parts[2]);

            // key is date+time, value is time+saleId
            outValue.set(time + " " + id);
            context.write(new DateTimeKey(date, time), outValue);
        }
    }

    public static class MyReducer extends Reducer<DateTimeKey, Text, Text, Text> {

        private final Text outKey = new Text();
        private final Text outVal = new Text();

        @Override
        public void reduce(DateTimeKey key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            StringBuilder sb = new StringBuilder();
            for (Text val : values) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(val.toString());
            }

            outKey.set(key.getDate());
            outVal.set(sb.toString());
            context.write(outKey, outVal);
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
        job.setPartitionerClass(DatePartitioner.class);
        job.setReducerClass(MyReducer.class);

        job.setMapOutputKeyClass(DateTimeKey.class);
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