<script setup lang="ts">
import { RouterView } from 'vue-router';
import { ref, computed } from 'vue';
import { saveAs } from 'file-saver';
import config from "@/config";

const isExporting = ref(false);
const exportStatus = ref('');

const showSnackbar = computed({
    get: () => !!exportStatus.value,
    set: (value) => {
        if (!value) exportStatus.value = '';
    }
});

function exportToCsv() {
    isExporting.value = true;
    exportStatus.value = 'Exporting...';
    fetch(`${config.apiBaseUrl}/todos/export`, {
        method: 'GET',
        headers: {
            'Accept': 'text/csv'
        }
    })
        .then(response => response.blob())
        .then(blob => {
            saveAs(blob, "todos_export.csv");
            exportStatus.value = 'Export successful!';
        })
        .catch(error => {
            console.error('Export failed:', error);
            exportStatus.value = 'Export failed. Please try again.';
        })
        .finally(() => {
            isExporting.value = false;
            setTimeout(() => {
                exportStatus.value = '';
            }, 3000);
        });
}
</script>

<template>
    <v-app>
        <v-app-bar app>
            <v-container class="d-flex align-center">
                <v-tabs>
                    <v-tab to="/">Home</v-tab>
                    <v-tab to="/about">About</v-tab>
                    <v-tab to="/assignees">Assignees</v-tab>
                    <v-tab to="/todos">ToDos</v-tab>
                    <v-tab to="/search-by-id">Search by ID</v-tab>
                </v-tabs>
                <v-spacer></v-spacer>
                <v-btn @click="exportToCsv" :loading="isExporting" color="primary">
                    {{ isExporting ? 'Exporting...' : 'Export ToDos' }}
                </v-btn>
            </v-container>
        </v-app-bar>

        <v-main>
            <v-container>
                <RouterView />
            </v-container>
        </v-main>

        <v-snackbar v-model="showSnackbar" :timeout="3000" color="info">
            {{ exportStatus }}
            <template v-slot:actions>
                <v-btn color="white" text @click="showSnackbar = false">
                    Close
                </v-btn>
            </template>
        </v-snackbar>
    </v-app>
</template>

<style scoped>
/* You can add any additional custom styles here if needed */
</style>
