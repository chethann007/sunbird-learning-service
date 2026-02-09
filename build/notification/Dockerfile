FROM adoptopenjdk/openjdk11:alpine-slim
RUN apk update \
    && apk add  unzip \
    && apk add curl \
    && adduser -u 1001 -h /home/sunbird/ -D sunbird \
    && mkdir -p /home/sunbird/
RUN chown -R sunbird:sunbird /home/sunbird
USER sunbird
COPY modules/notification/service/target/notification-service-1.0.0-dist.zip /home/sunbird/
RUN unzip /home/sunbird/notification-service-1.0.0-dist.zip -d /home/sunbird/
EXPOSE 9000
WORKDIR /home/sunbird/
CMD java -XX:+PrintFlagsFinal $JAVA_OPTIONS -Dlog4j2.formatMsgNoLookups=true -Dplay.server.http.idleTimeout=180s -cp '/home/sunbird/notification-service-1.0.0/lib/*' play.core.server.ProdServerStart  /home/sunbird/notification-service-1.0.0
