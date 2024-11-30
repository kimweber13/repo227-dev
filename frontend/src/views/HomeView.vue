<script setup lang="ts">
import {Button} from "agnostic-vue";
import {showToast, Toast} from "@/ts/toasts";

let name = "";

function submit() {
  showToast(new Toast("Welcome!", `Hello ${name}! Nice to meet you!`));
}
</script>

<template>
  <main>
    <h1>Welcome to Your Vue.js App</h1>
    <label class="mbs2 mbe2" >Type your name:</label>
    <br/>
    <input type="text" class="mbs2 mbe2" v-model="name"/>
    <br/>
    <Button class="mbs2 mbe2" @click="submit">Submit</Button>
  </main>
</template>

<style scoped>
main{
  max-width: 600px;
  margin: auto;
  padding-top: 0.5rem;
  text-align: center;
}

input[type=text]{
  width: 100%;
  padding: 0.5rem;
  border-radius: 0.25rem;
  border: 0.1rem solid #ccc;
}

input[type=text]:focus{
  outline: none;
  border-color: #3498db;
}

button{
  background-color: #3498db;
  color: white;
  border-radius: 0.25rem;
  border: none;
  padding: 0.5rem 0.75rem;
  transition: background-color 0.3s ease;
  cursor: pointer;
}

button:hover{
  background-color: #2980b9;
}
</style>
