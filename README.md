# Hadoop-Tutorial

This project assumes that the configuration for Hadoop is complete.



## Run WordCount

### Clone Project

- Command: git clone <https://github.com/MangKyu/Hadoop-Tutorial>



### Run Hadoop Cluster

- Command: start-dfs.sh
- Command: start-yarn.sh



### Compile and Build jar 

- Command: hadoop com.sun.tools.javac.Main MyWordCount.java
- Command: jar cf MyWordCount.jar MyWordCount*.class



### Create Directory in HDFS and put Files for WordCount

- Command: hdfs dfs -mkdir /wordcount_input/
- Command: hdfs dfs -put /home/{username}/hadoop-2.9.2/wordcount_input/* /wordcount_input/
  - ex) hdfs dfs –put /home/mang/hadoop-2.9.2/wordcount_input/* /wordcount_input/
  - /home/mang/hadoop-2.9.2 is directory where hadoop is installed
  - /home/mang/hadoop-2.9.2/wordcount_input/* is directory where text files for wordcount exist
  - /wordcount_input/* is a hdfs directory where to put text files



### Run WordCount and check result

- Command: hadoop jar MyWordCount.jar MyWordCount /wordcount_input /wordcount_output
- Command: hdfs dfs -ls /wordcount_output/
- Command: hdfs dfs -cat /output/part-r-0000



### Stop Hadoop Cluster

- Command: stop-dfs.sh
- Command: stop-yarn.sh





## Run Inverted Index

### Clone Project

- Command: git clone <https://github.com/MangKyu/Hadoop-Tutorial>



### Run Hadoop Cluster

- Command: start-dfs.sh
- Command: start-yarn.sh



### Compile and Build jar 

- Command: hadoop com.sun.tools.javac.Main MyInvertedIndex.java
- Command: jar cf MyInvertedIndex.jar MyInvertedIndex*.class



### Create Directory in HDFS and put Files for Inverted Index

- Command: hdfs dfs -mkdir /invertedindex_input/
- Command: hdfs dfs -put /home/{username}/hadoop-2.9.2/invertedindex_input/* /invertedindex_input/
  - ex) hdfs dfs –put /home/mang/hadoop-2.9.2/invertedindex_input/* /invertedindex_input/
  - /home/mang/hadoop-2.9.2 is directory where hadoop is installed
  - /home/mang/hadoop-2.9.2/invertedindex_input/* is directory where text files for invertedindex exist
  - /invertedindex_input/* is a hdfs directory where to put text files



### Run Inverted Index and check result

- Command: hadoop jar MyInvertedIndex.jar MyInvertedIndex /invertedindex_input /invertedindex_output
- Command: hdfs dfs -ls /invertedindex_output/
- Command: hdfs dfs -cat /output/part-r-0000



### Stop Hadoop Cluster

- Command: stop-dfs.sh
- Command: stop-yarn.sh

