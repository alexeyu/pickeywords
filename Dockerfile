FROM openjdk:11.0.2-jre-stretch 

ENV EXIFTOOL=Image-ExifTool-11.26
WORKDIR /opt
RUN wget http://owl.phy.queensu.ca/~phil/exiftool/$EXIFTOOL.tar.gz
RUN gzip -dc $EXIFTOOL.tar.gz | tar -xf -
ENV PATH="/opt/${EXIFTOOL}:${PATH}"
ARG SHUTTERSTOCK_API_NAME
ARG SHUTTERSTOCK_API_KEY

WORKDIR photomate
RUN mkdir log

COPY photomate-app/target/photomate.jar photomate.jar
COPY photomate.properties photomate.properties

CMD ["/usr/bin/java", "-Dconfigfile=photomate.properties", "-Dlogdir=./log", "-jar", "photomate.jar"]
