# INF112 Maven template 
Simple skeleton with libgdx. 


## Known bugs
Currently throws "WARNING: An illegal reflective access operation has occurred", 
when the java version used is >8. This has no effect on function or performance, and is just a warning.


## How to run
1. ensure all dependencies are met: ```$ mvn install```
2. build the package: ```$ mvn package```
3. run the server: ```$ java -jar target/Server.jar```
4. run the client: ```$ java -jar target/Client.jar```

*multiple clients can connect to same server, client connects to localhost.

## How to run on mac
make sure you add ```-XstartOnFirstThread``` as a jvm argument.


## How to run test
1. run the tests: ```$ mvn test```
