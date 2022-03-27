### Technologies Used:
- Apache Kafka 
- Open Liberty 19
- Docker
- Java 11
- J2EE
- Web Services
- Maven
- JWT Token Validation
- Distributed DB Yugabyte (Cassandra, Postgres and Redis)

### Testing Tools Used
- Apache Junit
- Postman
- Apache Jmeter

### Third Part Helper Tools Used

- IntelliJ IDEA 2021.3.3 (Community Edition)
- DBeaver Lite 22.0.0
- Table Plus

# How to Run the Application

## Pre-requisites:

### 1. Install Yugabyte in Container Mode

docker pull yugabytedb/yugabyte:2.13.0.1-b2
docker run -d --name yugabyte  -p7000:7000 -p9000:9000 -p5433:5433 -p9042:9042 yugabytedb/yugabyte:2.13.0.1-b2 bin/yugabyted start --daemon=false --ui=false

see: https://download.yugabyte.com/#docker

### 2. Checkout https://github.com/operations-relay42/iot-producer-simulator-api

Update root "docker-compose.yml" as below (update 3 kafka brokers to work with outer network container/s)

**KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka_0:29092,PLAINTEXT_HOST://host.docker.internal:9092
**KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka_1:29093,PLAINTEXT_HOST://host.docker.internal:9094
**KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka_2:29094,PLAINTEXT_HOST://host.docker.internal:9095

please follow steps mentioned in that repo read_me.md to run the Kafka Producer.

![image](https://user-images.githubusercontent.com/3264237/160287473-0bf4af0f-a69d-4567-bc49-b048af289ff7.png)

### 3. Install Tools
- Docker
- Maven
- JDK 11

## Steps to Build and Run this Project

### 1. Start Backend Openliberty Server

cd to \kafka-relay-test\kafka-relay folder
Run command ->  mvn clean install -Pdocker-run

![image](https://user-images.githubusercontent.com/3264237/160287813-d16d5af3-d26c-41bd-999c-7f3c3cd021dc.png)

Notes: "CWWKF0011I: The defaultServer server is ready to run a smarter planet. The defaultServer server started in..." check this apprear in the Log.

Preconfigured protocol and Port: http: 9080, https: 9443 

### 2. Start Front End Openliberty Server (Token creator and validator endpoint)  

cd to **\kafka-relay-test\kafka-relay** folder

Run command ->  **mvn -f .\boot\frontend\pom.xml clean install liberty:run**


Notes: **"The defaultServer server is ready to run a smarter planet. The defaultServer server started in.."** check this apprear in the log.

Preconfigured protocol and port: http: 9090, https: 9091

### 3. Login to the application 

If Steps 1 and 2 are success, then go to http://localhost:9090/

click the log in link.
![image](https://user-images.githubusercontent.com/3264237/160288082-6e007ae7-64c0-4baf-8138-94cd1a75bcbc.png)

Then you will be forwarded to basic login window.

![image](https://user-images.githubusercontent.com/3264237/160288136-ef302bfd-c095-45e8-82e7-1860dea43116.png)

use below **login pwds**

 ![image](https://user-images.githubusercontent.com/3264237/160288176-7c0bd55a-7043-43b0-8a4d-05be265bef14.png)

if login success you will get something similar
![image](https://user-images.githubusercontent.com/3264237/160288267-1a73f086-4595-4dbe-a5d2-09c453eadbd7.png)

Copy the JWT toekn for future requests.

### 4. Postman Collection for supported endpoints.

Please check the uploaded postman collection file in **\kafka-relay-test\kafka-relay\boot\kafka - Relay.postman_collection.json**

![image](https://user-images.githubusercontent.com/3264237/160288363-84165959-8c87-4409-82b4-b04989dec01a.png)

Include your JWT token inside Authorization, Bearer Token Section

![image](https://user-images.githubusercontent.com/3264237/160288555-cdaefe2b-0f2b-4da7-a6f7-ce3d6b8a9385.png)


## API Endpoints

### 1) Create Base Model Tables to Store Event Data 
Can call this multiple times, but it will create tables and keyspaces only once.

https://localhost:9443/relay/kafka/consumer/create-models

### 2) Start Kafka Consumer 
This will connect to existing kafka topic - iot-data, and this will run/wait untill it recieve wake up signal (singleton thread).

**https://localhost:9443/relay/kafka/consumer/start**

### 3) As mentioned in https://github.com/operations-relay42/iot-producer-simulator-api , generate kafka events. 


![image](https://user-images.githubusercontent.com/3264237/160288705-729c3fca-ba49-4c30-b5f8-ab06968a9a7f.png)

Notice that backend server start consume from the topic (iot-data).

![image](https://user-images.githubusercontent.com/3264237/160288730-bd8ec2f0-0524-4cf9-b1de-6bb346d2d37a.png)

### 4) Stop Kafka Consumer (This will send stop/wake signal to backend server)
**https://localhost:9443/relay/kafka/consumer/stop**

![image](https://user-images.githubusercontent.com/3264237/160288812-fdcec19e-be21-4c55-80c7-2aca86cc2b1c.png)

### 5) Read Min value in a given Time Frame 
Be mindful to add the time as UTC (it like that at the moment :))
![image](https://user-images.githubusercontent.com/3264237/160288842-8cefc4f8-04b8-4352-ad11-84d76d041be8.png)

### 5) Read Max value in a given Time Frame 
![image](https://user-images.githubusercontent.com/3264237/160288891-f32c67d1-8422-472b-a21b-3f5da8c6c212.png)

### 6) Read Avg value in a given Time Frame 
![image](https://user-images.githubusercontent.com/3264237/160288928-dda635c6-b791-4257-8fdf-5f4943a69f4c.png)

### 7) Read Median value in a given Time Frame (This is not implemented :/)
![image](https://user-images.githubusercontent.com/3264237/160288962-d497a09a-eb87-4996-a38b-c4cb9e4bb3b9.png)


## Architecuture

- Backend server is a container based application, focusing to deploy as microservice in kubernetes cluster.

- Code written as maven modules to easy readabilty and separate the purpose.
![image](https://user-images.githubusercontent.com/3264237/160290209-b63c5105-71e9-4ace-9ef7-4fc83ecefc1e.png)

- Token Validation is not a direct part of this project, that's why it's in boot :)

- Yugabyte taken as the database as it contains all three dbs (NoSQL: Cassandra, Relational: Postgress, In Mememory: Redis)

- Cassandra DB taken to store the Incoming IOT Data (iot_event_data_tab), Cassandra known for FAST DB Writes.
![image](https://user-images.githubusercontent.com/3264237/160290659-8b757abd-762f-42dd-8dae-ab7c39851953.png)

- Thought to use "Postgress DB" as persistent Event Model information storing. (but couldn't complete :/)

- Thought to use "Redis DB" as fast access cache Event Model file store (but couldn't complete :/), Idea was to flush the Redis DB in a event of staructure change.

## Limitations

- Can not dynamically update event model files, but can be improved with above design architecure.

- I have tested with large set of events, it worked, but at the moment there is no load balancing or auto scale up functinality, so it will break in somepoint.

## Improvements

- Parameters all possible configurations (ex: Possiblly through a init configuration filter)

- Dynamically update event model files.

- Maintain In Memeory Event Model cache.

- Add more integration tets (automatted).

- Code Coverage with Sonar Cloud etc.

- Run Trivy Container Scan.

- Run Dependency Scan.

- Many more.

### Some Useful Referances:

[1] https://www.yugabyte.com/

[2] https://openliberty.io/guides/microprofile-jwt.html#configuring-microprofile-jwt

[3] https://openliberty.io/docs/22.0.0.2/reference/feature/mpReactiveStreams-1.0.html

[4] https://kafka.apache.org/
