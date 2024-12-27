<template>
    <!-- Container for the Search ToDo by ID view -->
    <v-container>
        <!-- Header for the Search ToDo form -->
        <h1 class="text-h4 mb-4">Search ToDo by ID</h1>
        <!-- Row containing the input field and fetch button -->
        <v-row class="mb-4">
            <!-- Column for the ToDo ID input field -->
            <v-col cols="12" sm="8">
                <!-- Input field for entering the ToDo ID -->
                <v-text-field
                    v-model="searchId"
                    label="Enter ToDo ID"
                    type="number"
                    outlined
                    dense
                ></v-text-field>
            </v-col>
            <!-- Column for the fetch button -->
            <v-col cols="12" sm="4">
                <!-- Button to fetch the ToDo details -->
                <v-btn class="custom-btn" @click="fetchToDo" color="primary" block height="56">
                    Fetch ToDo
                </v-btn>
            </v-col>
        </v-row>

        <!-- Alert to display error messages -->
        <v-alert v-if="error" type="error" class="mb-4">
            {{ error }}
        </v-alert>

        <!-- Card to display the fetched ToDo details -->
        <v-card v-if="todo" class="mb-4">
            <!-- Card title with a checkbox to mark the ToDo as finished -->
            <v-card-title>
                <v-checkbox v-model="todo.finished" @change="updateToDoStatus(todo)" class="mr-2"></v-checkbox>
                {{ todo.title }}
            </v-card-title>
            <!-- Card text with ToDo details -->
            <v-card-text>
                <p class="text-truncate">{{ todo.description }}</p>
                <p>ID: {{ todo.id }}</p>
                <p>Due Date: {{ new Date(todo.dueDate).toLocaleDateString() }}</p>
                <p>Assigned to: {{ getAssigneesForTodo(todo) }}</p>
                <p>Category: {{ todo.category }}</p>
            </v-card-text>
            <!-- Card actions with edit and delete buttons -->
            <v-card-actions>
                <!-- Button to navigate to the edit page -->
                <v-btn @click="editToDo" color="warning">
                    <v-icon left>mdi-pencil</v-icon>
                    Edit
                </v-btn>
                <!-- Button to delete the ToDo -->
                <v-btn @click="deleteToDo" color="error">
                    <v-icon left>mdi-delete</v-icon>
                    Delete
                </v-btn>
            </v-card-actions>
        </v-card>
        <!-- Row containing the back button -->
        <v-row align="center" class="mb-2">
            <!-- Button to navigate back to the previous page -->
            <v-btn color="primary" class="custom-btn" @click="navigateBack">Back</v-btn>
            <v-spacer></v-spacer>
        </v-row>
    </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import config from "@/config";
import { showToast, Toast } from "@/ts/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";

interface Assignee {
    id: number;
    prename: string;
    name: string;
}

interface ToDo {
    id: number;
    title: string;
    description: string;
    finished: boolean;
    createdDate: string;
    dueDate: string;
    completedDate: string | null | undefined;
    assigneeList: Assignee[];
    assigneeIdList: number[];
    category: string;
}

const router = useRouter();
const searchId = ref<number | null>(null);
const todo = ref<ToDo | null>(null);
const error = ref<string | null>(null);

/**
 * Fetches the ToDo details based on the entered ID.
 *
 * This function sends a GET request to the server to retrieve the ToDo details.
 * If the `searchId` is not provided, it sets an error message.
 * On success, it updates the `todo` ref with the fetched data and clears any error.
 * On failure, it sets the error message and clears the `todo` ref.
 */
function fetchToDo() {
    if (!searchId.value) {
        error.value = "Please enter a ToDo ID";
        showToast(new Toast("Error", "Please enter a ToDo ID", "error", faXmark));
        return;
    }

    fetch(`${config.apiBaseUrl}/todos/${searchId.value}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("ToDo not found");
            }
            return response.json();
        })
        .then(data => {
            todo.value = data;
            error.value = null;
            showToast(new Toast("Success", "ToDo found successfully!", "success", faCheck));
        })
        .catch(err => {
            error.value = err.message;
            todo.value = null;
            showToast(new Toast("Error", `Failed to fetch todo: ${err.message}`, "error", faXmark));
        });
}


/**
 * Deletes the currently fetched ToDo.
 *
 * This function sends a DELETE request to the server to delete the ToDo with the current ID.
 * On success, it shows a success toast notification and clears the `todo` ref.
 * On failure, it shows an error toast notification.
 */
function deleteToDo() {
    if (!todo.value) return;

    fetch(`${config.apiBaseUrl}/todos/${todo.value.id}`, { method: "DELETE" })
        .then(() => {
            showToast(new Toast("Alert", `Successfully deleted ToDo with ID ${todo.value?.id}!`, "success", faCheck));
            todo.value = null;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Navigates to the edit page for the currently fetched ToDo.
 *
 * This function uses the router to navigate to the edit page for the ToDo with the current ID.
 */
function editToDo() {
    if (todo.value) {
        router.push(`/todos/${todo.value.id}/edit`);
    }
}

/**
 * Returns a string of assignees for the given ToDo.
 *
 * This function takes a ToDo object and returns a string of assignees' names.
 * If there are no assignees, it returns "No assignees".
 *
 * @param {ToDo} todo - The ToDo object.
 * @returns {string} - A string of assignees' names or "No assignees".
 */
function getAssigneesForTodo(todo: ToDo): string {
    if (!todo.assigneeList?.length) {
        return "No assignees";
    }
    return todo.assigneeList
        .map(assignee => `${assignee.prename} ${assignee.name}`)
        .join(", ");
}

/**
 * Updates the status of the given ToDo.
 *
 * This function sends a PUT request to the server to update the status of the ToDo.
 * On success, it updates the `completedDate` of the ToDo.
 * On failure, it shows an error toast notification.
 *
 * @param {ToDo} todo - The ToDo object to update.
 */
async function updateToDoStatus(todo: ToDo) {
    const updateData = {
        id: todo.id,
        title: todo.title,
        description: todo.description,
        finished: todo.finished,
        completedDate: todo.finished ? new Date().toISOString() : null,
        assigneeIdList: todo.assigneeList.map(assignee => assignee.id),
        assigneeList: todo.assigneeList,
        dueDate: todo.dueDate,
        category: todo.category
    };

    try {
        const response = await fetch(`${config.apiBaseUrl}/todos/${todo.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updateData),
        });
        if (!response.ok) {
            throw new Error('Failed to update todo status');
        }
        todo.completedDate = updateData.completedDate;
        showToast(new Toast("Success", `ToDo status successfully ${todo.finished ? 'completed' : 'reactivated'}!`, "success", faCheck));
    } catch (error) {
        showToast(new Toast("Error", "Failed to update todo status", "error", faXmark));
    }
}

/**
 * Navigates back to the ToDo list.
 *
 * This function uses the router to navigate back to the ToDo list page.
 */
function navigateBack() {
    router.push('/todos');
}
</script>

<style scoped>
.text-truncate {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.custom-btn {
    background-color: white !important;
    color: #1976D2 !important;
    box-shadow: none !important;
    border: none !important;
}

.custom-btn:hover {
    background-color: #daebff !important;
    color: #1976D2 !important;
}

.custom-btn::before {
    display: none;
}
</style>
