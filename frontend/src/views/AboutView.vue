<template>
  <div class="about">
    <h1>About This Project</h1>
    <div v-html="readmeContent"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { marked } from 'marked';

const readmeContent = ref('');

onMounted(async () => {
  try {
    const response = await fetch('/README.md');
    const text = await response.text();
    readmeContent.value = marked(text);
  } catch (error) {
    console.error('Error loading README.md:', error);
    readmeContent.value = '<p>Error loading README.md content.</p>';
  }
});
</script>

<style scoped>
.about {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>
