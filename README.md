how to run lab 2: 
SETUP 

export JAVA_HOME=/usr/jdk64/jdk1.8.0_112
export PATH=${JAVA_HOME}/bin:${PATH}
export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar


UPLOAD DATA TO HDFS 

hadoop fs -mkdir -p /user/lubo/input/sales
hadoop fs -copyFromLocal sales.txt /user/lubo/input/sales/


PLAIN JAVA (SalesByDayJava) 

javac SalesByDayJava.java
java SalesByDayJava sales.txt java_output.txt
cat java_output.txt


MAPREDUCE WITHOUT COMBINER (Salesbyday) 

hadoop com.sun.tools.javac.Main Salesbyday.java
jar cvf Salesbyday.jar *.class
hadoop fs -rm -r /user/lubo/output
hadoop jar Salesbyday.jar Salesbyday /user/lubo/input/sales /user/lubo/output
hadoop fs -cat /user/lubo/output/part-r-00000


MAPREDUCE WITH COMBINER (Salesbyday)

hadoop fs -rm -r /user/lubo/output
hadoop jar Salesbyday.jar Salesbyday /user/lubo/input/sales /user/lubo/output combiner
hadoop fs -cat /user/lubo/output/part-r-00000
