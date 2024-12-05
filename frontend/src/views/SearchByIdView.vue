<template>
    <div>
        <h1>Search by ID</h1>
        <div class="tab-container">
            <button @click="activeTab = 'assignee'" :class="{ active: activeTab === 'assignee' }">Assignee</button>
            <button @click="activeTab = 'todo'" :class="{ active: activeTab === 'todo' }">ToDo</button>
        </div>
        <div class="input-container">
            <input type="number" v-model="searchId" :placeholder="`Enter ${activeTab === 'assignee' ? 'Assignee' : 'ToDo'} ID`" />
            <Button @click="fetchItem" class="fetch-button">Fetch {{ activeTab === 'assignee' ? 'Assignee' : 'ToDo' }}</Button>
        </div>

        <Alert v-if="error" type="error">
            {{ error }}
        </Alert>

        <div v-if="assignee && activeTab === 'assignee'" class="itemBox">
            <h3>{{ assignee.prename }} {{ assignee.name }}</h3>
            <p>Email: {{ assignee.email }}</p>
            <div class="button-group">
                <Button @click="deleteItem" class="delete-button">
                    <FontAwesomeIcon icon="trash"/> Delete
                </Button>
                <Button @click="editItem" class="edit-button">
                    <FontAwesomeIcon icon="edit"/> Edit
                </Button>
            </div>
        </div>

        <div v-if="todo && activeTab === 'todo'" class="itemBox">
            <h3>{{ todo.title }}</h3>
            <p>Description: {{ todo.description }}</p>
            <p>Status: {{ todo.finished ? 'Finished' : 'Active' }}</p>
            <p>Created Date: {{ new Date(todo.createdDate).toLocaleDateString() }}</p>
            <p>Due Date: {{ new Date(todo.dueDate).toLocaleDateString() }}</p>
            <p v-if="todo.assigneeIdList && todo.assigneeIdList.length > 0">Assigned to: {{ getAssigneesNames(todo.assigneeIdList) }}</p>
            <div class="button-group">
                <Button @click="deleteItem" class="delete-button">
                    <FontAwesomeIcon icon="trash"/> Delete
                </Button>
                <Button @click="editItem" class="edit-button">
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
    email: string;
}

interface ToDo {
    id: number;
    title: string;
    description: string;
    finished: boolean;
    createdDate: string;
    dueDate: string;
    assigneeIdList: number[];
}

const router = useRouter();
const searchId = ref<number | null>(null);
const assignee = ref<Assignee | null>(null);
const todo = ref<ToDo | null>(null);
const error = ref<string | null>(null);
const assignees = ref<Assignee[]>([]);
const activeTab = ref<'assignee' | 'todo'>('assignee');

function fetchItem() {
    if (!searchId.value) {
        error.value = `Please enter a ${activeTab.value === 'assignee' ? 'Assignee' : 'ToDo'} ID`;
        return;
    }

    const endpoint = activeTab.value === 'assignee' ? 'assignees' : 'todos';
    fetch(`${config.apiBaseUrl}/${endpoint}/${searchId.value}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`${activeTab.value === 'assignee' ? 'Assignee' : 'ToDo'} not found`);
            }
            return response.json();
        })
        .then(data => {
            if (activeTab.value === 'assignee') {
                assignee.value = data;
                todo.value = null;
            } else {
                todo.value = data;
                assignee.value = null;
            }
            error.value = null;
        })
        .catch(err => {
            error.value = err.message;
            assignee.value = null;
            todo.value = null;
        });
}

function deleteItem() {
    const item = activeTab.value === 'assignee' ? assignee.value : todo.value;
    if (!item) return;

    const endpoint = activeTab.value === 'assignee' ? 'assignees' : 'todos';
    fetch(`${config.apiBaseUrl}/${endpoint}/${item.id}`, { method: "DELETE" })
        .then(() => {
            showToast(new Toast("Alert", `Successfully deleted ${activeTab.value} with ID ${item.id}!`, "success", faCheck));
            if (activeTab.value === 'assignee') {
                assignee.value = null;
            } else {
                todo.value = null;
            }
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

function editItem() {
    const item = activeTab.value === 'assignee' ? assignee.value : todo.value;
    if (item) {
        router.push(`/${activeTab.value}s/${item.id}/edit`);
    }
}

function getAssigneesNames(assigneeIds: number[]): string {
    return assigneeIds.map(id => {
        const assignee = assignees.value.find(a => a.id === id);
        return assignee ? `${assignee.prename} ${assignee.name}` : 'Unknown';
    }).join(', ');
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
.tab-container {
    display: flex;
    margin-bottom: 20px;
}

.tab-container button {
    flex: 1;
    padding: 10px;
    background-color: #3498db;
    color: white;
    border: none;
    cursor: pointer;
}

.tab-container button.active {
    background-color: #2980b9;
}

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

.itemBox {
    padding: 20px;
    margin-bottom: 15px;
    background-color: #4a4a4a;
    border-radius: 0.5rem;
    box-shadow: 0.25rem 0.25rem 0.75rem rgba(0, 0, 0, 0.1);
}

.itemBox h3 {
    margin-top: 0;
}

.itemBox p {
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
