<script setup lang="ts">
import LanguageSwitcher from './adapter/vuejs/components/LanguageSwitcher.vue';
</script>

<template>
  <v-app>
    <!-- App bar with language switcher -->
    <v-app-bar color="primary" prominent>
      <v-app-bar-title>Sensor App</v-app-bar-title>
      <v-spacer></v-spacer>
      <language-switcher />
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<style scoped>
</style>

<style scoped>

div {
  color: #000000;
}

p, li, strong, em {
  color: #000000;
}

h1 {
  color: #42b883;
  margin-bottom: 20px;
}

h2 {
  color: #2c3e50;
  margin-top: 30px;
  margin-bottom: 10px;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  background: #f4f4f4;
  margin: 10px 0;
  padding: 15px;
  border-radius: 5px;
  border-left: 4px solid #42b883;
  color: #000000;
}

div > p {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 5px;
  border-left: 4px solid #646cff;
  color: #000000;
}
</style>
