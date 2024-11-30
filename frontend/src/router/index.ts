import { createRouter, createWebHashHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import AboutView from '@/views/AboutView.vue';
import AssigneesView from '@/views/AssigneesView.vue';
import ToDoView from '@/views/ToDoView.vue';
import CreateEditAssignee from '@/views/CreateEditAssignee.vue';
import CreateEditToDo from "@/views/CreateEditToDo.vue";

const routes = [
  { path: '/', component: HomeView },
  { path: '/about', component: AboutView },
  { path: '/assignees', component: AssigneesView },
  { path: '/create-assignee', component: CreateEditAssignee },
  { path: '/assignees/:id/edit', component: CreateEditAssignee },
  { path: '/todos', component: ToDoView },
  { path: '/create-todo', component: CreateEditToDo },
  { path: '/todos/:id/edit', component: CreateEditToDo }, // Add this line for editing todos
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
