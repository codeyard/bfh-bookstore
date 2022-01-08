FROM maven:3

WORKDIR /catalog
COPY . .
RUN mvn clean install

CMD mvn spring-boot:run
