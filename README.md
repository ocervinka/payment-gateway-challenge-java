# Instructions for candidates

This is the Java version of the Payment Gateway challenge. If you haven't already read this [README.md](https://github.com/cko-recruitment/) on the details of this exercise, please do so now.

## Requirements
- JDK 17
- Docker

## Template structure

src/ - A skeleton SpringBoot Application

test/ - Some simple JUnit tests

imposters/ - contains the bank simulator configuration. Don't change this

.editorconfig - don't change this. It ensures a consistent set of rules for submissions when reformatting code

docker-compose.yml - configures the bank simulator


## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**


## Design considerations and assumptions

- Uses Spring Boot validation except for card expiration date, which is validated in code against
  current date.
- Only Payments with 200 responses from acquiring bank (either status `Authorized` or `Declined`)
  are stored.
- No retry for non-200 responses from acquiring bank.
- HTTP Status Codes
  - Payment processing - **POST** `/api/v1/payment`
    - **200 OK** when acquiring bank responds with 200 OK.
    - **400 Bad Request** if the request is invalid. 
    - **503 Service Unavailable** if acquiring bank responds with an error or times out.
      (*A better solution might be returning 200 and adding more payment statuses.*)
  - Payment retrieval - **GET** `/api/v1/payment/{id}`
    - **200 OK** when the payment is present. (the bank responded with 200)
- Focused on functional requirements and simplicity. Non-functional requirements currently not
  considered.