FROM java:8-jre-alpine
VOLUME /tmp
ADD ./examples/example-simple/build/libs/example-simple-0.1.0-dev.12.uncommitted+health.check.e198c26.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-server", "-Xms2g","-Xmx2g","-jar","/app.jar"]
