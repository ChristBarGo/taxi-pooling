# TAXI POOLING - JAVA SPRING BOOT
---
author: Christian Barquilla Gómez<br>
status: On going.<br>
tags: Java, SpringBoot, Rest API, taxi-pooling
---
## Introduction
This application implements a **taxi pooling system in Java using Spring Boot through a Rest API**.<br>
People organized in groups request taxis in order to travel to wherever they want. Each taxi has a specific cost per km and can accommodate as many people as available seats are available.<br>
Whenever the group gets off, they must pay the total cost of the journey.

## Constraints
- Taxi seats can oscillate from 4 to 7.
- Groups are made up of 1 to 7 people.
- Groups only can request a journey once at a time.
- Each taxi can accommodate different groups if they have enough seats available.
- Groups must be served according to their arrival time. A group with a later arrival time can be served earlier than another group as long as no taxi that can serve the earlier groups.

## REST API endpoints
### GET /api/status
It is used to verify that the service has started and is ready to accept requests. 
Response:
- Returns **200 OK** when it is properly started up.

### GET /api/taxis
Returns the list of taxis that are stored in the application database.<br>

**Content type**: _application/json_<br>

Responses:
- **200 OK** when the list is returned.
- **404 Not found** when there are no cars stored in the system.
- **400 Bad Request** when the request cannot be processed or is incorrect.

Sample of 200 OK response:
```
[
    {
        "id": 1,
        "seats": 6,
        "costPerKm": 3
    },
    {
        "id": 2,
        "seats": 5,
        "costPerKm": 4.2
    }
]
    
```
### POST /api/taxis
Allows to load a list of taxis. This list replaces all previous taxis.<br>

**Content type:** _application/json_<br>
**Body:** _mandatory_ A list of taxis to be loaded.

Responses:
- **200 OK** when the list has been successfully loaded.
- **400 Bad Request** when the request cannot be processed or is incorrect.

Sample of request body:
```
[
    {
        "id": 1,
        "seats": 4,
        "costPerKm": 5.3
    },
    {
        "id": 2,
        "seats": 7,
        "costPerKm": 5.5
    }
]
```
### DELETE /api/taxis
Clears the list of taxis.

Responses:
- **204 No Content** when the list of taxis is successfully deleted or is empty.

### PATCH /api/taxis/{taxi_Id}
Updates an existing taxi properties.

**Parameters:**
- **taxi_id:** The id of the taxi to updated.<br>

**Content type:** _application/json_<br>
**Body:** _mandatory_ the property to be updated.

Responses:
- **200 OK** when the taxi is successfully updated.
- **404 Not Found** when the taxi does not exist in the system.
- **400 Bad Request** when the request cannot be processed or is incorrect.

Sample of request body:
```
 {
    "seats": 4
 }
```
### POST /api/journey
A group requests a taxi to travel.

**Content type:** _application/json_<br>
**Body:** _mandatory_ the group of people

Responses:
- **200 OK** when the journey is registered correctly.
- **400 Bad Request** when the request cannot be processed or is incorrect.

Sample of request body:
```
 {
    "id": 1,
    "people": 6
 }
```
### POST /api/dropoff
A group requests to finish the journey.

**Content type:** _application/x-www-form-urlencoded_<br>
**Body:** _mandatory_ A form including the group_id

Responses:
- **200 OK** when the group is dropped off correctly. It also returns the total cost of the trip.
- **404 Not Found** when the group does not exist in the system.
- **400 Bad Request** when the request cannot be processed or is incorrect.

### GET /api/locate/{group_id}
Returns the taxi where the group is traveling with or no taxi if they are still waiting for a taxi.

**Content type:** _application/json_<br>

- **200 OK** when car assigned to that group is returned.
- **200 No Content** when the group is still waiting.
- **404 Not Found** when the group does not exist in the system.
- **400 Bad Request** when the request cannot be processed or is incorrect.

Sample of 200 OK response:
```
  {
    "id": 2,
    "seats": 7,
    "costPerKm": 5.5
  }
```

### DELETE /api/journey
Clears all the journeys in the system, including the ongoing ones.

Responses:
- **204 No Content** when the journeys are successfully deleted.
- **404 Not Found** when there are no journeys in the system.

## How to run the application

1. Download the lastest release from the github project.
2. Open a command prompt and execute the command: _java -jar taxi-pooling.jar_
3. While the application is running, open any HTTP client tool such as Postman to make the HTTP calls to the application.

***Note: It is necessary to have JRE (Java Runtime Environment) to run this application. The application is made using Java 11.***
