# Web application for creating and managing to-do lists

This application is a REST API-based system for managing tasks (to-dos) and assignees.
The application offers the possibility to create, edit and delete tasks, whereby each task can be assigned to an assignee.
Assignees can also be created, edited and deleted. The application also offers the option of listing all tasks and assignees.

## Prerequisites

- Java JDK 11 or higher / Application was develloped with Java 21
- Maven 3.9 or higher
- IDE of your choice (ex. IntelliJ IDEA, Eclipse, ...)
- Node.js
- web browser of your choice (ex. Chrome, Firefox, ...)


## Project Structure

The project consists of two main components:

1. `Backend` - [api](api/README.md)
2. `Frontend` - [frontend](frontend/README.md)

To get more information about the structure and the functionality of the frontend and the backend, please click on the links above.

### Disclaimer

- ToDo title: 1-100 characters, not empty
- Assignee first name and last name: 1-40 characters, not empty
- Assignee email: Must end with "uni-stuttgart.de"

### Error Handling

- 400 Bad Request: Invalid input data -> appears in POST/PUT assignees and POST/PUT todos
- 404 Not Found: Resource not found -> appears in GET/PUT/DELETE assignees and GET/PUT/DELETE todos
- 500 Internal Server Error: Server error -> appears in all endpoints

