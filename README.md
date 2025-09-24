# Assignment 2: Document Similarity using MapReduce

**Name: Sourabh Kumar Dubey**

**Student ID: 801429834** 

## Approach and Implementation

### Mapper Design

The Mapper class processes each line of input, where each line represents a document. The input key-value pair is `(LongWritable, Text)` where the key is the byte offset and the value is the line content.

**Mapper Logic:**
1. Split each line into document ID and content
2. Tokenize the content into words
3. Clean each word by converting to lowercase and removing punctuation
4. Store unique words in a HashSet to remove duplicates
5. Emit key-value pair: `(DocumentID, comma-separated-word-set)`

The Mapper helps by preprocessing each document and extracting the unique word sets needed for similarity calculation.

### Reducer Design

The Reducer class receives all document-word sets and calculates Jaccard Similarity between all unique document pairs.

**Reducer Logic:**
1. In the `reduce()` method, store each document's word set in a HashMap
2. In the `cleanup()` method (called after all reduce calls):
   - Generate all unique pairs of documents
   - For each pair, calculate Jaccard Similarity using the formula:
     ```
     Jaccard Similarity = |A ∩ B| / |A ∪ B|
     ```
   - Emit the result in the format: `(Document1, Document2, Similarity: score)`

### Overall Data Flow

1. **Input Phase:** Text files are split and read by mappers
2. **Map Phase:** Each mapper processes documents and emits document-word sets
3. **Shuffle/Sort:** Hadoop groups all values by document ID
4. **Reduce Phase:** Reducer collects all document-word sets, generates pairs, and calculates similarities
5. **Output Phase:** Results are written to HDFS in the specified format

## Setup and Execution


### 1. **Start the Hadoop Cluster**

Run the following command to start the Hadoop cluster:

```bash
docker compose up -d
```

### 2. **Build the Code**

Build the code using Maven:

```bash
mvn clean package
```

### 4. **Copy JAR to Docker Container**

Copy the JAR file to the Hadoop ResourceManager container:

```bash
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. **Move Dataset to Docker Container**

Copy the dataset to the Hadoop ResourceManager container:
**Command for 3 data nodes:**
```bash
docker cp src/input1.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```
```bash
docker cp src/input2.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```
```bash
docker cp src/input3.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```
**Command for 1 data nodes:**
```bash
docker cp src/input1.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### New Step to correct folder structure for Hadoop MapReduce

```bash
mkdir -p src/main/java/com/example/controller
```

```bash
git mv src/main/com/example/controller/DocumentSimilarityDriver.java src/main/java/com/example/controller/
```

```bash
git mv src/main/com/example/DocumentSimilarityMapper.java src/main/java/com/example/
```

```bash
git mv src/main/com/example/DocumentSimilarityReducer.java src/main/java/com/example/
```

```bash
mvn -q -DskipTests clean package
```

### 6. **Connect to Docker Container**

Access the Hadoop ResourceManager container:

```bash
docker exec -it resourcemanager /bin/bash
```

Navigate to the Hadoop directory:

```bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 7. **Set Up HDFS**

Create a folder in HDFS for the input dataset:

```bash
hadoop fs -mkdir -p /input/data
```

Copy the input dataset to the HDFS folder:
**Command for 3 data nodes:**
```bash
hadoop fs -put ./input.txt /input/data
```
**Command for 1 data nodes:**
```bash
hadoop fs -put -f ./input1.txt /input/data
```

### 8. **Execute the MapReduce Job**

Run your MapReduce job using the following command: Here I got an error saying output already exists so I changed it to output1 instead as destination folder

```bash
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.Controller /input/data/input.txt /output1
```

**Corrected Command for 3 data nodes:**

```bash
hadoop jar DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/data /output1
```
**Corrected Command for 1 data nodes:**

```bash
hadoop jar DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/data/input1.txt /output1_single
```

### 9. **View the Output**

To view the output of your MapReduce job, use:

```bash
hadoop fs -cat /output1/*
```

### 10. **Copy Output from HDFS to Local OS**

To copy the output from HDFS to your local machine:

1. Use the following command to copy from HDFS:
    ```bash
    hdfs dfs -get /output1 /opt/hadoop-3.2.1/share/hadoop/mapreduce/
    ```

2. use Docker to copy from the container to your local machine:
   ```bash
   exit 
   ```
    ```bash
    docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output1/ shared-folder/output/
    ```
3. Commit and push to your repo so that we can able to see your output


---


## Challenges and Solutions

Challenge 1: Generating Document Pairs

Problem: The reducer needs to generate all unique pairs of documents, which requires storing all document data in memory

Solution: Used the cleanup() method to process all documents after they've been collected in the reduce() method

Challenge 2: Handling Large Datasets

Problem: Storing all document-word sets in memory could cause issues with very large datasets

Solution: For production use, consider alternative approaches like multiple MapReduce jobs or more memory-efficient data structures

Challenge 3: Data Cleaning

Problem: Words with punctuation and different cases needed normalization

Solution: Implemented comprehensive text cleaning in the mapper using lowercase conversion and regex-based punctuation removal
---
## Sample Input

**Input from `small_dataset.txt`**
```
Document1 This is a sample document containing words
Document2 Another document that also has words
Document3 Sample text with different words
```
## Sample Output

**Output from `small_dataset.txt`**
```
"Document1, Document2 Similarity: 0.56"
"Document1, Document3 Similarity: 0.42"
"Document2, Document3 Similarity: 0.50"
```
## Obtained Output: (Place your obtained output here.)

**1. Output with 3 data nodes:-**
Database, Document1	Similarity: 0.10
Database, Document10	Similarity: 0.05
Database, Document100	Similarity: 0.05
Database, Document11	Similarity: 0.05
Database, Document12	Similarity: 0.05
Database, Document13	Similarity: 0.24
Database, Document14	Similarity: 0.15
Database, Document15	Similarity: 0.08
Database, Document16	Similarity: 0.05
Database, Document17	Similarity: 0.05
Database, Document18	Similarity: 0.08
Database, Document19	Similarity: 0.05
Database, Document2	Similarity: 0.07
Database, Document20	Similarity: 0.18
Database, Document21	Similarity: 0.05
Database, Document22	Similarity: 0.03
Database, Document23	Similarity: 0.13
Database, Document24	Similarity: 0.05
Database, Document25	Similarity: 0.05
Database, Document26	Similarity: 0.02
Database, Document27	Similarity: 0.11
Database, Document28	Similarity: 0.05
Database, Document29	Similarity: 0.05
Database, Document3	Similarity: 0.16
Database, Document30	Similarity: 0.05
Database, Document31	Similarity: 0.11
Database, Document32	Similarity: 0.05
Database, Document33	Similarity: 0.08
Database, Document34	Similarity: 0.08
Database, Document35	Similarity: 0.05
Database, Document36	Similarity: 0.05
Database, Document37	Similarity: 0.03
Database, Document38	Similarity: 0.05
Database, Document39	Similarity: 0.08
Database, Document4	Similarity: 0.11
Database, Document40	Similarity: 0.08
Database, Document41	Similarity: 0.11
Database, Document42	Similarity: 0.05
Database, Document43	Similarity: 0.08
Database, Document44	Similarity: 0.08
Database, Document45	Similarity: 0.08
Database, Document46	Similarity: 0.02
Database, Document47	Similarity: 0.05
Database, Document48	Similarity: 0.02
Database, Document49	Similarity: 0.05
Database, Document5	Similarity: 0.11
Database, Document50	Similarity: 0.05
Database, Document51	Similarity: 0.03
Database, Document52	Similarity: 0.14
Database, Document53	Similarity: 0.14
Database, Document54	Similarity: 0.11
Database, Document55	Similarity: 0.08
Database, Document56	Similarity: 0.05
Database, Document57	Similarity: 0.05
Database, Document58	Similarity: 0.06
Database, Document59	Similarity: 0.08
Database, Document6	Similarity: 0.14
Database, Document60	Similarity: 0.12
Database, Document61	Similarity: 0.05
Database, Document62	Similarity: 0.15
Database, Document63	Similarity: 0.11
Database, Document64	Similarity: 0.11
Database, Document65	Similarity: 0.09
Database, Document66	Similarity: 0.11
Database, Document67	Similarity: 0.05
Database, Document68	Similarity: 0.06
Database, Document69	Similarity: 0.05
Database, Document7	Similarity: 0.08
Database, Document70	Similarity: 0.11
Database, Document71	Similarity: 0.08


**2. Output with 1 data node:**
Document1, Document10	Similarity: 0.10
Document1, Document11	Similarity: 0.07
Document1, Document12	Similarity: 0.07
Document1, Document13	Similarity: 0.06
Document1, Document14	Similarity: 0.11
Document1, Document15	Similarity: 0.12
Document1, Document16	Similarity: 0.06
Document1, Document17	Similarity: 0.03
Document1, Document18	Similarity: 0.03
Document1, Document19	Similarity: 0.07
Document1, Document2	Similarity: 0.06
Document1, Document20	Similarity: 0.07
Document1, Document21	Similarity: 0.06
Document1, Document22	Similarity: 0.03
Document1, Document23	Similarity: 0.10
Document1, Document24	Similarity: 0.03
Document1, Document25	Similarity: 0.03
Document1, Document26	Similarity: 0.03
Document1, Document27	Similarity: 0.03
Document1, Document28	Similarity: 0.03
Document1, Document29	Similarity: 0.03
Document1, Document3	Similarity: 0.10
Document1, Document30	Similarity: 0.03
Document1, Document31	Similarity: 0.11
Document1, Document32	Similarity: 0.03
Document1, Document33	Similarity: 0.07
Document1, Document34	Similarity: 0.07
Document1, Document35	Similarity: 0.07
Document1, Document36	Similarity: 0.07
Document1, Document37	Similarity: 0.00
Document1, Document38	Similarity: 0.03
Document1, Document39	Similarity: 0.07
Document1, Document4	Similarity: 0.03
Document1, Document40	Similarity: 0.03
Document1, Document41	Similarity: 0.07
Document1, Document42	Similarity: 0.03
Document1, Document43	Similarity: 0.07
Document1, Document44	Similarity: 0.10
Document1, Document45	Similarity: 0.07
Document1, Document46	Similarity: 0.07
Document1, Document47	Similarity: 0.07
Document1, Document48	Similarity: 0.00
Document1, Document49	Similarity: 0.03
Document1, Document5	Similarity: 0.10
Document1, Document50	Similarity: 0.03
Document1, Document6	Similarity: 0.07
Document1, Document7	Similarity: 0.06
Document1, Document8	Similarity: 0.07
Document1, Document9	Similarity: 0.11
Document10, Document11	Similarity: 0.03
Document10, Document12	Similarity: 0.07
Document10, Document13	Similarity: 0.03
Document10, Document14	Similarity: 0.03
Document10, Document15	Similarity: 0.04
Document10, Document16	Similarity: 0.03
Document10, Document17	Similarity: 0.07
Document10, Document18	Similarity: 0.03
Document10, Document19	Similarity: 0.11
Document10, Document2	Similarity: 0.03
Document10, Document20	Similarity: 0.03
Document10, Document21	Similarity: 0.03
Document10, Document22	Similarity: 0.03
Document10, Document23	Similarity: 0.03
Document10, Document24	Similarity: 0.07
Document10, Document25	Similarity: 0.04
Document10, Document26	Similarity: 0.11
Document10, Document27	Similarity: 0.03
Document10, Document28	Similarity: 0.03
Document10, Document29	Similarity: 0.07
Document10, Document3	Similarity: 0.00
Document10, Document30	Similarity: 0.07
Document10, Document31	Similarity: 0.00
Document10, Document32	Similarity: 0.04
Document10, Document33	Similarity: 0.03
Document10, Document34	Similarity: 0.03
Document10, Document35	Similarity: 0.11
Document10, Document36	Similarity: 0.03
Document10, Document37	Similarity: 0.00
Document10, Document38	Similarity: 0.03
Document10, Document39	Similarity: 0.03
Document10, Document4	Similarity: 0.03
Document10, Document40	Similarity: 0.07
Document10, Document41	Similarity: 0.03
Document10, Document42	Similarity: 0.03
Document10, Document43	Similarity: 0.03
Document10, Document44	Similarity: 0.07
Document10, Document45	Similarity: 0.03
Document10, Document46	Similarity: 0.03
Document10, Document47	Similarity: 0.03
Document10, Document48	Similarity: 0.03
Document10, Document49	Similarity: 0.11
Document10, Document5	Similarity: 0.03
Document10, Document50	Similarity: 0.04
Document10, Document6	Similarity: 0.03
Document10, Document7	Similarity: 0.07
Document10, Document8	Similarity: 0.00
Document10, Document9	Similarity: 0.03
Document11, Document12	Similarity: 0.03

Document6, Document8	Similarity: 0.16
Document6, Document9	Similarity: 0.08
Document7, Document8	Similarity: 0.00
Document7, Document9	Similarity: 0.15
Document8, Document9	Similarity: 0.04


### Note:
The obtained output for both 3 nodes and 1 node was way too large, around 5000 lines, so I have shown a small segment of my obtained output here. You can find the complet output in the respective output files.
