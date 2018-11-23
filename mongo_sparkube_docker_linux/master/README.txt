# install docker (if you do not have the installer, you can get it here https://docs.docker.com/docker-for-windows/install/)
# NB: they will ask you to create a docker account at some point, just do it
1. Double-click on Docker for Windows Installer.exe
2. Keep ALL DEFAULT SETTINGS
3. FOR VM MASTER USE "Docker for Windows" (TICK the optional box at some point during installation) - you can also switch to windows containers later (show hidden icons then right click on the whale)

#NB: to login to Docker, you need to use the vpn freegate if in Iran!!

# launch FREEGATE vpn

# set up proxy for docker
1. see what port freegate is using, in freegate panel, look at "connected to n servers, port: 8580" --> the port is 8580
2. go to docker setting > Proxies
3. if freegate port is 8580, under Web Server (HTTP) input localhost:8580
4. click on Apply

# set up mirror (if in Iran) AND enable Experimental features
1. on Windows, go to docker settings (show hidden icons then right click on the whale)
2. Go to Daemon
3. TICK the box Experimental features
4. Paste this address in Registry mirrors: https://registry.docker-cn.com
5. click on Apply

# start spark master node with two spark workers
1. get the IP address of your computer: in command line: ipconfig then find Wireless LAN adapter Wi-Fi > IPv4 address 
2. edit create_master_two_worker.bat (same folder as this README.txt) with this IP address - change spark://192.168.100.160:7077 to spark://[this IP address]:7077 everywhere
3. run create_master_two_worker.bat

# IF you do not want spark to take too much memory set -c 2 and -m 2G to -c 1 and -m 1G or lower depending on how much you allocated

# FYI: this is what is inside create_master_two_worker.bat:
docker run -d -p 8080:8080 -p 7077:7077 --name spark-master actionml/spark master
docker run -d -p 8081:8081 --name spark-worker0 actionml/spark worker spark://192.168.100.160:7077 -c 2 -m 2G
docker run -d -p 8082:8081 --name spark-worker1 actionml/spark worker spark://192.168.100.160:7077 -c 2 -m 2G
# 192.168.100.160 should be the (static) IP address of the master, all the workers will connect at this address, so it had better not change!
# if port 7077 is taken on your computer, change 7077:7077 to 7078:7077 and spark://192.168.100.160:7077 to spark://192.168.100.160:7078
# IF you do not want spark to take too much memory set -c 2 and -m 2G to -c 1 and -m 1G or lower depending on how much you allocated

# That's it! Congrats
 


