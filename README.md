# Script Liferay Robot (Smiles)

Hi! I'm Nicolas, the script executor robot! I'm going to help you by executing scripts in a customer site. 

Be sure you are connected at customer VPN. 

If you get some handshake error try changing your internet connection from wi-fi to 4G. 

Type -help to get more information about the parameters you must to give me Nice to meet you! :)

## Step-by-step Execution

Build:

```
mvn clean package
```

Execute:

```
java -jar target/scriptrobot-1.0.0-spring-boot.jar -sclearCache.groovy -m100 -c12 -hprd -eblue -uadministrator -p*******
```


## Config Help

* **-s[scriptName]**: The name of the script which is saved in the scripts package
* **-f[printStatus]**: Dir where the script output is going to be saved
* **-m[maxAttempts]**: Number of attempts that the robot is going to perform to connect on the server
* **-c[clusterSize]**: Number of cluster nodes that the robot is going to discovery and try connecting into to it.
* **-h[server]**: The server prefix hostname
* **-e[blueGreen]**: blue or green
* **-sfx[sufix]**: The server sufix hostname
* **-u[user]**: The username to access liferay portal
* **-p[pass]**: The user password

## How to export jar

Maven is configured to generate the jar with all dependencies. All you need to do is run `mvn package` and the jar-with-dependencies will be created in target/ dir

