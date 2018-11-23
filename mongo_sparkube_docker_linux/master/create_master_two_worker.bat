docker stop spark-master spark-worker0 spark-worker1
docker rm spark-master spark-worker0 spark-worker1
docker run -d -p 8080:8080 -p 7077:7077 --name spark-master --restart always --platform linux actionml/spark master
docker run -d -p 8081:8081 --name spark-worker0 --restart always actionml/spark worker spark://192.168.10.178:7077 -c 2 -m 2G