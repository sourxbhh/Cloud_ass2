package com.example;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<LongWritable, Text, Text, Text> {
    
    @Override
    protected void map(LongWritable key, Text value, Context context) 
            throws IOException, InterruptedException {
        
        String line = value.toString();
        if (line.trim().isEmpty()) return;
        
        // Split document ID from content
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2) return;
        
        String docId = parts[0];
        String content = parts[1];
        
        // Clean and tokenize words
        Set<String> words = new HashSet<>();
        StringTokenizer tokenizer = new StringTokenizer(content);
        
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken()
                    .toLowerCase()
                    .replaceAll("[^a-zA-Z]", ""); // Remove punctuation
            
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        
        // Emit document ID and its word set
        context.write(new Text(docId), new Text(String.join(",", words)));
    }
}
