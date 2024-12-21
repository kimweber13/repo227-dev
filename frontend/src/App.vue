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

/**
 * Exports the ToDos to a CSV file.
 *
 * This function triggers the export process, updates the export status,
 * and handles the response to save the CSV file. It also manages the
 * snackbar notifications for success or failure.
 */
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
    <!-- Main application container -->
    <v-app>
        <!-- Application bar at the top -->
        <v-app-bar app>
            <!-- Container for aligning elements -->
            <v-container class="d-flex align-center">
                <!-- Button to navigate to the About page -->
                <v-btn to="/about" class="custom-btn">
                    <!-- Icon for the button -->
                    <v-icon>mdi-checkbox-marked-circle-outline</v-icon>ToDo List
                </v-btn>
                <!-- Spacer to push elements apart -->
                <v-spacer></v-spacer>
                <!-- Tabs for navigation -->
                <v-tabs>
                    <!-- Tab to navigate to the ToDos page -->
                    <v-tab to="/todos">ToDos</v-tab>
                    <!-- Tab to navigate to the Assignees page -->
                    <v-tab to="/assignees">Assignees</v-tab>
                </v-tabs>
                <!-- Spacer to push elements apart -->
                <v-spacer></v-spacer>
                <!-- Button to trigger CSV export -->
                <v-btn class="custom-btn" @click="exportToCsv" :loading="isExporting" color="primary">
                    <!-- Icon for the export button -->
                    <v-icon left>mdi-file-download</v-icon>{{ isExporting ? 'Exporting...' : 'Export ToDos' }}
                </v-btn>
            </v-container>
        </v-app-bar>

        <!-- Main content area -->
        <v-main>
            <!-- Container for the router view -->
            <v-container>
                <!-- Router view to display the current route's component -->
                <RouterView />
            </v-container>
        </v-main>

        <!-- Snackbar for displaying export status messages -->
        <v-snackbar v-model="showSnackbar" :timeout="3000" color="info">
            <!-- Icon for the snackbar -->
            <v-icon :icon="snackbarIcon" class="mr-2"></v-icon>
            {{ exportStatus }}
            <!-- Actions slot for the snackbar -->
            <template v-slot:actions>
                <!-- Button to close the snackbar -->
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
