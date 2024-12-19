<template>
    <v-container>
        <v-card class="mx-auto" max-width="800">
            <v-card-title class="text-h4 mb-4">
                About This Project
            </v-card-title>
            <v-card-text>
                <div v-html="readmeContent" class="content"></div>
            </v-card-text>
        </v-card>
    </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { marked } from 'marked';

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
</style>
