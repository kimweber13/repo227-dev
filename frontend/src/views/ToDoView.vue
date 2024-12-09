<template>
    <h1>ToDos</h1>
    <div class="button-container">
        <Button @click="navigateToCreate" class="create-button">Create New ToDo</Button>
    </div>
    <div class="filter-sort">
        <input v-model="titleFilter" placeholder="Filter by title" />
        <Button @click="toggleSort('title')" class="sort-button">
            Sort by Title {{ sortBy === 'title' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
        </Button>
        <Button @click="toggleSort('dueDate')" class="sort-button">
            Sort by Due Date {{ sortBy === 'dueDate' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
        </Button>
    </div>
    <h2>Active ToDos</h2>
    <Alert v-if="activeTodos.length === 0" type="warning">
        No active ToDos available or match the filter...
    </Alert>
    <div v-else class="todo-list">
        <div class="todoBox" v-for="todo in activeTodos" :key="todo.id">
            <div class="todo-header">
                <input type="checkbox" v-model="todo.finished" @change="updateToDoStatus(todo)">
                <h3>{{ todo.title }}</h3>
            </div>
            <p>ID: {{ todo.id }}</p>
            <p>{{ todo.description }}</p>
            <p>Due Date: {{ new Date(todo.dueDate as string).toLocaleDateString() }}</p>
            <p>Assigned to: {{ getAssigneesForTodo(todo) }}</p>
            <div class="button-group">
                <Button @click="editToDo(todo.id)" class="edit-button">
                    <FontAwesomeIcon icon="edit"/> Edit
                </Button>
                <Button @click="deleteToDo(todo.id)" class="delete-button">
                    <FontAwesomeIcon icon="trash"/> Delete
                </Button>
            </div>
        </div>
    </div>
    <h2>Completed ToDos</h2>
    <Alert v-if="completedTodos.length === 0" type="warning">
        No completed ToDos available or match the filter...
    </Alert>
    <div v-else class="todo-list">
        <div class="todoBox completed" v-for="todo in completedTodos" :key="todo.id">
            <div class="todo-header">
                <input type="checkbox" v-model="todo.finished" @change="updateToDoStatus(todo)" checked>
                <h3>{{ todo.title }}</h3>
            </div>
            <p>ID: {{ todo.id }}</p>
            <p>{{ todo.description }}</p>
            <p>Completed Date: {{ new Date(todo.completedDate as string).toLocaleDateString() }}</p>
            <p>Assigned to: {{ getAssigneesForTodo(todo) }}</p>
        </div>
    </div>
</template>

<script setup lang="ts">
import { Alert, Button } from "agnostic-vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import config from "@/config";
import { showToast, Toast } from "@/ts/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import { onMounted, ref, computed } from "vue";
import { useRouter } from "vue-router";

/**
 * Represents an assignee with an ID, prename, and name.
 */
interface Assignee {
    id: number;
    prename: string;
    name: string;
}

/**
 * Represents a ToDo item with various properties such as ID, title, description, status, dates, and a list of assignee IDs.
 */
interface ToDo {
    id: number;
    title: string;
    description: string;
    finished: boolean;
    createdDate?: string;
    dueDate?: string;
    completedDate?: string;
    assigneeIdList: number[];
}

const todos = ref<ToDo[]>([]);
const assignees = ref<Assignee[]>([]);
const router = useRouter();
const titleFilter = ref('');
const sortBy = ref('title');
const sortOrder = ref('asc');

const filteredAndSortedTodos = computed(() => {
    let result = todos.value.filter(todo =>
        todo.title.toLowerCase().includes(titleFilter.value.toLowerCase())
    );
    result.sort((a, b) => {
        if (sortBy.value === 'title') {
            return sortOrder.value === 'asc' ? a.title.localeCompare(b.title) : b.title.localeCompare(a.title);
        } else if (sortBy.value === 'dueDate') {
            const dateA = new Date(a.dueDate || '').getTime();
            const dateB = new Date(b.dueDate || '').getTime();
            return sortOrder.value === 'asc' ? dateA - dateB : dateB - dateA;
        }
        return 0;
    });
    return result;
});

const activeTodos = computed(() => filteredAndSortedTodos.value.filter(todo => !todo.finished));
const completedTodos = computed(() => filteredAndSortedTodos.value.filter(todo => todo.finished));

/**
 * Fetches all ToDo items from the server.
 * Updates the `todos` reference with the fetched data.
 * If the fetch operation fails, shows an error toast with the error message.
 */
function fetchAllToDos() {
    fetch(`${config.apiBaseUrl}/todos`)
        .then(response => response.json())
        .then(data => {
            todos.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Fetches the list of assignees from the server.
 * Updates the `assignees` reference with the fetched data.
 * If the fetch operation fails, shows an error toast with the error message.
 */
function fetchAssignees() {
    fetch(`${config.apiBaseUrl}/assignees`)
        .then(response => response.json())
        .then(data => {
            assignees.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Updates the status of a ToDo item on the server.
 * Sends a PUT request to update the `finished` status of the ToDo item.
 * If the ToDo is marked as finished, sets the `completedDate` to the current date.
 * If the ToDo is marked as unfinished, clears the `completedDate`.
 * Shows a success toast if the update is successful, otherwise shows an error toast.
 *
 * @param todo - The ToDo item to update.
 */
function updateToDoStatus(todo: ToDo) {
    fetch(`${config.apiBaseUrl}/todos/${todo.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ...todo, finished: todo.finished })
    })
        .then(() => {
            if (todo.finished) {
                todo.completedDate = new Date().toISOString();
            } else {
                todo.completedDate = undefined;
            }
            showToast(new Toast("Success", `ToDo status updated!`, "success", faCheck));
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Deletes a ToDo item from the server.
 * Sends a DELETE request to remove the ToDo item with the specified ID.
 * If the deletion is successful, updates the `todos` reference to remove the deleted item.
 * Shows a success toast if the deletion is successful, otherwise shows an error toast.
 *
 * @param id - The ID of the ToDo item to delete.
 */
function deleteToDo(id: number) {
    fetch(`${config.apiBaseUrl}/todos/${id}`, { method: "DELETE" })
        .then(() => {
            todos.value = todos.value.filter((todo) => todo.id !== id);
            showToast(new Toast("Alert", `Successfully deleted ToDo with ID ${id}!`, "success", faCheck));
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Navigates to the edit page for the ToDo item with the specified ID.
 *
 * @param id - The ID of the ToDo item to edit.
 */
function editToDo(id: number) {
    router.push(`/todos/${id}/edit`);
}

/**
 * Navigates to the create page for a new ToDo item.
 */
function navigateToCreate() {
    router.push('/create-todo');
}

/**
 * Retrieves the names of the assignees for a given ToDo item.
 * If the ToDo item has no assignees, returns "No assignees".
 * Otherwise, maps the assignee IDs to their corresponding names and joins them into a single string.
 *
 * @param todo - The ToDo item for which to get the assignees.
 * @returns    - A string containing the names of the assignees, or "No assignees" if there are none.
 */
function getAssigneesForTodo(todo: ToDo): string {
    if (!todo.assigneeIdList || todo.assigneeIdList.length === 0) {
        return "No assignees";
    }

    return todo.assigneeIdList
        .map(id => {
            const assignee = assignees.value.find(a => a.id === id);
            return assignee ? `${assignee.prename} ${assignee.name}` : '';
        })
        .filter(name => name !== '')
        .join(', ');
}

/**
 * Toggles the sorting order for the specified field.
 * If the current sort field is the same as the specified field, it toggles the sort order between ascending and descending.
 * If the current sort field is different from the specified field, it sets the sort field to the specified field and the sort order to ascending.
 *
 * @param field - The field by which to sort the ToDo items.
 */
function toggleSort(field: 'title' | 'dueDate') {
    if (sortBy.value === field) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
    } else {
        sortBy.value = field;
        sortOrder.value = 'asc';
    }
}

onMounted(async () => {
    await fetchAssignees();
    fetchAllToDos();
});
</script>

<style scoped>
.button-container {
    margin-bottom: 20px;
}

.todo-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.todoBox {
    padding: 20px;
    background-color: #4a4a4a;
    border-radius: 0.5rem;
    box-shadow: 0.25rem 0.25rem 0.75rem rgba(0, 0, 0, 0.1);
}

.todoBox.completed {
    background-color: #3a3a3a;
    opacity: 0.8;
}

.todo-header {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}

.todo-header input[type="checkbox"] {
    margin-right: 10px;
}

.todo-header h3 {
    margin: 0;
}

.todoBox p {
    margin-bottom: 0.5rem;
}

.button-group {
    display: flex;
    gap: 0.5rem;
    margin-top: 0.5rem;
}

button {
    color: white;
    border-radius: 0.25rem;
    border: none;
    padding: 0.5rem 0.75rem;
    transition: background-color 0.3s ease;
    cursor: pointer;
}

.create-button {
    background-color: #2ecc71;
}

.create-button:hover {
    background-color: #27ae60;
}

.delete-button {
    background-color: #e74c3c;
}

.delete-button:hover {
    background-color: #c0392b;
}

.edit-button {
    background-color: #f1c40f;
}

.edit-button:hover {
    background-color: #f39c12;
}

.sort-button {
    background-color: #3498db;
}

.sort-button:hover {
    background-color: #2980b9;
}

.filter-sort {
    margin-bottom: 1rem;
    display: flex;
    gap: 0.5rem;
}

.filter-sort input {
    flex-grow: 1;
    padding: 0.5rem;
    border-radius: 0.25rem;
    border: 1px solid #ccc;
}
</style>
