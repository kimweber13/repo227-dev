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
                <label>Assignees:</label>
                <div v-for="assignee in assignees" :key="assignee.id" style="display: flex; align-items: center;">
                    <input
                        type="checkbox"
                        :id="'assignee-' + assignee.id"
                        :checked="todo.assigneeIdList.includes(assignee.id)"
                        @change="toggleAssignee(assignee.id)"
                    >
                    <label :for="'assignee-' + assignee.id" style="margin-left: 5px;">
                        {{ assignee.prename }} {{ assignee.name }}
                    </label>
                </div>
            </div>
            <div>
                <label for="finished">Finished:</label>
                <input type="checkbox" v-model="todo.finished" id="finished" />
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
    const todoToSubmit = {
        title: todo.value.title,
        description: todo.value.description,
        finished: todo.value.finished,
        assigneeIdList: todo.value.assigneeIdList,
        dueDate: new Date(dueDateInput.value).getTime()
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
form textarea,
form input[type=datetime-local] {
    width: 100%;
    padding: 0.5rem;
    border-radius: 0.25rem;
    border: 0.1rem solid #ccc;
}

form input[type=text]:focus,
form textarea:focus,
form input[type=datetime-local]:focus {
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
