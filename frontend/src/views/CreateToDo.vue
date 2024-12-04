<template>
    <div>
        <h1>Create ToDo</h1>
        <form @submit.prevent="submitForm">
            <div>
                <label for="title">Title:</label>
                <input type="text" v-model="todo.title" required />
            </div>
            <div>
                <label for="description">Description:</label>
                <textarea v-model="todo.description" required></textarea>
            </div>
            <div>
                <label for="dueDate">Due Date:</label>
                <input type="datetime-local" v-model="dueDateInput" required />
            </div>
            <div>
                <label>Assignees:</label>
                <div v-for="assignee in assignees" :key="assignee.id" style="display: flex; align-items: center;">
                    <input
                        type="checkbox"
                        :id="'assignee-' + assignee.id"
                        :value="assignee.id"
                        v-model="todo.assigneeIdList"
                    >
                    <label :for="'assignee-' + assignee.id" style="margin-left: 5px;">
                        {{ assignee.prename }} {{ assignee.name }}
                    </label>
                </div>
            </div>
            <button type="submit">Create</button>
        </form>
    </div>
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
</script>

<style scoped>
form {
    background-color: #4a4a4a;
    padding: 20px;
    border-radius: 0.5rem;
    box-shadow: 0.25rem 0.25rem 0.75rem rgba(0, 0, 0, 0.1);
    max-width: 600px;
    margin: auto;
}

form div {
    margin-bottom: 0.75rem;
}

form label {
    display: block;
    font-weight: bold;
    margin-bottom: 0.25rem;
}

form input[type=text],
form input[type=email],
form input[type=date],
form textarea,
form select {
    width: 100%;
    padding: 0.5rem;
    border-radius: 0.25rem;
    border: 0.1rem solid #ccc;
}

form input[type=text]:focus,
form input[type=email]:focus,
form input[type=date]:focus,
form textarea:focus,
form select:focus {
    outline: none;
    border-color: #3498db;
}

button[type=submit] {
    background-color: #3498db;
    color: white;
    border-radius: 0.25rem;
    border: none;
    padding: 0.5rem 0.75rem;
    transition: background-color 0.3s ease;
    cursor: pointer;
}

button[type=submit]:hover {
    background-color: #2980b9;
}
</style>
