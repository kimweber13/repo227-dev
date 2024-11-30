<script setup lang="ts">
import { Alert, Button } from "agnostic-vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import config from "@/config";
import { showToast, Toast } from "@/ts/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import { onMounted, ref } from "vue";
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
    assigneeId?: number;
}

const todos = ref<ToDo[]>([]);
const assignees = ref<Assignee[]>([]);
const router = useRouter();

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

onMounted(() => {
    fetchAllToDos();
    fetchAssignees();
});
</script>

<template>
    <h1>ToDos</h1>
    <Button @click="navigateToCreate">Create New ToDo</Button>

    <Alert v-if="todos.length === 0" type="warning">
        No ToDos available on the server...
    </Alert>

    <div v-else>
        <div class="todoBox" v-for="todo in todos" :key="todo.id">
            <h3>{{ todo.title }}</h3>
            <p>{{ todo.description }}</p>
            <p>Status: {{ todo.finished ? 'Finished' : 'Pending' }}</p>
            <p>Created Date: {{ new Date(todo.createdDate as string).toLocaleDateString() }}</p>
            <p>Due Date: {{ new Date(todo.dueDate as string).toLocaleDateString() }}</p>
            <p v-if="todo.assigneeId">Assigned to: {{ getAssigneeName(todo.assigneeId) }}</p>
            <div>
                <Button @click="deleteToDo(todo.id)" mode="secondary">
                    <FontAwesomeIcon icon="trash"/> Delete
                </Button>
                <Button @click="editToDo(todo.id)" mode="primary">
                    <FontAwesomeIcon icon="edit"/> Edit
                </Button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.todoBox{
    padding: 0.5rem;
    margin-bottom: 0.75rem;
    background-color: #4a4a4a;
    border-radius: 0.5rem;
    box-shadow: 0.25rem 0.25rem 0.75rem rgba(0, 0, 0, 0.1);
}

.todoBox h3{
    margin-top: 0;
}

.todoBox p{
    margin-bottom: 0.5rem;
}

.todoBox button{
    margin-right: 0.5rem;
}

button{
    background-color: #3498db;
    color: white;
    border-radius: 0.25rem;
    border: none;
    padding: 0.5rem 0.75rem;
    transition: background-color 0.3s ease;
    cursor: pointer;
}

button:hover{
    background-color: #2980b9;
}
</style>
