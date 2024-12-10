import { createRouter, createWebHashHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import AboutView from '@/views/AboutView.vue';
import AssigneesView from '@/views/AssigneesView.vue';
import ToDoView from '@/views/ToDoView.vue';
import CreateEditAssignee from '@/views/CreateEditAssignee.vue';
import CreateToDo from "@/views/CreateToDo.vue";
import EditToDo from "@/views/EditToDo.vue";
import AssigneeByIdView from '@/views/AssigneeByIdView.vue';
import ToDoByIdView from '@/views/ToDoByIdView.vue';
import SearchByIdView from '@/views/SearchByIdView.vue';
import ExportToDoView from '@/views/ExportToDoView.vue';

const routes = [
  { path: '/', component: HomeView },
  { path: '/about', component: AboutView },
  { path: '/assignees', component: AssigneesView },
  { path: '/create-assignee', component: CreateEditAssignee },
  { path: '/assignees/:id/edit', component: CreateEditAssignee },
  { path: '/todos', component: ToDoView },
  { path: '/create-todo', component: CreateToDo },
  { path: '/todos/:id/edit', component: EditToDo },
  { path: '/assignee-by-id', component: AssigneeByIdView },
  { path: '/todo-by-id', component: ToDoByIdView },
  { path: '/search-by-id', component: SearchByIdView },
  { path: '/export-todos', component: ExportToDoView },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
