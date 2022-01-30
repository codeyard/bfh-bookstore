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


## Gathering Test Data

[Mockaroo](https://mockaroo.com) was used to generate some random test data.

This data was saved as csv-files. We then wrote the Python script `converter.ipynb` inside a [Jupyter](https://jupyter.org) Notebook to ensure consistency among the different data sets.

The final output csv-files as well as the Python script are provided in `order/src/test/resources/csvdata`.

To open the notebook you need to have Jupyter Notebook or JupyterLab installed on your machine.

