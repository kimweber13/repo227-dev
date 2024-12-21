<template>
    <!-- Container for the assignee search view -->
    <v-container>
        <!-- Header for the search form -->
        <h1 class="text-h4 mb-4">Search Assignee by ID</h1>
        <!-- Row containing the search input and button -->
        <v-row class="mb-4">
            <!-- Column for the assignee ID input field -->
            <v-col cols="12" sm="8">
                <!-- Input field for entering the assignee ID -->
                <v-text-field
                    v-model="searchId"
                    label="Enter Assignee ID"
                    type="number"
                    outlined
                    dense
                ></v-text-field>
            </v-col>
            <!-- Column for the fetch button -->
            <v-col cols="12" sm="4">
                <!-- Button to fetch the assignee details -->
                <v-btn class="custom-btn" @click="fetchAssignee" color="primary" block height="56">
                    Fetch Assignee
                </v-btn>
            </v-col>
        </v-row>

        <!-- Alert displayed when there is an error -->
        <v-alert v-if="error" type="error" class="mb-4">
            {{ error }}
        </v-alert>

        <!-- Card displaying the assignee details -->
        <v-card v-if="assignee" class="mb-4">
            <!-- Card title displaying the assignee's full name -->
            <v-card-title>{{ assignee.prename }} {{ assignee.name }}</v-card-title>
            <!-- Card text displaying the assignee's email -->
            <v-card-text>
                <p>Email: {{ assignee.email }}</p>
            </v-card-text>
            <!-- Card actions containing buttons for deleting and editing the assignee -->
            <v-card-actions>
                <!-- Button to delete the assignee -->
                <v-btn @click="deleteAssignee" color="error">
                    <v-icon left>mdi-delete</v-icon>
                    Delete
                </v-btn>
                <!-- Button to edit the assignee -->
                <v-btn @click="editAssignee" color="warning">
                    <v-icon left>mdi-pencil</v-icon>
                    Edit
                </v-btn>
            </v-card-actions>
        </v-card>
        <!-- Row containing the back button -->
        <v-row align="center" class="mb-2">
            <!-- Button to navigate back to the previous page -->
            <v-btn class="custom-btn" color="primary"  @click="navigateBack">Back</v-btn>
            <v-spacer></v-spacer>
        </v-row>
    </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import config from "@/config";
import { showToast, Toast } from "@/ts/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";

interface Assignee {
    id: number;
    prename: string;
    name: string;
    email: string;
}

const router = useRouter();
const searchId = ref<number | null>(null);
const assignee = ref<Assignee | null>(null);
const error = ref<string | null>(null);

/**
 * Fetches the assignee details based on the entered ID.
 *
 * This function sends a GET request to the server to retrieve the assignee details.
 * If the `searchId` is not provided, it sets an error message.
 * On success, it updates the `assignee` ref with the fetched data and clears any error.
 * On failure, it sets the error message and clears the `assignee` ref.
 */
function fetchAssignee() {
    if (!searchId.value) {
        error.value = "Please enter an Assignee ID";
        return;
    }

    fetch(`${config.apiBaseUrl}/assignees/${searchId.value}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Assignee not found");
            }
            return response.json();
        })
        .then(data => {
            assignee.value = data;
            error.value = null;
        })
        .catch(err => {
            error.value = err.message;
            assignee.value = null;
        });
}

/**
 * Deletes the currently fetched assignee.
 *
 * This function sends a DELETE request to the server to delete the assignee with the current ID.
 * On success, it shows a success toast notification and clears the `assignee` ref.
 * On failure, it shows an error toast notification.
 */
function deleteAssignee() {
    if (!assignee.value) return;

    fetch(`${config.apiBaseUrl}/assignees/${assignee.value.id}`, { method: "DELETE" })
        .then(() => {
            showToast(new Toast("Alert", `Successfully deleted Assignee with ID ${assignee.value?.id}!`, "success", faCheck));
            assignee.value = null;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Navigates to the edit page for the currently fetched assignee.
 *
 * This function uses the router to navigate to the edit page for the assignee with the current ID.
 */
function editAssignee() {
    if (assignee.value) {
        router.push(`/assignees/${assignee.value.id}/edit`);
    }
}

/**
 * Navigates back to the assignees list.
 *
 * This function uses the router to navigate back to the assignees list page.
 */
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
}

.custom-btn:hover {
    background-color: #daebff !important;
    color: #1976D2 !important;
}

.custom-btn::before {
    display: none;
}
</style>
