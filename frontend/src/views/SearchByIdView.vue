<template>
    <v-container>
        <h1 class="text-h4 mb-4">Search by ID</h1>
        <v-tabs v-model="activeTab" class="mb-4">
            <v-tab value="assignee">Assignee</v-tab>
            <v-tab value="todo">ToDo</v-tab>
        </v-tabs>
        <v-row class="mb-4">
            <v-col cols="12" sm="8">
                <v-text-field
                    v-model="searchId"
                    :label="`Enter ${activeTab === 'assignee' ? 'Assignee' : 'ToDo'} ID`"
                    type="number"
                    outlined
                    dense
                ></v-text-field>
            </v-col>
            <v-col cols="12" sm="4">
                <v-btn @click="fetchItem" color="primary" block height="56">
                    Fetch {{ activeTab === 'assignee' ? 'Assignee' : 'ToDo' }}
                </v-btn>
            </v-col>
        </v-row>

        <v-alert v-if="error" type="error" class="mb-4">
            {{ error }}
        </v-alert>

        <v-card v-if="assignee && activeTab === 'assignee'" class="mb-4">
            <v-card-title>{{ assignee.prename }} {{ assignee.name }}</v-card-title>
            <v-card-text>
                <p>Email: {{ assignee.email }}</p>
            </v-card-text>
            <v-card-actions>
                <v-btn @click="deleteItem" color="error">
                    <v-icon left>mdi-delete</v-icon>
                    Delete
                </v-btn>
                <v-btn @click="editItem" color="warning">
                    <v-icon left>mdi-pencil</v-icon>
                    Edit
                </v-btn>
            </v-card-actions>
        </v-card>

        <v-card v-if="todo && activeTab === 'todo'" class="mb-4">
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
                <v-btn @click="editItem" color="warning">
                    <v-icon left>mdi-pencil</v-icon>
                    Edit
                </v-btn>
                <v-btn @click="deleteItem" color="error">
                    <v-icon left>mdi-delete</v-icon>
                    Delete
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
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
    completedDate: string | null | undefined;
    assigneeList: Assignee[];
    assigneeIdList: number[];
    category: string;
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
.text-truncate {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
</style>
