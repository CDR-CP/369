#!/bin/bash

rm -rf *.class lab2.jar output output_combiner java_output.txt hadoop_output.txt hadoop_output_combiner.txt

javac --release 11 SalesByDayJava.java
javac --release 11 -classpath "$(hadoop classpath)" -d . SalesByDay.java

jar -cvf lab2.jar *.class > /dev/null

java SalesByDayJava sales.txt java_output.txt

rm -rf output
time hadoop jar lab2.jar SalesByDay sales.txt output
cp output/part-r-00000 hadoop_output.txt

rm -rf output_combiner
time hadoop jar lab2.jar SalesByDay sales.txt output_combiner combiner
cp output_combiner/part-r-00000 hadoop_output_combiner.txt

diff hadoop_output.txt java_output.txt
