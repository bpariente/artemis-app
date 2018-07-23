FROM qa.stratio.com/stratio/java-microservice-dockerbase:1.0.0

VOLUME /tmp

ADD target/*.jar app.jar
ADD docker/entrypoint.sh entrypoint.sh

RUN touch /data/app.jar && \
    chmod 600 /data/entrypoint.sh

ENTRYPOINT ["bash", "/data/entrypoint.sh" ]