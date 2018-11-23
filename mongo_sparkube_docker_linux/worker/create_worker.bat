docker stop spark-worker0
docker rm spark-worker0
docker run -d -p 8081:8081 --name spark-worker0 --restart always actionml/spark worker spark://192.168.100.160:7077 -c 2 -m 2G