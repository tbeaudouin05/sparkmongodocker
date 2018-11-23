# NB: before using sparkshell, you should install master! cf. master folder README.txt

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

# write your scala code inside userweeklytracker.scala
# NB you can change the file name to anything.scala 
# BUT you have to set scalaFile=anything.scala inside run.bat

# add your local jars in the same folder as run.bat (jars are scala packages that you can then import in your code!)
# NB: you need to add these jars inside run.bat > localJars=anything1.jar,anything2.jar

# make sure all config variables in run.bat are accurate
1. inside run.bat (same folder as this README.txt): set sparkMasterIP=[this IP address] (cf. master folder README.txt, [this IP address] is the master IP address!)
2. set all other variables appropriately (ex: if testing, set mongoIP=[the IP address of your computer]! not the IP of the master! you have to change in mongo bin folder on your computer mongod.cfg network > net > bindIP, search Google!)

# scalaFile=name of your scala file
# interactive=true/false, true if you want to test your code, false if you want to put on the server
# sparkMasterIP=the IP of the server where master is installed
# mongoReplaceDoc=true/false, false if you DO NOT want to replace all documents in the mongo collection where you write data
# mongoIP=the IP address:port of the mongo instance you want to connect to (if testing, set mongoIP=[the IP address of your computer]! not the IP of the master! you have to change in mongo bin folder on your computer mongod.cfg network > net > bindIP, search Google!)
# mongoUser= the user to connect with
# mongoPw= password for this user
# mongoAuthDb= the authentification database for this user
# localJars=anything1.jar,anything2.jar : these are the local jars / packages you can optionally add to add packages to scala (it is not the only way to add packages but for instance the only way to add sparkube for now)

# run your scala code on spark
1. double-click on run.bat

# That's it! Congrats