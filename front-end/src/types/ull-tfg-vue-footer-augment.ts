import type {} from '@ull-tfg/ull-tfg-vue';
import type { DefineComponent } from 'vue';

declare module '@ull-tfg/ull-tfg-vue' {
  export const Footer: DefineComponent<{
    links: import('@ull-tfg/ull-tfg-typescript').Link[];
    color?: string;
    height?: number;
    app?: boolean;
  }>;

  export const PieChart: DefineComponent<{
    title?: string;
    id: number;
    data: Array<Record<string, string | number>>;
    width_props: number;
    height_props: number;
  }>;

  export const StackedBarPlot: DefineComponent<{
    title?: string;
    id: number;
    data: Array<Record<string, string | number>>;
    width_props: number;
    height_props: number;
  }>;

  export const LinePlot: DefineComponent<{
    title?: string;
    id: number;
    data: Array<Record<string, number>>;
    width_props: number;
    height_props: number;
  }>;
}
