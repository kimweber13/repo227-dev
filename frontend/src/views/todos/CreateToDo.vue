<template>
    <v-container>
        <h1 class="text-h4 mb-4">Create ToDo</h1>
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
            <v-row align="center" class="mb-2">
                <v-btn color="primary" class="custom-btn" @click="navigateBack">Back</v-btn>
                <v-spacer></v-spacer>
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

function fetchAssignees() {
    fetch(`${config.apiBaseUrl}/assignees`)
        .then(response => response.json())
        .then(data => {
            assignees.value = data;
        })
        .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
}

onMounted(() => {
    fetchAssignees();
});

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
        .then(() => {
            showToast(new Toast('Success', 'ToDo created successfully!', 'success', faCheck));
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
