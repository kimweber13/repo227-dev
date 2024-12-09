<script setup lang="ts">
import { Alert, Button } from "agnostic-vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import config from "@/config";
import { showToast, Toast } from "@/ts/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

/**
 * Interface representing an Assignee.
 */
interface Assignee {
  id: number;
  prename: string;
  name: string;
  email: string;
}

const assignees = ref<Assignee[]>([]);
const router = useRouter();

/**
 * Fetches all assignees from the server and updates the `assignees` reference.
 * If an error occurs during the fetch operation, it displays an error toast.
 */
function fetchAllAssignees() {
  fetch(`${config.apiBaseUrl}/assignees`)
      .then(response => response.json())
      .then(data => {
        assignees.value = data;
      })
      .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Deletes an assignee by their ID.
 *
 * Sends a DELETE request to the server to remove the assignee with the specified ID.
 * If the deletion is successful, it updates the `assignees` reference to remove the deleted assignee
 * and displays a success toast message. If an error occurs, it displays an error toast message.
 *
 * @param id - The ID of the assignee to be deleted.
 */
function deleteAssignee(id: number) {
  fetch(`${config.apiBaseUrl}/assignees/${id}`, { method: "DELETE" })
      .then(() => {
        assignees.value = assignees.value.filter((assignee) => assignee.id !== id);
        showToast(new Toast("Alert", `Successfully deleted assignee with ID ${id}!`, "success", faCheck));
      })
      .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Navigates to the edit page for the assignee with the specified ID.
 *
 * @param id - The ID of the assignee to be edited.
 */
function editAssignee(id: number) {
  router.push(`/assignees/${id}/edit`);
}

/**
 * Navigates to the create assignee page.
 */
function navigateToCreate() {
  router.push('/create-assignee');
}

onMounted(() => fetchAllAssignees());
</script>

<template>
  <h1>Assignees</h1>
  <div class="button-container">
    <Button @click="navigateToCreate" class="create-button">Create New Assignee</Button>
  </div>

  <Alert v-if="assignees.length === 0" type="warning">
    No assignees available on the server...
  </Alert>

  <div v-else class="assignee-list">
    <div class="assigneeBox" v-for="assignee in assignees" :key="assignee.id">
      <h3>{{ assignee.prename }} {{ assignee.name }}</h3>
      <p>ID: {{ assignee.id }}</p>
      <p>Email: {{ assignee.email }}</p>
      <div class="button-group">
        <Button @click="deleteAssignee(assignee.id)" class="delete-button">
          <FontAwesomeIcon icon="trash"/> Delete
        </Button>
        <Button @click="editAssignee(assignee.id)" class="edit-button">
          <FontAwesomeIcon icon="edit"/> Edit
        </Button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.button-container {
  margin-bottom: 15px;
}

.assignee-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.assigneeBox {
  padding: 20px;
  background-color: #4a4a4a;
  border-radius: 0.5rem;
  box-shadow: 0.25rem 0.25rem 0.75rem rgba(0, 0, 0, 0.1);
}

.assigneeBox h3 {
  margin-top: 0;
}

.assigneeBox p {
  margin-bottom: 0.5rem;
}

.button-group {
  display: flex;
  gap: 0.5rem;
}

button {
  color: white;
  border-radius: 0.25rem;
  border: none;
  padding: 0.5rem 0.75rem;
  transition: background-color 0.3s ease;
  cursor: pointer;
}

.create-button {
  background-color: #2ecc71;
}

.create-button:hover {
  background-color: #27ae60;
}

.delete-button {
  background-color: #e74c3c;
}

.delete-button:hover {
  background-color: #c0392b;
}

.edit-button {
  background-color: #f1c40f;
}

.edit-button:hover {
  background-color: #f39c12;
}
</style>
