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
const searchId = ref('');
const searchedTodo = ref<ToDo | null>(null);

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

function searchToDoById() {
    if (!searchId.value) {
        showToast(new Toast("Error", "Please enter a ToDo ID", "error", faXmark));
        return;
    }

    fetch(`${config.apiBaseUrl}/todos/${searchId.value}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("ToDo not found");
            }
            return response.json();
        })
        .then(data => {
            searchedTodo.value = data;
            todos.value = [data];
            showToast(new Toast("Success", "ToDo found", "success", faCheck));
        })
        .catch(err => {
            showToast(new Toast("Error", err.message, "error", faXmark));
            searchedTodo.value = null;
            todos.value = [];
        });
}

onMounted(async () => {
    await fetchAssignees();
    fetchAllToDos();
});
</script>

<template>
    <v-container>
        <v-row align="center" class="mb-4">
            <v-spacer></v-spacer>
            <v-col cols="12" sm="4" class="text-right">
                <v-btn color="primary" @click="navigateToCreate">
                    Create New ToDo
                </v-btn>
            </v-col>
        </v-row>

        <v-row class="mb-4">
            <v-col cols="12" sm="6">
                <v-text-field
                    v-model="titleFilter"
                    label="Filter by title"
                    outlined
                    dense
                ></v-text-field>
            </v-col>
            <v-col cols="12" sm="3">
                <v-btn @click="toggleSort('title')" block height="56">
                    Sort by Title {{ sortBy === 'title' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
                </v-btn>
            </v-col>
            <v-col cols="12" sm="3">
                <v-btn @click="toggleSort('dueDate')" block height="56">
                    Sort by Due Date {{ sortBy === 'dueDate' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
                </v-btn>
            </v-col>
        </v-row>

        <h2 class="text-h5 mb-3">Active ToDos</h2>
        <v-alert v-if="activeTodos.length === 0" type="warning" class="mb-4">
            No active ToDos available or match the filter...
        </v-alert>
        <v-row v-else>
            <v-col v-for="todo in activeTodos" :key="todo.id" cols="12" sm="6" md="4">
                <v-card>
                    <v-card-title>
                        <v-checkbox v-model="todo.finished" @change="updateToDoStatus(todo)" class="mr-2"></v-checkbox>
                        {{ todo.title }}
                    </v-card-title>
                    <v-card-text>
                        <p class="text-truncate">{{ todo.description }}</p>
                        <p>ID: {{ todo.id }}</p>
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
        </v-row>

        <h2 class="text-h5 mb-3 mt-6">Completed ToDos</h2>
        <v-alert v-if="completedTodos.length === 0" type="warning" class="mb-4">
            No completed ToDos available or match the filter...
        </v-alert>
        <v-row v-else>
            <v-col v-for="todo in completedTodos" :key="todo.id" cols="12" sm="6" md="4">
                <v-card class="completed">
                    <v-card-title>
                        <v-checkbox v-model="todo.finished" @change="updateToDoStatus(todo)" class="mr-2"></v-checkbox>
                        {{ todo.title }}
                    </v-card-title>
                    <v-card-text>
                        <p class="text-truncate">{{ todo.description }}</p>
                        <p>ID: {{ todo.id }}</p>
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
            </v-col>
        </v-row>
    </v-container>
</template>

<style scoped>
.completed {
    opacity: 0.7;
}
.text-truncate {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
</style>
