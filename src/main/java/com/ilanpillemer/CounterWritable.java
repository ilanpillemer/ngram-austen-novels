package com.ilanpillemer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class CounterWritable implements WritableComparable<CounterWritable> {

    Text word = new Text();
    IntWritable count = new IntWritable();

    public String getWord() {
        return word.toString();
    }

    public int getCount() {
        return count.get();
    }

    public void setCount(int i) {
        this.count.set(i);
    }
    
    public CounterWritable() {
        set(new Text(""), new IntWritable(0));
    }

   public CounterWritable(String word, int count) {
       set(new Text(word), new IntWritable(count));
    }
    
    public CounterWritable(Text word, IntWritable count) {
        set(word, count);
    }

    
    public void set(Text word, IntWritable count) {
        this.word.set(word.toString());
        this.count.set(count.get());
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        word.readFields(in);
        count.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        word.write(out);
        count.write(out);
    }

    @Override
    public int compareTo(CounterWritable o) {
        // Order by count!
        int c = count.compareTo(o.count);
        if (c!=0) {
            return c;
        }
        return word.toString().compareTo(o.word.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CounterWritable) {
            CounterWritable c = (CounterWritable) o;
            return count.equals(c.count) && word.equals(c.word);
        }
        return false;
    }

    @Override
    public String toString() {
        return "<" + word + "," + count + ">";
    }

}
