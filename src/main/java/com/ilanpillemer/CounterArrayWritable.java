package com.ilanpillemer;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Writable;


public class CounterArrayWritable extends ArrayWritable {

    public CounterArrayWritable() {
        super(CounterWritable.class);
    }

    public CounterArrayWritable(CounterWritable[] counters) {
        super(CounterWritable.class, counters);
    }
    
    public void addNewWord(String word, int count) {
        Writable[] original = get();
        Writable[] updated = new Writable[original.length+1];

        for (int i = 0; i < original.length; i++) {
            updated[i] = original[i];
        }
        updated[original.length] = new CounterWritable(word, count);
        set(updated);
    }
}
