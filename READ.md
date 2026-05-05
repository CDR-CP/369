### SETUP (run once per session)

export JAVA_HOME=/usr/jdk64/jdk1.8.0_112
export PATH=${JAVA_HOME}/bin:${PATH}
export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar


### UPLOAD DATA TO HDFS

hadoop fs -mkdir -p /user/croger31/input/sales
hadoop fs -mkdir -p /user/croger31/input/products
hadoop fs -copyFromLocal /home/croger31/input/data/sales/sales.txt /user/croger31/input/sales/
hadoop fs -copyFromLocal /home/croger31/input/data/products/products.txt /user/croger31/input/products/


### LAB 2

cd /home/croger31/input/lab2

- Plain Java
javac SalesByDayJava.java
java SalesByDayJava /home/croger31/input/data/sales/sales.txt java_output.txt
cat java_output.txt

- MapReduce (no combiner)
hadoop com.sun.tools.javac.Main Salesbyday.java
jar cvf Salesbyday.jar *.class
hadoop fs -rm -r /user/croger31/output
hadoop jar Salesbyday.jar Salesbyday /user/croger31/input/sales /user/croger31/output
hadoop fs -cat /user/croger31/output/part-r-00000

- MapReduce (with combiner)
hadoop fs -rm -r /user/croger31/output
hadoop jar Salesbyday.jar Salesbyday /user/croger31/input/sales /user/croger31/output combiner
hadoop fs -cat /user/croger31/output/part-r-00000


### LAB 3

cd /home/croger31/input/lab3

- Plain Java
javac SalesByDayDetailJava.java
java SalesByDayDetailJava /home/croger31/input/data/sales/sales.txt java_output.txt
cat java_output.txt

- MapReduce
hadoop com.sun.tools.javac.Main SalesByDayDetail.java
jar cvf SalesByDayDetail.jar *.class
hadoop fs -rm -r /user/croger31/output
hadoop jar SalesByDayDetail.jar SalesByDayDetail /user/croger31/input/sales /user/croger31/output
hadoop fs -cat /user/croger31/output/part-r-00000


### LAB 4

cd /home/croger31/input/lab4

- Plain Java
javac TopTenProductsJava.java
java TopTenProductsJava /home/croger31/input/data/products/products.txt java_output.txt
cat java_output.txt

- MapReduce
hadoop com.sun.tools.javac.Main TopTenProducts.java
jar cvf TopTenProducts.jar *.class
hadoop fs -rm -r /user/croger31/output
hadoop jar TopTenProducts.jar TopTenProducts /user/croger31/input/products /user/croger31/output
hadoop fs -cat /user/croger31/output/part-r-00000


### NOTES

- Always run the SETUP exports at the start of each session
- Data only needs to be uploaded to HDFS once
- Always rm -r /user/croger31/output before re-running any MapReduce job
- Plain Java reads from local filesystem, MapReduce reads from HDFS
- Lab 2 and Lab 3 both use sales.txt, Lab 4 uses products.txt




ssh croger31@ambari-node5.csc.calpoly.edu
cd /home/croger31
git clone REPO