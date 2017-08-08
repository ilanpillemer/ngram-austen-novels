package com.ilanpillemer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.TreeMap;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

public class StripeWritable implements WritableComparable<StripeWritable> {

    CounterArrayWritable words = new CounterArrayWritable(new CounterWritable[0]);
    
    public void set(CounterArrayWritable words) {
        this.words = words;
    }

    public void incrementWord(String word, int wordCount) {
        Writable[] array = words.get();

        for ( int i = 0; i < array.length; i++)  {
            CounterWritable e = ((CounterWritable) array[i]);
            if ( e.getWord().equals(word)) {
                // sum up all those words!
                e.setCount(e.getCount() + wordCount);
                array[i] = e;
                words.set(array);
                return;
            }
        }
        // the word does not yet exist in the list, so add it
        words.addNewWord(word, wordCount);
    }

    public void incrementWord(CounterWritable c) {
        incrementWord(c.getWord(), c.getCount());
    }
    
    public StripeWritable() {
        set(new CounterArrayWritable(new CounterWritable[0]));
    }

    public int getTotalCount() {
        int total = 0;
        for (Writable e : words.get()) {
            CounterWritable word = ((CounterWritable) e);
            total += word.getCount();
        }

        return total;
    } 
    
    @Override
    public void readFields(DataInput in) throws IOException {
        words.readFields(in);

    }

    @Override
    public void write(DataOutput out) throws IOException {
        words.write(out);
    }

    @Override
    public int compareTo(StripeWritable o) {
        return 0;
    }

    public TreeMap<CounterWritable,Writable> getTopTen() {

        // TreeMap is very efficient data structure for storing sorted information.
        // as per the JavaDoc.

        // This implementation provides guaranteed log(n) time cost for the containsKey, get, put and remove operations.
        // Algorithms are adaptations of those in Cormen, Leiserson, and Rivest's Introduction to Algorithms.
        
        TreeMap<CounterWritable,Writable> topTen = new TreeMap<>();
        for (Writable e : words.get()) {
            CounterWritable word = (CounterWritable) e;
            topTen.put(word, word);
            //we only need the top ten so we can just keep removing the 11th item from the sorted list
            //this means we can stream through the items once and never need to sort more than 10 at a time
            if (topTen.size() > 10) {
                topTen.remove(topTen.firstKey());
            }
        }
        return topTen;
        
    }
 
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Writable e : getTopTen().descendingMap().values()) {
            CounterWritable word = (CounterWritable) e;
            builder.append("<" + word.getWord() + ",");
            builder.append((word.getCount() / (getTotalCount() * 1.0)) + ">");
            builder.append(":");
        }
        // remove trailing ":""
        builder.deleteCharAt(builder.length()-1); 
        return builder.toString();
    }

    public void mergeStripe(StripeWritable o) {
        for (Writable e : o.words.get()) {
            CounterWritable  counter = (CounterWritable)  e;
            incrementWord(counter);
        }
    }

} 
