FROM tomcat:jdk8-openjdk-buster

RUN mkdir /app

COPY target/luisfga-spring-demo.jar /app/luisfga-spring-demo.jar

#essas variáveis devem estar no localhost (em desenvolvimento) e no servidor, no caso do momento: Heroku (Config Vars)
ENV APP_MAIL_SESSION_HOST=${APP_MAIL_SESSION_HOST}
ENV APP_MAIL_SESSION_PORT=${APP_MAIL_SESSION_PORT}
ENV APP_MAIL_SESSION_USERNAME=${APP_MAIL_SESSION_USERNAME}
ENV APP_MAIL_SESSION_PASSWORD=${APP_MAIL_SESSION_PASSWORD}

#Development port and entry point
#EXPOSE 8080
#ENTRYPOINT [ "java", "-jar", "-Dserver.port=8080", "/app/luisfga-spring-demo.jar"]

#Production port and entry point
EXPOSE ${PORT}
ENTRYPOINT [ "java", "-Xss512k -XX:MaxRAM=500m", "-jar", "-Dserver.port=$PORT", "/app/luisfga-spring-demo.jar"]

#TIPS
# não esquecer de -> mvn clean package

#DOCKER TIPS
# Excluir 'dangling' containers -> docker rmi $(docker images --filter "dangling=true" -q)

# Build command DEV  -> docker build -t spring-demo:dev .
# Build command PROD -> docker build -t spring-demo:prd .

# Run command (DEV) -> docker run -p 8080:8080 spring-demo:dev

# Listar containeres em execução -> docker ps

# Acessar bash dentro de um container que esteja rodando -> docker exec -it <containerID> /bin/bash

# inspecionar o exit code de um container que não estartou
# a qualquer momento -> docker inspect <container-id> --format='{{.State.ExitCode}}'
# logo após a falha -> echo $?

#HEROKU TIPS
#se há apenas um container, p.e. :prd
#push -> heroku container:push web -a luisfga-spring-demo
#release -> heroku container:release web -a luisfga-spring-demo

#se buildou mais de um container, p.e. :dev e :prd é preciso marcar o container pra upload
#-> docker tag <image> registry.heroku.com/<app>/<process-type>
#por exemplo -> docker tag spring-demo:prd registry.heroku.com/luisfga-spring-demo/web

#depois push
#-> docker push registry.heroku.com/<app>/<process-type>
#por exemplo -> docker push registry.heroku.com/luisfga-spring-demo/web

#depois, release normal -> heroku container:release web -a luisfga-spring-demo