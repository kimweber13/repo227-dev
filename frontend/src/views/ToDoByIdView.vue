<template>
    <div>
        <h1>ToDo by ID</h1>
        <div class="input-container">
            <input type="number" v-model="todoId" placeholder="Enter ToDo ID" />
            <Button @click="fetchToDo" class="fetch-button">Fetch ToDo</Button>
        </div>

        <Alert v-if="error" type="error">
            {{ error }}
        </Alert>

        <div v-if="todo" class="todoBox">
            <h3>{{ todo.title }}</h3>
            <p>Description: {{ todo.description }}</p>
            <p>Status: {{ todo.finished ? 'Finished' : 'Pending' }}</p>
            <p>Created Date: {{ new Date(todo.createdDate).toLocaleDateString() }}</p>
            <p>Due Date: {{ new Date(todo.dueDate).toLocaleDateString() }}</p>
            <p v-if="todo.assigneeId">Assigned to: {{ getAssigneeName(todo.assigneeId) }}</p>
            <div class="button-group">
                <Button @click="deleteToDo" class="delete-button">
                    <FontAwesomeIcon icon="trash"/> Delete
                </Button>
                <Button @click="editToDo" class="edit-button">
                    <FontAwesomeIcon icon="edit"/> Edit
                </Button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Alert, Button } from "agnostic-vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
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
    assigneeId?: number;
}

const router = useRouter();
const todoId = ref<number | null>(null);
const todo = ref<ToDo | null>(null);
const error = ref<string | null>(null);
const assignees = ref<Assignee[]>([]);

function fetchToDo() {
    if (!todoId.value) {
        error.value = "Please enter a ToDo ID";
        return;
    }

    fetch(`${config.apiBaseUrl}/todos/${todoId.value}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("ToDo not found");
            }
            return response.json();
        })
        .then(data => {
            todo.value = data;
            error.value = null;
        })
        .catch(err => {
            error.value = err.message;
            todo.value = null;
        });
}

function deleteToDo() {
    if (!todo.value) return;

    fetch(`${config.apiBaseUrl}/todos/${todo.value.id}`, { method: "DELETE" })
        .then(() => {
            showToast(new Toast("Alert", `Successfully deleted ToDo with ID ${todo.value?.id}!`, "success", faCheck));
            todo.value = null;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

function editToDo() {
    if (todo.value) {
        router.push(`/todos/${todo.value.id}/edit`);
    }
}

function getAssigneeName(assigneeId: number): string {
    const assignee = assignees.value.find(a => a.id === assigneeId);
    return assignee ? `${assignee.prename} ${assignee.name}` : 'Unknown';
}

function fetchAssignees() {
    fetch(`${config.apiBaseUrl}/assignees`)
        .then(response => response.json())
        .then(data => {
            assignees.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

onMounted(() => {
    fetchAssignees();
});
</script>

<style scoped>
.input-container {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
}

.input-container input {
    flex-grow: 1;
    padding: 0.5rem;
    border-radius: 0.25rem;
    border: 0.1rem solid #ccc;
}

.todoBox {
    padding: 20px;
    margin-bottom: 15px;
    background-color: #4a4a4a;
    border-radius: 0.5rem;
    box-shadow: 0.25rem 0.25rem 0.75rem rgba(0, 0, 0, 0.1);
}

.todoBox h3 {
    margin-top: 0;
}

.todoBox p {
    margin-bottom: 0.5rem;
}

.button-group {
    display: flex;
    gap: 0.5rem;
}

button {
    color: white;
    border-radius: 0.25rem;
    border: none;
    padding: 0.5rem 0.75rem;
    transition: background-color 0.3s ease;
    cursor: pointer;
}

.fetch-button {
    background-color: #3498db;
}

.fetch-button:hover {
    background-color: #2980b9;
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
</style>
