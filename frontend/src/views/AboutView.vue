<template>
  <div class="about">
    <h1>About This Project</h1>
    <div v-html="readmeContent" class="content"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { marked } from 'marked';

const readmeContent = ref('');

/**
 * Fetches the README.md file, converts its content to HTML using the marked library,
 * and assigns the result to the readmeContent reference.
 */
onMounted(async () => {
  try {
    const response = await fetch('/README.md');
    const text = await response.text();
    readmeContent.value = await marked(text);
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
  padding: 40px;
  background-color: #2e2e2e;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
}

.content {
  font-size: 1.1rem;
  line-height: 1.6;
}

.content p {
  margin-bottom: 15px;
}

.content h2,
.content h3 {
  color: #3498db; /* Modern blue color for headings */
}

.content pre {
  background-color: #272c34; /* Dark background for code blocks */
  color: #ffffff; /* White text for code */
  padding: 10px;
  border-radius: 5px;
}

.content code {
  background-color: #eaeaea; /* Light gray background for inline code */
  padding: 2px 4px;
  border-radius: 4px;
}
</style>
