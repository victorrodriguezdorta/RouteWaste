<template>
  <span class="bulk-import-file-button">
    <input
      ref="fileInputRef"
      type="file"
      accept=".json,application/json"
      class="bulk-import-file-button__input"
      @change="onFileSelected"
    />
    <ButtonTooltip
      :color="color"
      :icon="icon"
      size="default"
      variant="elevated"
      :eventclick="openFilePicker"
      :text="text"
      :tooltip="tooltip"
      :class="buttonClass"
      :disabled="disabled || importing"
    />
  </span>
</template>

<script lang="ts" setup>
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { ref } from 'vue';

const props = withDefaults(
  defineProps<{
    text: string;
    tooltip: string;
    disabled?: boolean;
    importing?: boolean;
    color?: string;
    icon?: string;
    buttonClass?: string;
  }>(),
  {
    disabled: false,
    importing: false,
    color: 'secondary',
    icon: 'mdi-upload',
    buttonClass: '',
  },
);

const emit = defineEmits<{
  import: [file: File];
}>();

const fileInputRef = ref<HTMLInputElement | null>(null);

const openFilePicker = () => {
  if (props.disabled || props.importing) {
    return;
  }
  fileInputRef.value?.click();
};

const onFileSelected = (event: Event) => {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (file) {
    emit('import', file);
  }
  input.value = '';
};
</script>

<style scoped>
.bulk-import-file-button__input {
  display: none;
}
</style>
