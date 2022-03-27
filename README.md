Technologies Used:
- Kafka 
- Open Liberty 19
- Docker
- Java 11
- J2EE
- Web Services
- Maven
- JWT Token Validation
- Distributed DB Yugabyte (Cassandra, Postgres and Redis)

Testing Tools
- Junit
- Postman
- Jmeter

Third Part Helper Tools

- Inteliji 
- DBeaver Lite 22.0.0
- Table Plus

How to Run the Application

Prequists Steps

1. Install Yugabyte in Container Mode

docker pull yugabytedb/yugabyte:2.13.0.1-b2
docker run -d --name yugabyte  -p7000:7000 -p9000:9000 -p5433:5433 -p9042:9042 yugabytedb/yugabyte:2.13.0.1-b2 bin/yugabyted start --daemon=false --ui=false

https://download.yugabyte.com/#docker

2. Checkout https://github.com/operations-relay42/iot-producer-simulator-api

Update docker-compose.yml to work with dockerized enviroment
Change to 3 broakers as below
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka_0:29092,PLAINTEXT_HOST://host.docker.internal:9092
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka_1:29093,PLAINTEXT_HOST://host.docker.internal:9094
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka_2:29094,PLAINTEXT_HOST://host.docker.internal:9095

![image](https://user-images.githubusercontent.com/3264237/160287473-0bf4af0f-a69d-4567-bc49-b048af289ff7.png)

3. Install Tools
- Maven
- JDK 11

**Steps to Run this Project

1. Start Backend Openliberty Server

cd to \kafka-relay-test\kafka-relay folder
Run command ->  mvn clean install -Pdocker-run

![image](https://user-images.githubusercontent.com/3264237/160287813-d16d5af3-d26c-41bd-999c-7f3c3cd021dc.png)

Notes: "CWWKF0011I: The defaultServer server is ready to run a smarter planet. The defaultServer server started in..." check this apprear in the Log.

Preconfigured Port: http: 9080 , https: 9443 

2. Start Front End Open Liberty (Token Creator Validator endpoint)  

cd to \kafka-relay-test\kafka-relay folder
Run command ->  mvn -f .\boot\frontend\pom.xml clean install liberty:run

Notes: "The defaultServer server is ready to run a smarter planet. The defaultServer server started in.." check this apprear in the log.

Preconfigured Port: http: 9090 , https: 9091

3. Login to the application 

If Steps 1 and 2 are success, then go to http://localhost:9090/

click log in.
![image](https://user-images.githubusercontent.com/3264237/160288082-6e007ae7-64c0-4baf-8138-94cd1a75bcbc.png)

Then you will be forwarded to basic login window.

![image](https://user-images.githubusercontent.com/3264237/160288136-ef302bfd-c095-45e8-82e7-1860dea43116.png)

use below login pwds.

 ![image](https://user-images.githubusercontent.com/3264237/160288176-7c0bd55a-7043-43b0-8a4d-05be265bef14.png)




