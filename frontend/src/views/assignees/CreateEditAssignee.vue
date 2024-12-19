<template>
    <v-container>
        <h1 class="text-h4 mb-4">{{ isEditing ? 'Edit' : 'Create' }} Assignee</h1>
        <v-form @submit.prevent="submitForm">
            <v-text-field
                v-model="assignee.prename"
                label="First Name"
                required
            ></v-text-field>
            <v-text-field
                v-model="assignee.name"
                label="Last Name"
                required
            ></v-text-field>
            <v-text-field
                v-model="assignee.email"
                label="Email"
                type="email"
                required
            ></v-text-field>
            <v-row align="center" class="mb-2">
                <v-btn class="custom-btn" color="primary"  @click="navigateBack">Back</v-btn>
                <v-spacer></v-spacer>
                <v-btn class="custom-btn" type="submit" color="primary" >
                    {{ isEditing ? 'Update' : 'Create' }} Assignee
                </v-btn>
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

interface Assignee {
    id?: number;
    prename: string;
    name: string;
    email: string;
}

const route = useRoute();
const router = useRouter();
const assignee = ref<Assignee>({
    prename: '',
    name: '',
    email: ''
});
const isEditing = ref(false);

onMounted(() => {
    if (route.params.id) {
        isEditing.value = true;
        fetchAssignee();
    }
});

function fetchAssignee() {
    fetch(`${config.apiBaseUrl}/assignees/${route.params.id}`)
        .then(response => response.json())
        .then(data => {
            assignee.value = data;
        })
        .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
}

function submitForm() {
    const url = isEditing.value
        ? `${config.apiBaseUrl}/assignees/${route.params.id}`
        : `${config.apiBaseUrl}/assignees`;
    const method = isEditing.value ? 'PUT' : 'POST';

    fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(assignee.value),
    })
        .then(response => response.json())
        .then(() => {
            showToast(new Toast('Success', `Assignee ${isEditing.value ? 'updated' : 'created'} successfully!`, 'success', faCheck));
            router.push('/assignees');
        })
        .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
}

function navigateBack() {
    router.push('/assignees');
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
    margin-top: 18px !important;
}

.custom-btn:hover {
    background-color: #daebff !important;
    color: #1976D2 !important;
}

.custom-btn::before {
    display: none;
}
</style>
