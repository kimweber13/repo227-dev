<template>
    <v-container>
        <v-card class="mx-auto" max-width="1000">
            <v-card-title class="text-h4 text-center">
                Little break from the <v-icon>mdi-checkbox-marked-circle-outline</v-icon> ToDo List App
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
                    Go back to work
                </v-btn>
            </v-card-text>
        </v-card>

        <v-card class="mx-auto mt-8" max-width="1000">
            <v-card-title class="text-h4 mb-4 px-10">
                About This Project
            </v-card-title>
            <v-card-text class="px-10">
                <div v-html="readmeContent" class="content"></div>
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
import { marked } from 'marked';

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

const readmeContent = ref('');

onMounted(async () => {
    try {
        const response = await fetch('/README.md');
        const text = await response.text();
        readmeContent.value = await marked(text);
    } catch (error) {
        console.error('Error loading README.md:', error);
        readmeContent.value = '<p>Error loading README.md content.</p>';
    }
    updateDateTime();
    setInterval(updateDateTime, 1000);
    fetchActiveTodoCount();
    setInterval(fetchActiveTodoCount, 5000);
});
</script>

<style scoped>
.content {
    font-size: 1.1rem;
    line-height: 1.6;
}

.content p {
    margin-bottom: 15px;
}

.content h2,
.content h3 {
    color: #1976D2;
    margin-top: 20px;
    margin-bottom: 10px;
}

.content pre {
    background-color: #F5F5F5;
    padding: 16px;
    border-radius: 4px;
    margin: 16px 0;
}

.content code {
    background-color: #F5F5F5;
    padding: 2px 4px;
    border-radius: 4px;
    font-family: monospace;
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
</style>
