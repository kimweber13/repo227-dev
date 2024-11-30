<template>
    <div>
        <h1>{{ isEditing ? 'Edit Assignee' : 'Create Assignee' }}</h1>
        <form @submit.prevent="submitForm">
            <div>
                <label for="prename">Prename:</label>
                <input type="text" v-model="assignee.prename" required />
            </div>
            <div>
                <label for="name">Name:</label>
                <input type="text" v-model="assignee.name" required />
            </div>
            <div>
                <label for="email">Email:</label>
                <input type="email" v-model="assignee.email" required />
            </div>
            <button type="submit">{{ isEditing ? 'Update' : 'Create' }}</button>
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
    id?: number;
    prename: string;
    name: string;
    email: string;
}

const route = useRoute();
const router = useRouter();
const isEditing = ref(false);
const assignee = ref<Assignee>({ prename: '', name: '', email: '' });

onMounted(() => {
    if (route.params.id) {
        isEditing.value = true;
        fetch(`${config.apiBaseUrl}/assignees/${route.params.id}`)
            .then(response => response.json())
            .then(data => {
                assignee.value = data;
            })
            .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
    }
});

function submitForm() {
    const method = isEditing.value ? 'PUT' : 'POST';
    const url = isEditing.value
        ? `${config.apiBaseUrl}/assignees/${route.params.id}`
        : `${config.apiBaseUrl}/assignees`;

    fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(assignee.value),
    })
        .then(() => {
            showToast(new Toast('Success', `Assignee ${isEditing.value ? 'updated' : 'created'} successfully!`, 'success', faCheck));
            router.push('/assignees');
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

