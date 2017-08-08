package com.ilanpillemer;

import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

// Input files are expected to have one paragraph per line. This was
// achieved by preprocessing the data files with the unix 'fmt'
// programme.
//
// This has the consequence of ensuring that there is one map job per paragraph
// whicb ensures that the pairs are not counted over parapgraph boundries.
// 
// The below are the commands that were run to ensure this.
// bash-3.2$ fmt -w 100000 < input_original/pg105.txt > input/pg105.txt
// bash-3.2$ fmt -w 100000 < input_original/pg121.txt > input/pg121.txt
// bash-3.2$ fmt -w 100000 < input_original/pg1212.txt > input/pg1212.txt
// bash-3.2$ fmt -w 100000 < input_original/pg1342.txt > input/pg1342.txt
// bash-3.2$ fmt -w 100000 < input_original/pg141.txt > input/pg141.txt
// bash-3.2$ fmt -w 100000 < input_original/pg158.txt > input/pg158.txt
// bash-3.2$ fmt -w 100000 < input_original/pg161.txt > input/pg161.txt
// bash-3.2$ fmt -w 100000 < input_original/pg946.txt > input/pg946.txt


public class StripeCount {

    public static class StripeMapper extends Mapper<Object, Text, Text, StripeWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString().toLowerCase().
                // remove punctuation
                replaceAll("\\p{Punct}", " ");
            
            HashMap<String, StripeWritable> stripes = new HashMap<>();
            StringTokenizer itr = new StringTokenizer(line);
            String prevWord = "_";
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                incrementWordForKey(stripes, prevWord, new CounterWritable(word, one));
                prevWord = word.toString();
            }

            for (String word : stripes.keySet()) {
                context.write(new Text(word), stripes.get(word));
            }

        }

        private void incrementWordForKey(HashMap<String, StripeWritable> stripes, String key, CounterWritable counter) {
            StripeWritable stripe = stripes.get(key);
            if (stripe == null) {
                stripe = new StripeWritable();
            }
            stripe.incrementWord(counter);
            stripes.put(key,stripe);                
        }

    }

    // Can also be using as the combiner
    public static class StripeReducer extends Reducer<Text, StripeWritable, Text, StripeWritable> {
        
        public void reduce(Text key, Iterable<StripeWritable> stripes, Context context)
            throws IOException, InterruptedException {

            StripeWritable result = new StripeWritable();
            
            for (StripeWritable stripe : stripes) {
                result.mergeStripe(stripe);
            }
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "cond prob");
        job.setJarByClass(StripeCount.class);
        job.setMapperClass(StripeMapper.class);
        job.setCombinerClass(StripeReducer.class);
        job.setReducerClass(StripeReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(StripeWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(StripeWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
