# bookstore 1

## Install PostgreSQL databases with Docker

book-catalog:
```
docker run --name postgres-book-catalog -d -p5432:5432 -e POSTGRES_DB=bookcatalog -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin postgres
```


book-orders:
```
docker run --name postgres-book-orders -d -p5401:5432 -e POSTGRES_DB=bookorders -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin postgres
```
