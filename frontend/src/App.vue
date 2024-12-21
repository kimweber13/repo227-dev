<script setup lang="ts">
import { RouterView } from 'vue-router';
import { ref, computed } from 'vue';
import { saveAs } from 'file-saver';
import config from "@/config";
import { mdiCheckCircle, mdiAlertCircle, mdiHome } from '@mdi/js';

const isExporting = ref(false);
const exportStatus = ref('');
const snackbarIcon = ref('');

const showSnackbar = computed({
    get: () => !!exportStatus.value,
    set: (value) => {
        if (!value) exportStatus.value = '';
    }
});

function exportToCsv() {
    isExporting.value = true;
    exportStatus.value = 'Exporting...';
    fetch(`${config.apiBaseUrl}/csv-downloads/todos`, {
        method: 'GET',
        headers: {
            'Accept': 'text/csv'
        }
    })
        .then(response => response.blob())
        .then(blob => {
            saveAs(blob, "todos_export.csv");
            exportStatus.value = 'Export successful!';
            snackbarIcon.value = mdiCheckCircle;
        })
        .catch(error => {
            console.error('Export failed:', error);
            exportStatus.value = 'Export failed. Please try again.';
            snackbarIcon.value = mdiAlertCircle;
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
                <v-btn to="/about" class="custom-btn">
                    <v-icon>mdi-checkbox-marked-circle-outline</v-icon>ToDo List
                </v-btn>
                <v-spacer></v-spacer>
                <v-tabs>
                    <v-tab to="/todos">ToDos</v-tab>
                    <v-tab to="/assignees">Assignees</v-tab>
                </v-tabs>
                <v-spacer></v-spacer>
                <v-btn class="custom-btn" @click="exportToCsv" :loading="isExporting" color="primary">
                    <v-icon left>mdi-file-download</v-icon>{{ isExporting ? 'Exporting...' : 'Export ToDos' }}
                </v-btn>
            </v-container>
        </v-app-bar>

        <v-main>
            <v-container>
                <RouterView />
            </v-container>
        </v-main>

        <v-snackbar v-model="showSnackbar" :timeout="3000" color="info">
            <v-icon :icon="snackbarIcon" class="mr-2"></v-icon>
            {{ exportStatus }}
            <template v-slot:actions>
                <v-btn color="white" @click="showSnackbar = false">
                    Close
                </v-btn>
            </template>
        </v-snackbar>
    </v-app>
</template>

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
