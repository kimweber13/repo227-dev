<template>
    <!-- Container for the form -->
    <v-container>
        <!-- Header displaying the form title based on the editing state -->
        <h1 class="text-h4 mb-4">{{ isEditing ? 'Edit' : 'Create' }} Assignee</h1>
        <!-- Form for creating or editing an assignee -->
        <v-form @submit.prevent="submitForm">
            <!-- Input field for the first name of the assignee -->
            <v-text-field
                v-model="assignee.prename"
                label="First Name"
                required
            ></v-text-field>
            <!-- Input field for the last name of the assignee -->
            <v-text-field
                v-model="assignee.name"
                label="Last Name"
                required
            ></v-text-field>
            <!-- Input field for the email of the assignee -->
            <v-text-field
                v-model="assignee.email"
                label="Email"
                type="email"
                required
            ></v-text-field>
            <!-- Row for the form buttons -->
            <v-row align="center" class="mb-2">
                <!-- Button to navigate back to the previous page -->
                <v-btn class="custom-btn" color="primary" @click="navigateBack">Back</v-btn>
                <!-- Spacer to separate the buttons -->
                <v-spacer></v-spacer>
                <!-- Button to submit the form, displaying 'Update' or 'Create' based on the editing state -->
                <v-btn class="custom-btn" type="submit" color="primary">
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

/**
 * Fetches the assignee data from the server based on the route parameter ID.
 *
 * This function sends a GET request to the server to retrieve the assignee data.
 * On success, it updates the `assignee` ref with the fetched data.
 * On failure, it shows an error toast notification.
 */
function fetchAssignee() {
    fetch(`${config.apiBaseUrl}/assignees/${route.params.id}`)
        .then(response => response.json())
        .then(data => {
            assignee.value = data;
        })
        .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
}

/**
 * Submits the form data to create or update an assignee.
 *
 * This function determines the appropriate URL and HTTP method based on the editing state.
 * It sends a POST request to create a new assignee or a PUT request to update an existing assignee.
 * On success, it shows a success toast notification and navigates back to the assignees list.
 * On failure, it shows an error toast notification.
 */
function submitForm() {
    const url = isEditing.value
        ? `${config.apiBaseUrl}/assignees/${route.params.id}`
        : `${config.apiBaseUrl}/assignees`;
    const method = isEditing.value ? 'PUT' : 'POST';

    if (!assignee.value.prename.trim()) {
        showToast(new Toast('Error', 'First Name is required', 'error', faXmark));
        return;
    }
    if (!assignee.value.name.trim()) {
        showToast(new Toast('Error', 'Last Name is required', 'error', faXmark));
        return;
    }
    if (!assignee.value.email.trim()) {
        showToast(new Toast('Error', 'Email is required', 'error', faXmark));
        return;
    }
    if (!assignee.value.email.endsWith('@uni-stuttgart.de')) {
        showToast(new Toast('Error', 'Email must end with @uni-stuttgart.de', 'error', faXmark));
        return;
    }

    fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(assignee.value),
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || `Failed to ${isEditing.value ? 'update' : 'create'} assignee`);
                });
            }
            return response.json();
        })
        .then(() => {
            showToast(new Toast('Success', `Assignee ${isEditing.value ? 'updated' : 'created'} successfully!`, 'success', faCheck));
            router.push('/assignees');
        })
        .catch(error => {
            let errorMessage = error.message;
            if (errorMessage.includes('email')) {
                errorMessage = 'Invalid email format or email already exists';
            }
            showToast(new Toast('Error', errorMessage, 'error', faXmark));
        });
}



/**
 * Navigates back to the assignees list.
 *
 * This function uses the router to navigate back to the assignees list page.
 */
function navigateBack() {
    router.push('/assignees');
}

onMounted(() => {
    if (route.params.id) {
        isEditing.value = true;
        fetchAssignee();
    }
});
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
