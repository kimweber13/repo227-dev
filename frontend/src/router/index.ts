import { createRouter, createWebHashHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import AboutView from '@/views/AboutView.vue';
import AssigneesView from '@/views/assignees/AssigneesView.vue';
import ToDoView from '@/views/todos/ToDoView.vue';
import CreateEditAssignee from '@/views/assignees/CreateEditAssignee.vue';
import EditToDo from "@/views/todos/EditToDo.vue";
import AssigneeByIdView from '@/views/assignees/AssigneeByIdView.vue';
import ToDoByIdView from '@/views/todos/ToDoByIdView.vue';
import ExportToDoView from '@/views/ExportToDoView.vue';

const routes = [
  { path: '/', component: HomeView },
  { path: '/about', component: AboutView },
  { path: '/assignees', component: AssigneesView },
  { path: '/create-assignee', component: CreateEditAssignee },
  { path: '/assignees/:id/edit', component: CreateEditAssignee },
  { path: '/todos', component: ToDoView },
  { path: '/todos/:id/edit', component: EditToDo },
  { path: '/assignee-by-id', component: AssigneeByIdView },
  { path: '/todo-by-id', component: ToDoByIdView },
  { path: '/export-todos', component: ExportToDoView },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
