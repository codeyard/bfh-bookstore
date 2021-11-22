# bookstore 1

Team:

* Igor Stojanovic @stoji2
* Raphael Gerber @gerbr19


## Install PostgreSQL databases with Docker

Run the following commands in a console.

**book-catalog**:
```
docker run --name postgres-book-catalog -d -p5432:5432 -e POSTGRES_DB=bookcatalog -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin postgres
```


**book-orders**:
```
docker run --name postgres-book-orders -d -p5401:5432 -e POSTGRES_DB=bookorders -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin postgres
```


## Gathering Test Data

[Mockaroo](https://mockaroo.com) was used to generate some random test data.

This data was saved as csv-files. We then wrote the Python script `converter.ipynb` inside a [Jupyter](https://jupyter.org) Notebook to ensure consistency among the different data sets.

The final output csv-files as well as the Python script are provided in `book-orders/src/test/resources/csvdata`.

To open the notebook you need to have Jupyter Notebook or JupyterLab installed on your machine.

