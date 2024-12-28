<template>
    <!-- Container for the assignees view -->
    <v-container>
        <!-- Row containing buttons for creating and searching assignees -->
        <v-row align="center" class="mb-4">
            <!-- Column for the 'Create Assignee' button -->
            <v-col cols="12" sm="3" class="text-right">
                <!-- Button to navigate to the create assignee page -->
                <v-btn class="custom-btn" color="primary" @click="navigateToCreate" block>
                    <v-icon left>mdi-account-plus</v-icon> Create Assignee
                </v-btn>
            </v-col>
            <!-- Spacer to separate the buttons -->
            <v-spacer></v-spacer>
            <!-- Column for the 'Search Assignee' button -->
            <v-col cols="12" sm="3">
                <!-- Button to navigate to the search assignee page -->
                <v-btn class="custom-btn" color="primary" @click="navigateToSearch" block>
                    <v-icon left>mdi-magnify</v-icon> Search Assignee
                </v-btn>
            </v-col>
        </v-row>
        <!-- Alert displayed when no assignees are available -->
        <v-alert v-if="assignees.length === 0" type="warning" class="mb-4">
            No assignees available on the server...
        </v-alert>
        <!-- Row containing cards for each assignee -->
        <v-row v-else>
            <!-- Column for each assignee card -->
            <v-col v-for="assignee in assignees" :key="assignee.id" cols="12" sm="6" md="6">
                <!-- Card displaying assignee details -->
                <v-card>
                    <!-- Card title displaying assignee's full name -->
                    <v-card-title>{{ assignee.prename }} {{ assignee.name }}</v-card-title>
                    <!-- Card text displaying assignee's ID and email -->
                    <v-card-text>
                        <p>ID: {{ assignee.id }}</p>
                        <p>Email: {{ assignee.email }}</p>
                    </v-card-text>
                    <!-- Card actions containing buttons for deleting and editing the assignee -->
                    <v-card-actions>
                        <!-- Button to delete the assignee -->
                        <v-btn @click="deleteAssignee(assignee.id)" color="error">
                            <v-icon left>mdi-delete</v-icon>
                            Delete
                        </v-btn>
                        <!-- Button to edit the assignee -->
                        <v-btn @click="editAssignee(assignee.id)" color="warning">
                            <v-icon left>mdi-pencil</v-icon>
                            Edit
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script setup lang="ts">
import config from "@/config";
import { showToast, Toast } from "@/ts/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

interface Assignee {
    id: number;
    prename: string;
    name: string;
    email: string;
}

const assignees = ref<Assignee[]>([]);
const router = useRouter();

/**
 * Fetches all assignees from the server.
 *
 * This function sends a GET request to the server to retrieve all assignees.
 * On success, it updates the `assignees` ref with the fetched data.
 * On failure, it shows an error toast notification.
 */
function fetchAllAssignees() {
    fetch(`${config.apiBaseUrl}/assignees`)
        .then(response => response.json())
        .then(data => {
            assignees.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Deletes an assignee by ID.
 *
 * This function sends a DELETE request to the server to delete the assignee with the specified ID.
 * On success, it removes the assignee from the `assignees` ref and shows a success toast notification.
 * On failure, it shows an error toast notification.
 *
 * @param id - The ID of the assignee to delete
 */
function deleteAssignee(id: number) {
    fetch(`${config.apiBaseUrl}/assignees/${id}`, { method: "DELETE" })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete assignee');
            }
            assignees.value = assignees.value.filter((assignee) => assignee.id !== id);
            showToast(new Toast("Success", `Successfully deleted assignee with ID ${id}!`, "success", faCheck));
        })
        .catch(error => showToast(new Toast("Error", `Failed to delete assignee: ${error.message}`, "error", faXmark)));
}

/**
 * Navigates to the edit page for the specified assignee.
 *
 * This function uses the router to navigate to the edit page for the assignee with the specified ID.
 *
 * @param id - The ID of the assignee to edit
 */
function editAssignee(id: number) {
    router.push(`/assignees/${id}/edit`);
}

/**
 * Navigates to the create assignee page.
 *
 * This function uses the router to navigate to the create assignee page.
 */
function navigateToCreate() {
    router.push('/create-assignee');
}

/**
 * Navigates to the search assignee page.
 *
 * This function uses the router to navigate to the search assignee page.
 */
function navigateToSearch() {
    router.push('/assignee-by-id');
}


onMounted(() => fetchAllAssignees());

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
