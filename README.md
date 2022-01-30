# bookstore 1

Team:

* Igor Stojanovic @stoji2
* Raphael Gerber @gerbr19


## Installation

The microservices and infrastructure components of the bookstore application are run in Docker containers which are linked together.

The application can be installed by conducting the following steps:

1. Compile and package the microservices using the Maven parent project   
```mvn clean package```

2. Create Docker images of the microservices using the Spring Boot plugin   
```mvn spring-boot:build-image```

3. Run the Docker containers using the Docker Compose file   
```docker-compose up```


## REST Endpoints

* [Catalog REST API](http://localhost:8001/books)
* [Customer REST API](http://localhost:8002/customers)
* [Order REST API](http://localhost:8002/orders)
* [Payment REST API](http://localhost:8003/payments)


## Email Service
We decided to use the google login data for sending emails. The setting can be en- and disabled within the properties.
For the tests no email is sent as we opted for mocking the Email Service.
Due to access to more data, we decided to place the Email Service in the Order Microservice, instead of the Shipping Microservice, as suggested by the specifications.


## Saving Books from Google
We opted for the decision to persist all books that are requested by the isbn number and not present in the database. 
The book received by google is saved in the local database to avoid unnecessary future calls to the google api. 


## Gathering Test Data

[Mockaroo](https://mockaroo.com) was used to generate some random test data.

This data was saved as csv-files. We then wrote the Python script `converter.ipynb` inside a [Jupyter](https://jupyter.org) Notebook to ensure consistency among the different data sets.

The final output csv-files as well as the Python script are provided in `order/src/test/resources/csvdata`.

To open the notebook you need to have Jupyter Notebook or JupyterLab installed on your machine.


***

## Remnants of persistence

### Install PostgreSQL databases with Docker (deprecated)

Run the following commands in a console.

**book-catalog**:
```
docker run --name postgres-book-catalog -d -p5432:5432 -e POSTGRES_DB=bookcatalog -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin postgres
```


**book-orders**:
```
docker run --name postgres-book-orders -d -p5401:5432 -e POSTGRES_DB=bookorders -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin postgres
```
