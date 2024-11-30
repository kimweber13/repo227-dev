<template>
    <div>
        <h1>Assignee by ID</h1>
        <div class="input-container">
            <input type="number" v-model="assigneeId" placeholder="Enter Assignee ID" />
            <Button @click="fetchAssignee" class="fetch-button">Fetch Assignee</Button>
        </div>

        <Alert v-if="error" type="error">
            {{ error }}
        </Alert>

        <div v-if="assignee" class="assigneeBox">
            <h3>{{ assignee.prename }} {{ assignee.name }}</h3>
            <p>Email: {{ assignee.email }}</p>
            <div class="button-group">
                <Button @click="deleteAssignee" class="delete-button">
                    <FontAwesomeIcon icon="trash"/> Delete
                </Button>
                <Button @click="editAssignee" class="edit-button">
                    <FontAwesomeIcon icon="edit"/> Edit
                </Button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
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

const router = useRouter();
const assigneeId = ref<number | null>(null);
const assignee = ref<Assignee | null>(null);
const error = ref<string | null>(null);

function fetchAssignee() {
    if (!assigneeId.value) {
        error.value = "Please enter an Assignee ID";
        return;
    }

    fetch(`${config.apiBaseUrl}/assignees/${assigneeId.value}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Assignee not found");
            }
            return response.json();
        })
        .then(data => {
            assignee.value = data;
            error.value = null;
        })
        .catch(err => {
            error.value = err.message;
            assignee.value = null;
        });
}

function deleteAssignee() {
    if (!assignee.value) return;

    fetch(`${config.apiBaseUrl}/assignees/${assignee.value.id}`, { method: "DELETE" })
        .then(() => {
            showToast(new Toast("Alert", `Successfully deleted assignee with ID ${assignee.value?.id}!`, "success", faCheck));
            assignee.value = null;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

function editAssignee() {
    if (assignee.value) {
        router.push(`/assignees/${assignee.value.id}/edit`);
    }
}
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

.assigneeBox {
    padding: 20px;
    margin-bottom: 15px;
    background-color: #4a4a4a;
    border-radius: 0.5rem;
    box-shadow: 0.25rem 0.25rem 0.75rem rgba(0, 0, 0, 0.1);
}

.assigneeBox h3 {
    margin-top: 0;
}

.assigneeBox p {
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
