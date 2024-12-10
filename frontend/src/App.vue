<script setup lang="ts">
import { RouterView } from 'vue-router';
import { Header, HeaderNav, HeaderNavItem, Button } from "agnostic-vue";
import { ref } from 'vue';
import { saveAs } from 'file-saver';
import config from "@/config";

import 'agnostic-vue/dist/common.min.css';
import "agnostic-vue/dist/index.css";

const isExporting = ref(false);
const exportStatus = ref('');

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
    <div id="app">
        <Header isHeaderContentStart>
            <template v-slot:headernav>
                <HeaderNav>
                    <HeaderNavItem>
                        <RouterLink to="/">Home</RouterLink>
                    </HeaderNavItem>
                    <HeaderNavItem>
                        <RouterLink to="/about">About</RouterLink>
                    </HeaderNavItem>
                    <HeaderNavItem>
                        <RouterLink to="/assignees">Assignees</RouterLink>
                    </HeaderNavItem>
                    <HeaderNavItem>
                        <RouterLink to="/todos">ToDos</RouterLink>
                    </HeaderNavItem>
                    <HeaderNavItem>
                        <RouterLink to="/search-by-id">Search by ID</RouterLink>
                    </HeaderNavItem>
                    <HeaderNavItem>
                        <Button @click="exportToCsv" :disabled="isExporting" class="export-button">
                            {{ isExporting ? 'Exporting...' : 'Export ToDos' }}
                        </Button>
                    </HeaderNavItem>
                </HeaderNav>
            </template>
        </Header>

        <div class="main">
            <RouterView/>
        </div>

        <div v-if="exportStatus" class="export-status">
            {{ exportStatus }}
        </div>
    </div>
</template>

<style scoped>

header a {
    color: white;
    text-decoration: none;
    padding: 10px 15px;
    margin-right: 5px; /* Add this line to create space between tabs */
}

body {
  background-color: #2e2e2e; /* Dark grey background */
  color: #ffffff; /* White text */
}

.main {
  padding: 20px;
  background-color: #2e2e2e; /* Dark grey background */
  min-height: 100vh;
}

header {
  background-color: #1f1f1f; /* Slightly darker header */
  color: white;
}

header a {
  color: white;
  text-decoration: none;
  padding: 10px 15px;
}

header a:hover {
  background-color: #3a3a3a; /* Darker hover effect */
}

.export-status {
    position: fixed;
    bottom: 20px;
    right: 20px;
    background-color: #4a4a4a;
    color: white;
    padding: 10px 20px;
    border-radius: 5px;
    box-shadow: 0px 0px 10px rgba(0,0,0,0.1);
}

.toasts-container {
  position: fixed;
  top: 20px;
  right: 20px;
}

.toast {
  background-color: #4a4a4a; /* Lighter grey for toast */
  border-left: 4px solid #3498db;
  box-shadow: 0px 0px 10px rgba(0,0,0,0.1);
}

.toast h3 {
  margin: 0;
}

.toast .close-button {
  cursor: pointer;
}
</style>
