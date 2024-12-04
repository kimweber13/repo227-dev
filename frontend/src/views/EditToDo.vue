<template>
    <div>
        <h1>Edit ToDo</h1>
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
                <label for="assignees">Assignees:</label>
                <select v-model="todo.assigneeIdList" multiple>
                    <option v-for="assignee in assignees" :key="assignee.id" :value="assignee.id">
                        {{ assignee.prename }} {{ assignee.name }}
                    </option>
                </select>
            </div>
            <div>
                <label for="finished">Finished:</label>
                <input type="checkbox" v-model="todo.finished" />
            </div>
            <button type="submit">Update</button>
        </form>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast, Toast } from '@/ts/toasts';
import config from '@/config';
import { faCheck, faXmark } from '@fortawesome/free-solid-svg-icons';

interface Assignee {
    id: number;
    prename: string;
    name: string;
}

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
const todo = ref<ToDo>({ title: '', description: '', finished: false, dueDate: Date.now(), assigneeIdList: [] });
const assignees = ref<Assignee[]>([]);
const dueDateInput = ref('');

function fetchAssignees() {
    fetch(`${config.apiBaseUrl}/assignees`)
        .then(response => response.json())
        .then(data => {
            assignees.value = data;
        })
        .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
}

function fetchToDo() {
    if (route.params.id) {
        fetch(`${config.apiBaseUrl}/todos/${route.params.id}`)
            .then(response => response.json())
            .then(data => {
                todo.value = data;
                if (data.dueDate) {
                    dueDateInput.value = new Date(data.dueDate).toISOString().slice(0, -1);
                }
            })
            .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
    }
}

onMounted(() => {
    fetchAssignees();
    fetchToDo();
});

function submitForm() {
    const todoToSubmit = {
        ...todo.value,
        dueDate: new Date(dueDateInput.value).getTime()
    };

    fetch(`${config.apiBaseUrl}/todos/${route.params.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(todoToSubmit),
    })
        .then(() => {
            showToast(new Toast('Success', 'ToDo updated successfully!', 'success', faCheck));
            router.push('/todos');
        })
        .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
}
</script>

<style scoped>
form{
    background-color: #4a4a4a;
    padding: 20px;
    border-radius: 0.5rem;
    box-shadow: 0.25rem 0.25rem 0.75rem rgba(0, 0, 0, 0.1);
    max-width: 600px;
    margin: auto;
}

form div{
    margin-bottom: 0.75rem;
}

form label{
    display: block;
    font-weight: bold;
    margin-bottom: 0.25rem;
}

form input[type=text],
form input[type=email],
form input[type=date],
form textarea,
form select{
    width: 100%;
    padding: 0.5rem;
    border-radius: 0.25rem;
    border: 0.1rem solid #ccc;
}

form input[type=text]:focus,
form input[type=email]:focus,
form input[type=date]:focus,
form textarea:focus,
form select:focus{
    outline: none;
    border-color: #3498db;
}

button[type=submit]{
    background-color: #3498db;
    color: white;
    border-radius: 0.25rem;
    border: none;
    padding: 0.5rem 0.75rem;
    transition: background-color 0.3s ease;
    cursor: pointer;
}

button[type=submit]:hover{
    background-color: #2980b9;
}
</style>
