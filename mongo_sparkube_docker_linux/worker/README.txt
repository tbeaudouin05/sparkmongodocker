# install docker (if you do not have the installer, you can get it here https://docs.docker.com/docker-for-windows/install/)
# NB: they will ask you to create a docker account at some point, just do it
1. Double-click on Docker for Windows Installer.exe
2. Keep ALL DEFAULT SETTINGS
3. DO NOT use "Docker for Windows" (do not tick the optional box at some point during installation)

# if in a country under American sanctions, you need to set up a docker mirror
1. on Windows, go to docker settings (show hidden icons then right click on the whale)
2. Go to Daemon
3. Paste this address in Registry mirrors: https://registry.docker-cn.com
4. click on Apply

# make sure worker IP address = master IP address = [this IP address] (cf.master folder README.txt)
1. edit create_worker.bat (same folder as this README.txt): change 192.168.100.160 to [this IP address] (cf. master folder README.txt)

# start spark worker
1. run create_worker.bat

# That's it! Congrats