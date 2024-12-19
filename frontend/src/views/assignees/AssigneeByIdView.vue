<template>
    <v-container>
        <h1 class="text-h4 mb-4">Search Assignee by ID</h1>
        <v-row class="mb-4">
            <v-col cols="12" sm="8">
                <v-text-field
                    v-model="searchId"
                    label="Enter Assignee ID"
                    type="number"
                    outlined
                    dense
                ></v-text-field>
            </v-col>
            <v-col cols="12" sm="4">
                <v-btn class="custom-btn" @click="fetchAssignee" color="primary" block height="56">
                    Fetch Assignee
                </v-btn>
            </v-col>
        </v-row>

        <v-alert v-if="error" type="error" class="mb-4">
            {{ error }}
        </v-alert>

        <v-card v-if="assignee" class="mb-4">
            <v-card-title>{{ assignee.prename }} {{ assignee.name }}</v-card-title>
            <v-card-text>
                <p>Email: {{ assignee.email }}</p>
            </v-card-text>
            <v-card-actions>
                <v-btn @click="deleteAssignee" color="error">
                    <v-icon left>mdi-delete</v-icon>
                    Delete
                </v-btn>
                <v-btn @click="editAssignee" color="warning">
                    <v-icon left>mdi-pencil</v-icon>
                    Edit
                </v-btn>
            </v-card-actions>
        </v-card>
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

function deleteAssignee() {
    if (!assignee.value) return;

    fetch(`${config.apiBaseUrl}/assignees/${assignee.value.id}`, { method: "DELETE" })
        .then(() => {
            showToast(new Toast("Alert", `Successfully deleted Assignee with ID ${assignee.value?.id}!`, "success", faCheck));
            assignee.value = null;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

function editAssignee() {
    if (assignee.value) {
        router.push(`/assignees/${assignee.value.id}/edit`);
    }
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
