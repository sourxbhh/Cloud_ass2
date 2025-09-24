package com.example;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    
    private Map<String, Set<String>> documentWords = new HashMap<>();
    
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) 
            throws IOException, InterruptedException {
        
        // Store document words
        Set<String> words = new HashSet<>();
        for (Text value : values) {
            String[] wordArray = value.toString().split(",");
            words.addAll(Arrays.asList(wordArray));
        }
        documentWords.put(key.toString(), words);
    }
    
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        List<String> docIds = new ArrayList<>(documentWords.keySet());
        Collections.sort(docIds);
        
        // Generate all unique pairs and calculate Jaccard similarity
        for (int i = 0; i < docIds.size(); i++) {
            for (int j = i + 1; j < docIds.size(); j++) {
                String doc1 = docIds.get(i);
                String doc2 = docIds.get(j);
                
                Set<String> words1 = documentWords.get(doc1);
                Set<String> words2 = documentWords.get(doc2);
                
                double similarity = calculateJaccardSimilarity(words1, words2);
                
                // Format output
                String outputKey = doc1 + ", " + doc2;
                String outputValue = String.format("Similarity: %.2f", similarity);
                
                context.write(new Text(outputKey), new Text(outputValue));
            }
        }
    }
    
    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        
        if (union.isEmpty()) return 0.0;
        
        return (double) intersection.size() / union.size();
    }
}