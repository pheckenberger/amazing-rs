# Amazing CO - Backend

Create PostgreSQL docker image:
```
docker pull postgres
docker run --name 'postgres' -e 'POSTGRES_PASSWORD=postgres123' -p 5432:5432 -d postgres
```

Run the application:
- Use the run configuration in the launch folder to run the application from Eclipse/SpringToolSuite.
- Use the SpringBoot Maven plugin to run the application: `mvn spring-boot:run`

Build the docker image:
```
spring-boot:build-image -Dmaven.test.skip -Dspring-boot.build-image.imageName=springio/gs-spring-boot-docker
```

Start or stop the infrastructure using the docker-compose.yml in the base directory:
```
docker-compose up -d
docker-compose down
```

Sample fetches:
```
curl -i http://reader:reader123@localhost:8080/v1/organizations/1
curl -i http://reader:reader123@localhost:8080/v1/organizations/9
curl -i http://reader:reader123@localhost:8080/v1/organizations/12
```

Sample valid moves:
```
curl -i -X PATCH -H 'Content-Type: application/json' -d '{ "id": 10 }' http://writer:writer123@localhost:8080/v1/organizations/7/move
curl -i -X PATCH -H 'Content-Type: application/json' -d '{ "id": 6 }' http://writer:writer123@localhost:8080/v1/organizations/7/move
```

Sample invalid moves:
```
curl -i -X PATCH -H 'Content-Type: application/json' -d '{ "id": 10 }' http://writer:writer123@localhost:8080/v1/organizations/1/move
curl -i -X PATCH -H 'Content-Type: application/json' -d '{ "id": 7 }' http://writer:writer123@localhost:8080/v1/organizations/5/move
```
