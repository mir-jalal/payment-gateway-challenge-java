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

**Feel free to change the structure of the solution, use a different library etc.**

# Implementation Summary

## Design Decisions
- `PaymentGatewayServiceImpl` implements `PaymentGatewayService` and contains the main processing flow. It does validation, delegate payment processing, and persist payment results.
- `PaymentGatewayController` exposes two endpoints:
  - `POST /payment` to process a payment request
  - `GET /payment/{id}` to retrieve a payment by UUID=
- Payment Gateway Abstraction: This abstraction supports future gateway implementations without changing service logic.
- `BankGateway` is an implementation that sends the request to a bank simulation service via `RestTemplate`

## Testing
- `PaymentGatewayControllerTest` covers:
  - retrieving an existing payment returns `200 OK`
  - unknown payment IDs return validation-style error responses
  - authorized payments return `201 Created`
  - declined payments return `400 Bad Request`
