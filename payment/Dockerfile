FROM maven:3

COPY . /payment
WORKDIR /payment

RUN mvn clean install -DskipTests

CMD mvn spring-boot:run
