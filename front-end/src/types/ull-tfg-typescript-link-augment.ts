/**
 * `Footer` from ull-tfg-vue expects `Link` from `@ull-tfg/ull-tfg-typescript`; the published
 * typings omit it. Augment the module (do not replace it — use `import type {}` first).
 */
import type {} from '@ull-tfg/ull-tfg-typescript';

declare module '@ull-tfg/ull-tfg-typescript' {
  export interface Link {
    text: string;
    href: string;
  }
}
