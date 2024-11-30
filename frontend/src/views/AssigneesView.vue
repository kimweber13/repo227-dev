<script setup lang="ts">
import { Alert, Button } from "agnostic-vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
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
  <h1>Assignees</h1>
  <Button @click="navigateToCreate">Create New Assignee</Button>

  <Alert v-if="assignees.length === 0" type="warning">
    No assignees available on the server...
  </Alert>

  <div v-else>
    <div class="assigneeBox" v-for="assignee in assignees" :key="assignee.id">
      <h3>{{ assignee.prename }} {{ assignee.name }}</h3>
      <p>Email: {{ assignee.email }}</p>
      <div>
        <Button @click="deleteAssignee(assignee.id)" mode="secondary">
          <FontAwesomeIcon icon="trash"/> Delete
        </Button>
        <Button @click="editAssignee(assignee.id)" mode="primary">
          <FontAwesomeIcon icon="edit"/> Edit
        </Button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.assigneeBox {
  padding: 20px;
  margin-bottom:15px;

  background-color:#4a4a4a;
  border-radius:.5rem;
  box-shadow:.25rem .25rem .75rem rgba(0,0,0,.1);

}
.assigneeBox h3{
  margin-top:0;

}
.assigneeBox p{
  margin-bottom:.5rem;

}
.assigneeBox button{
  margin-right:.5rem;

}
button{
  background-color:#3498db;

  color:white;

  border-radius:.25rem;

  border:none;

  padding:.5rem .75rem;

  transition:background-color .3s ease;

  cursor:pointer;


}
button:hover{
  background-color:#2980b9;


}
</style>
