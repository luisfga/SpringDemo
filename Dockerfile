FROM tomcat:jdk8-openjdk-buster

RUN mkdir /luisfga

COPY target/luisfga-spring-demo-0.0.1-SNAPSHOT.jar /luisfga/spring-demo.jar

EXPOSE ${PORT}

ENTRYPOINT [ "java", "-Xss512k -XX:MaxRAM=500m", "-jar", "-Dserver.port=$PORT", "/luisfga/spring-demo.jar"]

#TIPS
# não esquecer de $> mvn clean package
# Build command $> docker build -t spring-demo:dev .
# Run command $> docker run -p 8080:8080 spring-demo:dev
# listar containeres em execução: docker ps
# acessar bash dentro de um container que esteja rodando: docker exec -it <containerID> /bin/bash

#HEROKU tips
#push $> heroku container:push web -a luisfga-spring-demo
#release $> heroku container:release web -a luisfga-spring-demo