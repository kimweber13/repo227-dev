<template>
    <v-container>
        <h1 class="text-h4 mb-4">Edit ToDo</h1>
        <v-form @submit.prevent="submitForm">
            <v-text-field
                v-model="todo.title"
                label="Title"
                required
            ></v-text-field>
            <v-textarea
                v-model="todo.description"
                label="Description"
                required
            ></v-textarea>
            <v-text-field
                v-model="dueDateInput"
                label="Due Date"
                type="datetime-local"
                required
            ></v-text-field>
            <v-card class="mb-4">
                <v-card-title>Assignees</v-card-title>
                <v-card-text>
                    <v-checkbox
                        v-for="assignee in assignees"
                        :key="assignee.id"
                        v-model="todo.assigneeIdList"
                        :label="`${assignee.prename} ${assignee.name}`"
                        :value="assignee.id"
                    ></v-checkbox>
                </v-card-text>
            </v-card>
            <v-checkbox
                v-model="todo.finished"
                label="Finished"
            ></v-checkbox>
            <v-row align="center" class="mb-2">
                <v-btn color="primary" class="custom-btn" @click="navigateBack">Back</v-btn>
                <v-spacer></v-spacer>
                <v-btn type="submit" color="primary" class="custom-btn">Update ToDo</v-btn>
            </v-row>
        </v-form>
    </v-container>
</template>


<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast, Toast } from '@/ts/toasts';
import config from '@/config';
import { faCheck, faXmark } from '@fortawesome/free-solid-svg-icons';

/**
 * Represents an assignee with an ID, prename, and name.
 */
interface Assignee {
    id: number;
    prename: string;
    name: string;
}

/**
 * Represents a ToDo item with optional ID, title, description, finished status, due date, and a list of assignee IDs.
 */
interface ToDo {
    id?: number;
    title: string;
    description: string;
    finished?: boolean;
    dueDate?: number; // Unix timestamp
    assigneeIdList: number[];
}

const route = useRoute();
const router = useRouter();
const todo = ref<ToDo>({
    title: '',
    description: '',
    finished: false,
    dueDate: Date.now(),
    assigneeIdList: []
});
const assignees = ref<Assignee[]>([]);
const dueDateInput = ref('');

/**
 * Fetches the list of assignees from the server.
 * Updates the `assignees` reference with the fetched data.
 * Displays an error toast if the fetch operation fails.
 */
function fetchAssignees() {
    fetch(`${config.apiBaseUrl}/assignees`)
        .then(response => response.json())
        .then(data => {
            assignees.value = data;
        })
        .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
}

/**
 * Fetches the ToDo item from the server based on the route parameter ID.
 * Updates the `todo` reference with the fetched data.
 * Sets the `dueDateInput` value if the ToDo item has a due date.
 * Displays an error toast if the fetch operation fails.
 */
function fetchToDo() {
    if (route.params.id) {
        fetch(`${config.apiBaseUrl}/todos/${route.params.id}`)
            .then(response => response.json())
            .then(data => {
                todo.value = {
                    title: data.title,
                    description: data.description,
                    finished: data.finished || false,
                    dueDate: data.dueDate || Date.now(),
                    assigneeIdList: data.assigneeIdList || []
                };
                if (data.dueDate) {
                    // Korrigierte Zeitzonenbehandlung
                    const date = new Date(data.dueDate);
                    const offset = date.getTimezoneOffset() * 60000;
                    const localDate = new Date(date.getTime() - offset);
                    dueDateInput.value = localDate.toISOString().slice(0, -1);
                }
            })
            .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
    }
}

onMounted(() => {
    fetchAssignees();
    fetchToDo();
});

/**
 * Toggles the inclusion of an assignee in the ToDo item's assignee list.
 * If the assignee is not in the list, they are added.
 * If the assignee is already in the list, they are removed.
 *
 * @param assigneeId - The ID of the assignee to toggle.
 */
function toggleAssignee(assigneeId: number) {
    const index = todo.value.assigneeIdList.indexOf(assigneeId);
    if (index === -1) {
        todo.value.assigneeIdList.push(assigneeId);
    } else {
        todo.value.assigneeIdList.splice(index, 1);
    }
}

/**
 * Submits the form to update an existing ToDo item.
 * Constructs the ToDo object with the current form values and due date timestamp.
 * Sends a PUT request to the server with the ToDo data.
 * Displays a success toast message and navigates to the ToDo list on success.
 * Displays an error toast message if the request fails.
 */
function submitForm() {
    // Korrigierte Zeitzonenbehandlung beim Submit
    const inputDate = new Date(dueDateInput.value);
    const offset = inputDate.getTimezoneOffset() * 60000;

    const todoToSubmit = {
        title: todo.value.title,
        description: todo.value.description,
        finished: todo.value.finished,
        assigneeIdList: todo.value.assigneeIdList,
        dueDate: inputDate.getTime() + offset // Korrektur der Zeitzone
    };

    fetch(`${config.apiBaseUrl}/todos/${route.params.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(todoToSubmit),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            showToast(new Toast('Success', 'ToDo updated successfully!', 'success', faCheck));
            router.push('/todos');
        })
        .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
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
