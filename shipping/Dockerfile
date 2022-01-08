FROM maven:3

COPY . /shipping
WORKDIR /shipping

RUN mvn clean install -DskipTests

CMD mvn spring-boot:run
