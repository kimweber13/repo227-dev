<template>
    <!-- Container for the Export ToDos view -->
    <div class="export-container">
        <!-- Header for the Export ToDos section -->
        <h1>Export ToDos</h1>
        <!-- Button to trigger the export to CSV function -->
        <Button @click="exportToCsv" class="export-button" :disabled="isExporting">
            <!-- Button text changes based on the exporting state -->
            {{ isExporting ? 'Exporting...' : 'Export ToDos as CSV' }}
        </Button>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { Button } from "agnostic-vue";
import config from "@/config";
import { showToast, Toast } from "@/ts/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import { saveAs } from 'file-saver';

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
const isExporting = ref(false);

/**
 * Fetches all ToDos from the server.
 *
 * This function sends a GET request to the server to retrieve all ToDos.
 * On success, it updates the `todos` ref with the fetched data.
 * On failure, it shows an error toast notification.
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
 * Exports all ToDos to a CSV file.
 *
 * This function sends a GET request to the server to export all ToDos as a CSV file.
 * If there are no ToDos available to export, it shows a warning toast notification.
 * On success, it triggers a download of the CSV file and shows a success toast notification.
 * On failure, it shows an error toast notification.
 * The exporting state is managed using the `isExporting` ref.
 */
function exportToCsv() {
    if (todos.value.length === 0) {
        showToast(new Toast("Warning", "No ToDos available to export", "warning", faXmark));
        return;
    }

    isExporting.value = true;
    fetch(`${config.apiBaseUrl}/todos/export`, {
        method: 'GET',
        headers: {
            'Accept': 'text/csv'
        }
    })
        .then(response => response.blob())
        .then(blob => {
            saveAs(blob, "todos_export.csv");
            showToast(new Toast("Success", "ToDos exported successfully!", "success", faCheck));
        })
        .catch(error => {
            showToast(new Toast("Error", "Failed to export ToDos", "error", faXmark));
        })
        .finally(() => {
            isExporting.value = false;
        });
}

onMounted(() => {
    fetchAllToDos();
});
</script>

<style scoped>
.export-container {
    margin: 20px;
}

.export-button {
    background-color: #3498db;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.export-button:hover {
    background-color: #2980b9;
}

.export-button:disabled {
    background-color: #95a5a6;
    cursor: not-allowed;
}
</style>
