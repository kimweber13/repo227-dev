<template>
    <v-container>
        <v-card class="mx-auto" max-width="800">
            <v-card-title class="text-h4 text-center">
                Welcome to the <v-icon>mdi-checkbox-marked-circle-outline</v-icon> ToDo List App
            </v-card-title>

            <v-card-text class="text-center">
                <p class="text-h6 mb-4">
                    {{ currentDateTime }}
                </p>
                <p class="text-h6 mb-6">
                    Active ToDos: {{ activeTodoCount }}
                </p>
                <v-btn
                    class="custom-btn"
                    @click="navigateToTodos"
                    size="large"
                >
                    Get Started
                </v-btn>
            </v-card-text>
        </v-card>
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

function updateDateTime() {
    const now = new Date();
    currentDateTime.value = now.toLocaleString();
}

function fetchActiveTodoCount() {
    fetch(`${config.apiBaseUrl}/todos`)
        .then(response => response.json())
        .then(data => {
            activeTodoCount.value = data.filter(todo => !todo.finished).length;
        })
        .catch(error => console.error('Error fetching todos:', error));
}

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
