# Backend of Web application for creating and managing to-do lists

This part of the application is the backend part, which is responsible for the logic and the communication with the database.
It makes sure that the functionality of the application is matching the given requirements of the task in sheet 2 of the PE2 module.
## Prerequisites

- Java JDK 11 or higher / Application was developed with Java 21
- Maven 3.9 or higher
- IDE of your choice (ex. IntelliJ IDEA, Eclipse, ...)

## Backend Structure

The backend consists of two main components:

1. ToDo Management
2. Assignee Management

Each component includes controller, service, repository, and model classes.

## Installation and Execution

>1. Clone the repository: `git clone https://github.tik.uni-stuttgart.de/iste-pe2-2024/repo227`

>2. Navigate to the backend directory called api: `cd api`

>3. Run the matching docker container: `docker-compose up`

>4. Build the project with maven: `mvn clean install`

>5. Run the application: `mvn spring-boot:run`

Now the application is running and available at `http://localhost:8080/api/v1` in your browser.

## API REST Endpoints

### Assignee-Endpoints

- List all assignees:
>`GET /assignees`:
- Get a specific assignee by ID:
>`GET /assignees/{id}`:
- Create a new assignee:
>`POST /assignees`:
- Update an assignee:
>`PUT /assignees/{id}`:
- Delete an assignee:
>`DELETE /assignees/{id}`:


### ToDo-Endpoints
- Retrieve all ToDos:
> GET `/api/v1/todos`:
- Retrieve a specific ToDo:
> GET `/api/v1/todos/{id}`:
- Create a new ToDo:
> POST `/api/v1/todos`:
- Update an existing ToDo:
> PUT `/api/v1/todos/{id}`:
- Delete a ToDo:
> DELETE `/api/v1/todos/{id}`:


### Disclaimer

- ToDo title: 1-100 characters, not empty
- Assignee first name and last name: 1-40 characters, not empty
- Assignee email: Must end with "uni-stuttgart.de"

### Error Handling

- 400 Bad Request: Invalid input data -> appears in POST/PUT assignees and POST/PUT todos
- 404 Not Found: Resource not found -> appears in GET/PUT/DELETE assignees and GET/PUT/DELETE todos
- 500 Internal Server Error: Server error -> appears in all endpoints

