FROM tomcat:jdk8-openjdk-buster

RUN mkdir /app

COPY target/luisfga-spring-demo.jar /app/luisfga-spring-demo.jar

#essas variáveis devem estar no localhost (em desenvolvimento) e no servidor, no caso do momento: Heroku (Config Vars)
ENV DEVELOPMENT_MAIL_HOST=${DEVELOPMENT_MAIL_HOST}
ENV DEVELOPMENT_MAIL_PORT=${DEVELOPMENT_MAIL_PORT}
ENV DEVELOPMENT_MAIL_USERNAME=${DEVELOPMENT_MAIL_USERNAME}
ENV DEVELOPMENT_MAIL_PASSWORD=${DEVELOPMENT_MAIL_PASSWORD}

EXPOSE ${PORT}

#Development entry point
#ENTRYPOINT [ "java", "-jar", "-Dserver.port=8080", "/app/luisfga-spring-demo.jar"]

ENTRYPOINT [ "java", "-Xss512k -XX:MaxRAM=500m", "-jar", "-Dserver.port=$PORT", "/app/luisfga-spring-demo.jar"]

#TIPS
# não esquecer de -> mvn clean package

#DOCKER TIPS
# Excluir 'dangling' containers -> docker rmi $(docker images --filter "dangling=true" -q)

# Build command -> docker build -t spring-demo:dev .

# Run command -> docker run -p 8080:8080 spring-demo:dev

# Listar containeres em execução -> docker ps

# Acessar bash dentro de um container que esteja rodando -> docker exec -it <containerID> /bin/bash

# inspecionar o exit code de um container que não estartou
# a qualquer momento -> docker inspect <container-id> --format='{{.State.ExitCode}}'
# logo após a falha -> echo $?

#HEROKU TIPS
#push -> heroku container:push web -a luisfga-spring-demo
#release -> heroku container:release web -a luisfga-spring-demo