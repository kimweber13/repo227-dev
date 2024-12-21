<template>
    <!-- Container for the Home view -->
    <v-container>
        <!-- Card displaying a welcome message and current ToDo stats -->
        <v-card class="mx-auto" max-width="800">
            <!-- Title of the card with an icon -->
            <v-card-title class="text-h4 text-center">
                Welcome to the <v-icon>mdi-checkbox-marked-circle-outline</v-icon> ToDo List App
            </v-card-title>

            <!-- Card text displaying current date/time and active ToDo count -->
            <v-card-text class="text-center">
                <p class="text-h6 mb-4">
                    {{ currentDateTime }}
                </p>
                <p class="text-h6 mb-6">
                    Active ToDos: {{ activeTodoCount }}
                </p>
                <!-- Button to navigate to the ToDo list -->
                <v-btn
                    class="custom-btn"
                    @click="navigateToTodos"
                    size="large"
                >
                    Get Started
                </v-btn>
            </v-card-text>
        </v-card>
        <!-- Row displaying the version information -->
        <v-row class="mt-12">
            <v-col class="text-center text-caption">
                Version v0.4 by Kim Weber
            </v-col>
        </v-row>
    </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import config from "@/config";

const router = useRouter();
const currentDateTime = ref('');
const activeTodoCount = ref(0);

/**
 * Updates the current date and time.
 *
 * This function sets the `currentDateTime` ref to the current date and time
 * formatted as a locale string.
 */
function updateDateTime() {
    const now = new Date();
    currentDateTime.value = now.toLocaleString();
}

/**
 * Fetches the count of active ToDos from the server.
 *
 * This function sends a GET request to the server to retrieve all ToDos.
 * It then filters the ToDos to count only the active ones (not finished)
 * and updates the `activeTodoCount` ref with this count.
 * If an error occurs during the fetch, it logs the error to the console.
 */
function fetchActiveTodoCount() {
    fetch(`${config.apiBaseUrl}/todos`)
        .then(response => response.json())
        .then(data => {
            activeTodoCount.value = data.filter((todo: { finished: boolean }) => !todo.finished).length;
        })
        .catch(error => console.error('Error fetching todos:', error));
}

/**
 * Navigates to the ToDo list view.
 *
 * This function uses the router to navigate to the '/todos' route.
 */
function navigateToTodos() {
    router.push('/todos');
}

onMounted(() => {
    updateDateTime();
    setInterval(updateDateTime, 1000);
    fetchActiveTodoCount();
    setInterval(fetchActiveTodoCount, 5000);
});
</script>

<style scoped>
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
