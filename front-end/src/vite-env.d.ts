/// <reference types="vite/client" />

declare module '*.css' {
  const content: string
  export default content
}

declare module 'vuetify/styles' {
  const content: string
  export default content
}

declare module '@mdi/font/css/materialdesignicons.css' {
  const content: string
  export default content
}
