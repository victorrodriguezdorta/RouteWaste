import type {} from '@ull-tfg/ull-tfg-vue';
import type { DefineComponent } from 'vue';

declare module '@ull-tfg/ull-tfg-vue' {
  export const Footer: DefineComponent<{
    links: import('@ull-tfg/ull-tfg-typescript').Link[];
    color?: string;
    height?: number;
    app?: boolean;
  }>;
}
