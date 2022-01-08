FROM maven:3

WORKDIR /order
COPY . .
RUN mvn clean install

CMD mvn spring-boot:run
