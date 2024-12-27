<template>
    <!-- Container for the Create ToDo view -->
    <v-container>
        <!-- Header for the Create ToDo form -->
        <h1 class="text-h4 mb-4">Create ToDo</h1>
        <!-- Form for creating a new ToDo -->
        <v-form @submit.prevent="submitForm">
            <!-- Input field for the ToDo title -->
            <v-text-field
                v-model="todo.title"
                label="Title"
                required
            ></v-text-field>
            <!-- Textarea for the ToDo description -->
            <v-textarea
                v-model="todo.description"
                label="Description"
                required
            ></v-textarea>
            <!-- Input field for the ToDo due date -->
            <v-text-field
                v-model="dueDateInput"
                label="Due Date"
                type="datetime-local"
                required
            ></v-text-field>
            <!-- Card containing the list of assignees -->
            <v-card class="mb-4">
                <!-- Card title for assignees section -->
                <v-card-title>Assignees</v-card-title>
                <!-- Card text containing checkboxes for each assignee -->
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
            <!-- Row containing the back and submit buttons -->
            <v-row align="center" class="mb-2">
                <!-- Button to navigate back to the previous page -->
                <v-btn color="primary" class="custom-btn" @click="navigateBack">Back</v-btn>
                <v-spacer></v-spacer>
                <!-- Button to submit the form and create the ToDo -->
                <v-btn type="submit" color="primary" class="custom-btn">Create ToDo</v-btn>
            </v-row>
        </v-form>
    </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { showToast, Toast } from '@/ts/toasts';
import config from '@/config';
import { faCheck, faXmark } from '@fortawesome/free-solid-svg-icons';

interface Assignee {
    id: number;
    prename: string;
    name: string;
}

interface ToDo {
    title: string;
    description: string;
    dueDate: number;
    assigneeIdList: number[];
}

const router = useRouter();
const todo = ref<ToDo>({
    title: '',
    description: '',
    dueDate: 0,
    assigneeIdList: []
});
const assignees = ref<Assignee[]>([]);
const dueDateInput = ref('');

const dueDateTimestamp = computed(() => {
    return new Date(dueDateInput.value).getTime();
});

/**
 * Fetches the list of assignees from the server.
 *
 * This function sends a GET request to the server to retrieve the list of assignees.
 * On success, it updates the `assignees` ref with the fetched data.
 * On failure, it shows an error toast notification.
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
 * Lifecycle hook that is called when the component is mounted.
 *
 * This function calls `fetchAssignees` to load the list of assignees when the component is mounted.
 */
onMounted(() => {
    fetchAssignees();
});

/**
 * Submits the form data to create a new ToDo.
 *
 * This function constructs the ToDo object with the form data and sends a POST request to the server.
 * On success, it shows a success toast notification and navigates back to the ToDo list.
 * On failure, it shows an error toast notification.
 */
function submitForm() {
    const todoToSubmit = {
        ...todo.value,
        dueDate: dueDateTimestamp.value
    };

    fetch(`${config.apiBaseUrl}/todos`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(todoToSubmit),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create todo');
            }
            return response.json();
        })
        .then(() => {
            showToast(new Toast('Success', 'ToDo created successfully!', 'success', faCheck));
            router.push('/todos');
        })
        .catch(error => showToast(new Toast('Error', `Failed to create todo: ${error.message}`, 'error', faXmark)));
}


/**
 * Navigates back to the ToDo list.
 *
 * This function uses the router to navigate back to the ToDo list page.
 */
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
    margin-top: 16px !important;
}

.custom-btn:hover {
    background-color: #daebff !important;
    color: #1976D2 !important;
}

.custom-btn::before {
    display: none;
}
</style>
