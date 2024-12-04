<script setup lang="ts">
import { Alert, Button } from "agnostic-vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import config from "@/config";
import { showToast, Toast } from "@/ts/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import { onMounted, ref, computed } from "vue";
import { useRouter } from "vue-router";

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
    createdDate?: string;
    dueDate?: string;
    completedDate?: string;
    assigneeId?: number;
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
            return sortOrder.value === 'asc'
                ? a.title.localeCompare(b.title)
                : b.title.localeCompare(a.title);
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

function fetchAllToDos() {
    fetch(`${config.apiBaseUrl}/todos`)
        .then(response => response.json())
        .then(data => {
            todos.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

function fetchAssignees() {
    fetch(`${config.apiBaseUrl}/assignees`)
        .then(response => response.json())
        .then(data => {
            assignees.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

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

function deleteToDo(id: number) {
    fetch(`${config.apiBaseUrl}/todos/${id}`, { method: "DELETE" })
        .then(() => {
            todos.value = todos.value.filter((todo) => todo.id !== id);
            showToast(new Toast("Alert", `Successfully deleted ToDo with ID ${id}!`, "success", faCheck));
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

function editToDo(id: number) {
    router.push(`/todos/${id}/edit`);
}

function navigateToCreate() {
    router.push('/create-todo');
}

function getAssigneeName(assigneeId: number): string | undefined {
    const assignee = assignees.value.find(a => a.id === assigneeId);
    return assignee ? `${assignee.prename} ${assignee.name}` : undefined;
}

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
            <p v-if="todo.assigneeId">Assigned to: {{ getAssigneeName(todo.assigneeId) }}</p>
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
            <p v-if="todo.assigneeId">Assigned to: {{ getAssigneeName(todo.assigneeId) }}</p>
        </div>
    </div>
</template>

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
