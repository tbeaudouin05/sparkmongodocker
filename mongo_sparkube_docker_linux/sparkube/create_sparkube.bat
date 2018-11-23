
REM CHANGE THE VARIABLES BELOW AS PER THE JOB YOU WANT TO RUN
REM set scalaFile= if you don't want to run any file (for interactive mode for instances)
set scalaFile=sparkube0.scala
REM if interactive session, set interactive=true, otherwise, set interactive=false
set interactive=true
set sparkMasterIP=192.168.10.178:7077
set mongoReplaceDoc=false
set mongoIP=192.168.10.178:27017
set mongoUser=admin
set mongoPw=Sepideh0205
set mongoAuthDb=admin
set localJars=sparkube.jar


setlocal ENABLEDELAYEDEXPANSION
REM replace \ by / and delete ":" in path to current currentFolder
set slash=/
set currentFolder=%~dp0
set currentFolder1=%currentFolder:\=!slash!%
set currentFolder2=%currentFolder1::=!!%

REM erase all former spark folders
for /d %%i in (%currentFolder1%spark*) do rd /S /Q "%%i"
for /d %%i in (%currentFolder1%metastore_db*) do rd /S /Q "%%i"
for /d %%i in (%currentFolder1%*resources) do rd /S /Q "%%i"
for /d %%i in (%currentFolder1%snappy*) do rd /S /Q "%%i"
for /d %%i in (%currentFolder1%blockmgr*) do rd /S /Q "%%i"

docker stop %scalaFile%
docker rm %scalaFile%

REM do not erase -it!! Session should be interactive

IF %interactive%==true (
docker run -v /host_mnt/%currentFolder2%:/tmp --name %scalaFile% --rm -it ^
actionml/spark shell --master spark://%sparkMasterIP% ^
--conf spark.mongodb.input.uri=mongodb://%mongoUser%:%mongoPw%@%mongoIP%/%mongoAuthDb%.test ^
--conf spark.mongodb.output.uri=mongodb://%mongoUser%:%mongoPw%@%mongoIP%/%mongoAuthDb%.test ^
--conf spark.mongodb.output.replaceDocument=%mongoReplaceDoc% ^
--packages org.mongodb.spark:mongo-spark-connector_2.11:2.1.4 ^
--conf spark.shuffle.service.enabled=false ^
--conf spark.dynamicAllocation.enabled=false ^
--conf spark.io.compression.codec=snappy ^
--conf spark.rdd.compress=true ^
--conf spark.serializer=org.apache.spark.serializer.KryoSerializer ^
--jars %localJars% ^
-i %scalaFile%
 ) ELSE ( 
docker run -v /host_mnt/%currentFolder2%:/tmp --name %scalaFile% --rm ^
actionml/spark shell --master spark://%sparkMasterIP% ^
--conf spark.mongodb.input.uri=mongodb://%mongoUser%:%mongoPw%@%mongoIP%/%mongoAuthDb%.test ^
--conf spark.mongodb.output.uri=mongodb://%mongoUser%:%mongoPw%@%mongoIP%/%mongoAuthDb%.test ^
--conf spark.mongodb.output.replaceDocument=%mongoReplaceDoc% ^
--packages org.mongodb.spark:mongo-spark-connector_2.11:2.1.4 ^
--conf spark.shuffle.service.enabled=false ^
--conf spark.dynamicAllocation.enabled=false ^
--conf spark.io.compression.codec=snappy ^
--conf spark.rdd.compress=true ^
--conf spark.serializer=org.apache.spark.serializer.KryoSerializer ^
--jars %localJars% ^
-i %scalaFile% > log\log_%DATE:~-4%-%DATE:~4,2%-%DATE:~7,2%-%TIME:~0,2%h%TIME:~3,2%m.txt 2>&1
 )