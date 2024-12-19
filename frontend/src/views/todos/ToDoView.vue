<template>
    <v-container fluid>
        <v-row align="start" class="mb-2">
            <v-col cols="12" sm="2" class="text-right">
                <v-btn class="custom-btn" color="primary" @click="navigateToCreate" block>
                    <v-icon left>mdi-file-plus</v-icon> Create ToDo
                </v-btn>
            </v-col>
            <v-spacer></v-spacer>
            <v-col cols="12" sm="2" class="text-right">
                <v-btn class="custom-btn" color="primary" @click="navigateToSearch" block>
                    <v-icon left>mdi-magnify</v-icon> Search ToDo
                </v-btn>
            </v-col>
        </v-row>

        <v-row class="mt-n2">
            <v-col cols="12" sm="8">
                <v-text-field
                    v-model="titleFilter"
                    label="Filter by title"
                    outlined
                    dense
                ></v-text-field>
            </v-col>
            <v-col cols="12" sm="2">
                <v-btn @click="toggleSort('title')" block height="56">
                    Sort by Title {{ sortBy === 'title' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
                </v-btn>
            </v-col>
            <v-col cols="12" sm="2">
                <v-btn @click="toggleSort('dueDate')" block height="56">
                    Sort by Due Date {{ sortBy === 'dueDate' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
                </v-btn>
            </v-col>
        </v-row>
        <v-row>
            <!-- Linke Hälfte: Aktive ToDos -->
            <v-col cols="12" md="6">
                <h2 class="text-h5 mb-3">Active ToDos</h2>
                <v-alert v-if="activeTodos.length === 0" type="warning" class="mt-4">
                    No active ToDos available or match the filter...
                </v-alert>
                <v-card v-for="todo in activeTodos" :key="todo.id" class="mt-4">
                    <v-card-title>
                        <v-checkbox v-model="todo.finished" @change="updateToDoStatus(todo)" class="mr-2"></v-checkbox>
                        {{ todo.title }}
                    </v-card-title>
                    <v-card-text>
                        <p>ID: {{ todo.id }}</p>
                        <p class="text-truncate">{{ todo.description }}</p>
                        <p>Due Date: {{ new Date(todo.dueDate as string).toLocaleDateString() }}</p>
                        <p>Assigned to: {{ getAssigneesForTodo(todo) }}</p>
                        <p>Category: {{ todo.category }}</p>
                    </v-card-text>
                    <v-card-actions>
                        <v-btn @click="editToDo(todo.id)" color="warning">
                            <v-icon left>mdi-pencil</v-icon>
                            Edit
                        </v-btn>
                        <v-btn @click="deleteToDo(todo.id)" color="error">
                            <v-icon left>mdi-delete</v-icon>
                            Delete
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>

            <!-- Rechte Hälfte: Formular zum Erstellen eines neuen ToDos und abgeschlossene ToDos -->
            <v-col cols="12" md="6">

                <v-expansion-panels v-model="createTodoPanel">
                    <v-expansion-panel>
                        <v-expansion-panel-title>
                            <h2 class="text-h5">Create New ToDo</h2>
                        </v-expansion-panel-title>
                        <v-expansion-panel-text>
                            <v-form @submit.prevent="submitForm">
                                <v-text-field
                                    v-model="newTodo.title"
                                    label="Title"
                                    required
                                ></v-text-field>
                                <v-textarea
                                    v-model="newTodo.description"
                                    label="Description"
                                    required
                                ></v-textarea>
                                <v-text-field
                                    v-model="dueDateInput"
                                    label="Due Date"
                                    type="datetime-local"
                                    required
                                ></v-text-field>
                                <v-card class="mb-4">
                                    <v-card-title>Assignees</v-card-title>
                                    <v-card-text>
                                        <v-checkbox
                                            v-for="assignee in assignees"
                                            :key="assignee.id"
                                            v-model="newTodo.assigneeIdList"
                                            :label="`${assignee.prename} ${assignee.name}`"
                                            :value="assignee.id"
                                        ></v-checkbox>
                                    </v-card-text>
                                </v-card>
                                <v-btn type="submit" color="primary" class="custom-btn">Create ToDo</v-btn>
                            </v-form>
                        </v-expansion-panel-text>
                    </v-expansion-panel>
                </v-expansion-panels>


                <v-expansion-panels>
                    <v-expansion-panel>
                        <v-expansion-panel-title>
                            <h2 class="text-h5">Completed ToDos</h2>
                        </v-expansion-panel-title>
                        <v-expansion-panel-text>
                            <v-alert v-if="completedTodos.length === 0" type="warning" class="mt-4">
                                No completed ToDos available or match the filter...
                            </v-alert>
                            <v-card v-for="todo in completedTodos" :key="todo.id" class="mt-4 completed" color="#F6F6F6">
                                <v-card-title>
                                    <v-checkbox v-model="todo.finished" @change="updateToDoStatus(todo)" class="mr-2"></v-checkbox>
                                    {{ todo.title }}
                                </v-card-title>
                                <v-card-text>
                                    <p>ID: {{ todo.id }}</p>
                                    <p class="text-truncate">{{ todo.description }}</p>
                                    <p>Completed Date: {{ new Date(todo.completedDate as string).toLocaleDateString() }}</p>
                                    <p>Assigned to: {{ getAssigneesForTodo(todo) }}</p>
                                    <p>Category: {{ todo.category }}</p>
                                </v-card-text>
                                <v-card-actions>
                                    <v-btn @click="editToDo(todo.id)" color="warning">
                                        <v-icon left>mdi-pencil</v-icon>
                                        Edit
                                    </v-btn>
                                    <v-btn @click="deleteToDo(todo.id)" color="error">
                                        <v-icon left>mdi-delete</v-icon>
                                        Delete
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-expansion-panel-text>
                    </v-expansion-panel>
                </v-expansion-panels>
            </v-col>
        </v-row>
    </v-container>
</template>


<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import config from "@/config";
import { showToast, Toast } from "@/ts/toasts";
import { faCheck, faXmark } from "@fortawesome/free-solid-svg-icons";

interface Assignee {
    id: number;
    prename: string;
    name: string;
}

interface ToDo {
    id: number;
    title: string;
    description: string;
    finished: boolean;
    createdDate?: string;
    dueDate?: string;
    completedDate: string | null | undefined;
    assigneeList: Assignee[];
    assigneeIdList: number[];
    category: string;
}

const todos = ref<ToDo[]>([]);
const assignees = ref<Assignee[]>([]);
const router = useRouter();
const titleFilter = ref('');
const sortBy = ref('title');
const sortOrder = ref('asc');

const newTodo = ref({
    title: '',
    description: '',
    assigneeIdList: [] as number[],
    dueDate: 0,
    category: ''
});
const dueDateInput = ref('');

const filteredAndSortedTodos = computed(() => {
    let result = todos.value.filter(todo => todo.title.toLowerCase().includes(titleFilter.value.toLowerCase()));
    result.sort((a, b) => {
        if (sortBy.value === 'title') {
            return sortOrder.value === 'asc' ? a.title.localeCompare(b.title) : b.title.localeCompare(a.title);
        } else if (sortBy.value === 'dueDate') {
            const dateA = new Date(a.dueDate || '').getTime();
            const dateB = new Date(b.dueDate || '').getTime();
            return sortOrder.value === 'asc' ? dateA - dateB : dateB - dateA;
        }
        return 0;
    });
    return result;
});

const activeTodos = computed(() => filteredAndSortedTodos.value.filter(todo => !todo.finished));
const completedTodos = computed(() => filteredAndSortedTodos.value.filter(todo => todo.finished));

const assigneeItems = computed(() => {
    return assignees.value.map(assignee => ({
        id: assignee.id,
        name: `${assignee.prename} ${assignee.name}`
    }));
});

const createTodoPanel = ref([0]); // Panel standardmäßig geöffnet

function fetchAllToDos() {
    fetch(`${config.apiBaseUrl}/todos`)
        .then(response => response.json())
        .then(data => {
            todos.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

function fetchAssignees() {
    fetch(`${config.apiBaseUrl}/assignees`)
        .then(response => response.json())
        .then(data => {
            assignees.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

async function updateToDoStatus(todo: ToDo) {
    const updateData = {
        id: todo.id,
        title: todo.title,
        description: todo.description,
        finished: todo.finished,
        completedDate: !todo.finished ? new Date().toISOString() : null,
        assigneeIdList: todo.assigneeList.map(assignee => assignee.id),
        assigneeList: todo.assigneeList,
        dueDate: todo.dueDate,
        category: todo.category
    };

    try {
        const response = await fetch(`${config.apiBaseUrl}/todos/${todo.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updateData),
        });
        if (!response.ok) {
            throw new Error('Failed to update todo status');
        }
        todo.completedDate = updateData.completedDate;
    } catch (error) {
        showToast(new Toast("Error", "Failed to update todo status", "error", faXmark));
    }
}

function deleteToDo(id: number) {
    fetch(`${config.apiBaseUrl}/todos/${id}`, { method: "DELETE" })
        .then(() => {
            todos.value = todos.value.filter((todo) => todo.id !== id);
            showToast(new Toast("Alert", `Successfully deleted ToDo with ID ${id}!`, "success", faCheck));
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

function editToDo(id: number) {
    router.push(`/todos/${id}/edit`);
}

function navigateToCreate() {
    router.push('/create-todo');
}

function navigateToSearch() {
    router.push('/todo-by-id');
}

function getAssigneesForTodo(todo: ToDo): string {
    if (!todo.assigneeList?.length) {
        return "No assignees";
    }
    return todo.assigneeList
        .map(assignee => `${assignee.prename} ${assignee.name}`)
        .join(", ");
}

function toggleSort(field: 'title' | 'dueDate') {
    if (sortBy.value === field) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
    } else {
        sortBy.value = field;
        sortOrder.value = 'asc';
    }
}

function submitForm() {
    const todoToSubmit = {
        ...newTodo.value,
        dueDate: new Date(dueDateInput.value).getTime(),
        finished: false
    };

    fetch(`${config.apiBaseUrl}/todos`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(todoToSubmit),
    })
        .then(response => response.json())
        .then(data => {
            todos.value.push(data);
            showToast(new Toast('Success', 'ToDo created successfully!', 'success', faCheck));
            newTodo.value = { title: '', description: '', assigneeIdList: [], dueDate: 0, category: '' };
            dueDateInput.value = '';
        })
        .catch(error => showToast(new Toast('Error', error.message, 'error', faXmark)));
}

onMounted(async () => {
    await fetchAssignees();
    fetchAllToDos();
});
</script>

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
