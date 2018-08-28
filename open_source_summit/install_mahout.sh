#!/bin/bash

# Install Apache Spark
wget https://archive.apache.org/dist/spark/spark-2.1.1/spark-2.1.1-bin-hadoop2.7.tgz
tar xzvf spark-2.1.1-bin-hadoop2.7.tgz
./spark-2.1.1-bin-hadoop2.7/sbin/start-all.sh
export SPARK_HOME=$PWD/spark-2.1.1-bin-hadoop2.7
# Visit http://localhost:8080, get Spark Master URL, e.g., spark://bob:7077
export MASTER=spark://localhost:7077

# Install Apache Mahout
wget http://apache.cs.utah.edu/mahout/0.13.0/apache-mahout-distribution-0.13.0.tar.gz
tar xzvf apache-mahout-distribution-0.13.0.tar.gz
export MAHOUT_HOME=$PWD/apache-mahout-distribution-0.13.0
cd apache-mahout-distribution-0.13.0
./bin/mahout spark-shell
