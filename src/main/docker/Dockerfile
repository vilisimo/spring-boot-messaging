FROM java:8
VOLUME /tmp
ADD messaging-app-1.0-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
CMD ["java", "-jar", "/app.jar" ]