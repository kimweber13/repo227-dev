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

function fetchAllAssignees() {
    fetch(`${config.apiBaseUrl}/assignees`)
        .then(response => response.json())
        .then(data => {
            assignees.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

function deleteAssignee(id: number) {
    fetch(`${config.apiBaseUrl}/assignees/${id}`, { method: "DELETE" })
        .then(() => {
            assignees.value = assignees.value.filter((assignee) => assignee.id !== id);
            showToast(new Toast("Alert", `Successfully deleted assignee with ID ${id}!`, "success", faCheck));
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

function editAssignee(id: number) {
    router.push(`/assignees/${id}/edit`);
}

function navigateToCreate() {
    router.push('/create-assignee');
}

onMounted(() => fetchAllAssignees());
</script>

<template>
    <v-container>
        <h1 class="text-h4 mb-4">Assignees</h1>
        <v-btn @click="navigateToCreate" color="primary" class="mb-4">Create New Assignee</v-btn>

        <v-alert v-if="assignees.length === 0" type="warning" class="mb-4">
            No assignees available on the server...
        </v-alert>

        <v-row v-else>
            <v-col v-for="assignee in assignees" :key="assignee.id" cols="12" sm="6" md="4">
                <v-card>
                    <v-card-title>{{ assignee.prename }} {{ assignee.name }}</v-card-title>
                    <v-card-text>
                        <p>ID: {{ assignee.id }}</p>
                        <p>Email: {{ assignee.email }}</p>
                    </v-card-text>
                    <v-card-actions>
                        <v-btn @click="deleteAssignee(assignee.id)" color="error">
                            <v-icon left>mdi-delete</v-icon>
                            Delete
                        </v-btn>
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
