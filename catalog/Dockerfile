FROM maven:3

COPY . /catalog
WORKDIR /catalog

RUN mvn clean install -DskipTests

CMD mvn spring-boot:run
