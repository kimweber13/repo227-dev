
# Frontend of Web application for creating and managing to-do lists

This part of the application is the frontend part, which is responsible for the User Interface and the visualisation with the database.
It is working as the connection between input of the user and output of information coming from the database
The frontend directory is matching the given requirements of the task in sheet 3 of the PE2 module.

## Prerequisites

- Node.js
- web browser of your choice (ex. Chrome, Firefox, ...)

## Frontend Structure

The frontend consists different components:

1. assets  - base for visual design, like images, fonts, colours, etc.
2. router  - navigation between different parts which are displayed based on the current URL-route.
3. ts      - allows to display notifications in the application, such as success messages or error messages that automatically disappear after a certain time.
4. views   - multiple view components, which are used to display the information to the user, but also to define the methods for the different operations.

-> views are mostly separated between Assignee and ToDo components, which are responsible for the different REST operations on the assignees and the todos.

## Installation and Execution

>1. Clone the repository: `git clone https://github.tik.uni-stuttgart.de/iste-pe2-2024/repo227`

>2. Make sure the [backend](../api/README.md) is running and the database is connected

>3. Navigate to the frontend directory called frontend: `cd frontend`

>4. Install the required dependencies with: `npm install`

>5. Run the project with maven: `npm run dev`

Now the application is running and available at `http://localhost:5173/` in your browser.

## User Manual

You can navigate to the different parts of the application by clicking on the navigation bar at the top of the page.
The application offers the possibility to create, edit and delete tasks and assignees, whereby each task can be assigned to an assignee.
It is also possible to search for a specific task or assignee by id.

>Assignee operations:
- to display all assignees: click on the "Assignees" tab
- to create an assignee: click on the "Assignees" tab and then on the "Create Assignee" button, afterwards you can enter the data
- to update an assignee: click on the "Assignees" tab and then on the "Edit" button of the assignee you want to update
- to delete an assignee: click on the "Assignees" tab and then on the "Delete" button of the assignee you want to delete
- to display a specific assignee: click on the "Assignee by ID" tab and then enter the ID of the assignee you want to display

>ToDo operations:
- to display all todos: click on the "ToDos" tab
- to create a todo: click on the "ToDos" tab and then on the "Create ToDo" button, afterwards you can enter the data
- to update a todo: click on the "ToDos" tab and then on the "Edit" button of the todo you want to update
- to delete a todo: click on the "ToDos" tab and then on the "Delete" button of the todo you want to delete
- to display a specific todo: click on the "ToDo by ID" tab and then enter the ID of the todo you want to display

### Disclaimer

- ToDo title: 1-100 characters, not empty
- Assignee first name and last name: 1-40 characters, not empty
- Assignee email: Must end with "uni-stuttgart.de"

### Error Handling

- 400 Bad Request: Invalid input data -> appears in POST/PUT assignees and POST/PUT todos
- 404 Not Found: Resource not found -> appears in GET/PUT/DELETE assignees and GET/PUT/DELETE todos
- 500 Internal Server Error: Server error -> appears in all endpoints

