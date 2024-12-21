<template>
    <!-- Container for the ToDo view with fluid layout -->
    <v-container fluid>
        <!-- Row for the search button -->
        <v-row align="start" class="mb-2">
            <!-- Empty column for alignment -->
            <v-col cols="12" sm="2" class="text-right">

            </v-col>
            <!-- Spacer for alignment -->
            <v-spacer></v-spacer>
            <!-- Column for the search button -->
            <v-col cols="12" sm="2" class="text-right">
                <!-- Button to navigate to the search ToDo page -->
                <v-btn class="custom-btn" color="primary" @click="navigateToSearch" block>
                    <!-- Icon for the search button -->
                    <v-icon left>mdi-magnify</v-icon> Search ToDo
                </v-btn>
            </v-col>
        </v-row>

        <!-- Row for the filter and sort buttons -->
        <v-row class="mt-n2">
            <!-- Column for the title filter input field -->
            <v-col cols="12" sm="8">
                <!-- Input field for filtering ToDos by title -->
                <v-text-field
                    v-model="titleFilter"
                    label="Filter by title"
                    outlined
                    dense
                ></v-text-field>
            </v-col>
            <!-- Column for the sort by title button -->
            <v-col cols="12" sm="2">
                <!-- Button to toggle sort order by title -->
                <v-btn @click="toggleSort('title')" block height="56">
                    Sort by Title {{ sortBy === 'title' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
                </v-btn>
            </v-col>
            <!-- Column for the sort by due date button -->
            <v-col cols="12" sm="2">
                <!-- Button to toggle sort order by due date -->
                <v-btn @click="toggleSort('dueDate')" block height="56">
                    Sort by Due Date {{ sortBy === 'dueDate' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
                </v-btn>
            </v-col>
        </v-row>
        <!-- Row for displaying active and completed ToDos -->
        <v-row>
            <!-- Column for active ToDos -->
            <v-col cols="12" md="6">
                <h2 class="text-h5 mb-3">Active ToDos</h2>
                <!-- Alert if no active ToDos are available or match the filter -->
                <v-alert v-if="activeTodos.length === 0" type="warning" class="mt-4">
                    No active ToDos available or match the filter...
                </v-alert>
                <!-- Card for each active ToDo -->
                <v-card v-for="todo in activeTodos" :key="todo.id" class="mt-4">
                    <!-- Card title with a checkbox to mark the ToDo as finished -->
                    <v-card-title>
                        <v-checkbox v-model="todo.finished" @change="updateToDoStatus(todo)" class="mr-2"></v-checkbox>
                        {{ todo.title }}
                    </v-card-title>
                    <!-- Card text with ToDo details -->
                    <v-card-text>
                        <p>ID: {{ todo.id }}</p>
                        <p class="text-truncate">{{ todo.description }}</p>
                        <p>Due Date: {{ new Date(todo.dueDate as string).toLocaleDateString() }}</p>
                        <p>Assigned to: {{ getAssigneesForTodo(todo) }}</p>
                        <p>Category: {{ todo.category }}</p>
                    </v-card-text>
                    <!-- Card actions with edit and delete buttons -->
                    <v-card-actions>
                        <!-- Button to navigate to the edit page -->
                        <v-btn @click="editToDo(todo.id)" color="warning">
                            <v-icon left>mdi-pencil</v-icon>
                            Edit
                        </v-btn>
                        <!-- Button to delete the ToDo -->
                        <v-btn @click="deleteToDo(todo.id)" color="error">
                            <v-icon left>mdi-delete</v-icon>
                            Delete
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>

            <!-- Column for creating new ToDo and displaying completed ToDos -->
            <v-col cols="12" md="6">
                <!-- Expansion panel for creating a new ToDo -->
                <v-expansion-panels v-model="createTodoPanel">
                    <v-expansion-panel>
                        <v-expansion-panel-title>
                            <h2 class="text-h5">Create New ToDo</h2>
                        </v-expansion-panel-title>
                        <v-expansion-panel-text>
                            <!-- Form for creating a new ToDo -->
                            <v-form @submit.prevent="submitForm">
                                <!-- Input field for the new ToDo title -->
                                <v-text-field
                                    v-model="newTodo.title"
                                    label="Title"
                                    required
                                ></v-text-field>
                                <!-- Textarea for the new ToDo description -->
                                <v-textarea
                                    v-model="newTodo.description"
                                    label="Description"
                                    required
                                ></v-textarea>
                                <!-- Input field for the new ToDo due date -->
                                <v-text-field
                                    v-model="dueDateInput"
                                    label="Due Date"
                                    type="datetime-local"
                                    required
                                ></v-text-field>
                                <!-- Card for selecting assignees -->
                                <v-card class="mb-4">
                                    <v-card-title>Assignees</v-card-title>
                                    <v-card-text>
                                        <!-- Checkbox for each assignee -->
                                        <v-checkbox
                                            v-for="assignee in assignees"
                                            :key="assignee.id"
                                            v-model="newTodo.assigneeIdList"
                                            :label="`${assignee.prename} ${assignee.name}`"
                                            :value="assignee.id"
                                        ></v-checkbox>
                                    </v-card-text>
                                </v-card>
                                <!-- Button to submit the form and create the new ToDo -->
                                <v-btn type="submit" color="primary" class="custom-btn">
                                    <v-icon left>mdi-file-plus</v-icon>Create ToDo
                                </v-btn>
                            </v-form>
                        </v-expansion-panel-text>
                    </v-expansion-panel>
                </v-expansion-panels>

                <!-- Expansion panel for displaying completed ToDos -->
                <v-expansion-panels>
                    <v-expansion-panel>
                        <v-expansion-panel-title>
                            <h2 class="text-h5">Completed ToDos</h2>
                        </v-expansion-panel-title>
                        <v-expansion-panel-text>
                            <!-- Alert if no completed ToDos are available or match the filter -->
                            <v-alert v-if="completedTodos.length === 0" type="warning" class="mt-4">
                                No completed ToDos available or match the filter...
                            </v-alert>
                            <!-- Card for each completed ToDo -->
                            <v-card v-for="todo in completedTodos" :key="todo.id" class="mt-4 completed" color="#F6F6F6">
                                <!-- Card title with a checkbox to mark the ToDo as finished -->
                                <v-card-title>
                                    <v-checkbox v-model="todo.finished" @change="updateToDoStatus(todo)" class="mr-2"></v-checkbox>
                                    {{ todo.title }}
                                </v-card-title>
                                <!-- Card text with ToDo details -->
                                <v-card-text>
                                    <p>ID: {{ todo.id }}</p>
                                    <p class="text-truncate">{{ todo.description }}</p>
                                    <p>Completed Date: {{ new Date(todo.completedDate as string).toLocaleDateString() }}</p>
                                    <p>Assigned to: {{ getAssigneesForTodo(todo) }}</p>
                                    <p>Category: {{ todo.category }}</p>
                                </v-card-text>
                                <!-- Card actions with edit and delete buttons -->
                                <v-card-actions>
                                    <!-- Button to navigate to the edit page -->
                                    <v-btn @click="editToDo(todo.id)" color="warning">
                                        <v-icon left>mdi-pencil</v-icon>
                                        Edit
                                    </v-btn>
                                    <!-- Button to delete the ToDo -->
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

const createTodoPanel = ref([0]);

/**
 * Fetches all ToDos from the server.
 *
 * This function sends a GET request to the server to retrieve all ToDos.
 * On success, it updates the `todos` ref with the fetched data.
 * On failure, it shows an error toast notification.
 */
function fetchAllToDos() {
    fetch(`${config.apiBaseUrl}/todos`)
        .then(response => response.json())
        .then(data => {
            todos.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Fetches all assignees from the server.
 *
 * This function sends a GET request to the server to retrieve all assignees.
 * On success, it updates the `assignees` ref with the fetched data.
 * On failure, it shows an error toast notification.
 */
function fetchAssignees() {
    fetch(`${config.apiBaseUrl}/assignees`)
        .then(response => response.json())
        .then(data => {
            assignees.value = data;
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Updates the status of the given ToDo.
 *
 * This function sends a PUT request to the server to update the status of the ToDo.
 * On success, it updates the `completedDate` of the ToDo.
 * On failure, it shows an error toast notification.
 *
 * @param {ToDo} todo - The ToDo object to update.
 */
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

/**
 * Deletes a ToDo by its ID.
 *
 * This function sends a DELETE request to the server to delete the ToDo with the given ID.
 * On success, it removes the ToDo from the `todos` ref and shows a success toast notification.
 * On failure, it shows an error toast notification.
 *
 * @param {number} id - The ID of the ToDo to delete.
 */
function deleteToDo(id: number) {
    fetch(`${config.apiBaseUrl}/todos/${id}`, { method: "DELETE" })
        .then(() => {
            todos.value = todos.value.filter((todo) => todo.id !== id);
            showToast(new Toast("Alert", `Successfully deleted ToDo with ID ${id}!`, "success", faCheck));
        })
        .catch(error => showToast(new Toast("Error", error.message, "error", faXmark)));
}

/**
 * Navigates to the edit page for a ToDo by its ID.
 *
 * This function uses the router to navigate to the edit page for the ToDo with the given ID.
 *
 * @param {number} id - The ID of the ToDo to edit.
 */
function editToDo(id: number) {
    router.push(`/todos/${id}/edit`);
}

/**
 * Navigates to the create ToDo page.
 *
 * This function uses the router to navigate to the create ToDo page.
 */
function navigateToCreate() {
    router.push('/create-todo');
}

/**
 * Navigates to the search ToDo by ID page.
 *
 * This function uses the router to navigate to the search ToDo by ID page.
 */
function navigateToSearch() {
    router.push('/todo-by-id');
}

/**
 * Returns a string of assignees for the given ToDo.
 *
 * This function takes a ToDo object and returns a string of assignees' names.
 * If there are no assignees, it returns "No assignees".
 *
 * @param {ToDo} todo - The ToDo object.
 * @returns {string} - A string of assignees' names or "No assignees".
 */
function getAssigneesForTodo(todo: ToDo): string {
    if (!todo.assigneeList?.length) {
        return "No assignees";
    }
    return todo.assigneeList
        .map(assignee => `${assignee.prename} ${assignee.name}`)
        .join(", ");
}

/**
 * Toggles the sort order for the given field.
 *
 * This function toggles the sort order (ascending/descending) for the specified field.
 * If the field is already being sorted, it reverses the sort order.
 * Otherwise, it sets the sort order to an ascending order for the new field.
 *
 * @param {'title' | 'dueDate'} field - The field to sort by.
 */
function toggleSort(field: 'title' | 'dueDate') {
    if (sortBy.value === field) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
    } else {
        sortBy.value = field;
        sortOrder.value = 'asc';
    }
}

/**
 * Submits the form to create a new ToDo.
 *
 * This function sends a POST request to the server to create a new ToDo.
 * On success, it adds the new ToDo to the `todos` ref and shows a success toast notification.
 * On failure, it shows an error toast notification.
 */
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
