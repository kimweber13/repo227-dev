<template>
    <v-container>
        <h1 class="text-h4 mb-4">Search ToDo by ID</h1>
        <v-row class="mb-4">
            <v-col cols="12" sm="8">
                <v-text-field
                    v-model="searchId"
                    label="Enter ToDo ID"
                    type="number"
                    outlined
                    dense
                ></v-text-field>
            </v-col>
            <v-col cols="12" sm="4">
                <v-btn class="custom-btn" @click="fetchToDo" color="primary" block height="56">
                    Fetch ToDo
                </v-btn>
            </v-col>
        </v-row>

        <v-alert v-if="error" type="error" class="mb-4">
            {{ error }}
        </v-alert>

        <v-card v-if="todo" class="mb-4">
            <v-card-title>
                <v-checkbox v-model="todo.finished" @change="updateToDoStatus(todo)" class="mr-2"></v-checkbox>
                {{ todo.title }}
            </v-card-title>
            <v-card-text>
                <p class="text-truncate">{{ todo.description }}</p>
                <p>ID: {{ todo.id }}</p>
                <p>Due Date: {{ new Date(todo.dueDate).toLocaleDateString() }}</p>
                <p>Assigned to: {{ getAssigneesForTodo(todo) }}</p>
                <p>Category: {{ todo.category }}</p>
            </v-card-text>
            <v-card-actions>
                <v-btn @click="editToDo" color="warning">
                    <v-icon left>mdi-pencil</v-icon>
                    Edit
                </v-btn>
                <v-btn @click="deleteToDo" color="error">
                    <v-icon left>mdi-delete</v-icon>
                    Delete
                </v-btn>
            </v-card-actions>
        </v-card>
        <v-row align="center" class="mb-2">
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

function fetchToDo() {
    if (!searchId.value) {
        error.value = "Please enter a ToDo ID";
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

function getAssigneesForTodo(todo: ToDo): string {
    if (!todo.assigneeList?.length) {
        return "No assignees";
    }
    return todo.assigneeList
        .map(assignee => `${assignee.prename} ${assignee.name}`)
        .join(", ");
}

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
    } catch (error) {
        showToast(new Toast("Error", "Failed to update todo status", "error", faXmark));
    }
}

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
