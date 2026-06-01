import { defineConfig, devices } from '@playwright/test';

const baseURL = process.env.PLAYWRIGHT_BASE_URL ?? 'http://localhost:5174';
// E2E always targets the isolated api-test back-end (8081), never the dev DB on 8080.
const apiUrl = process.env.PLAYWRIGHT_API_URL ?? 'http://localhost:8081/api/v1/';
process.env.VITE_APP_API_URL = apiUrl;

export default defineConfig({
  testDir: './e2e',
  testMatch: '**/*-spec.ts',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  // Many parallel tabs against one Vite dev server (e.g. WSL) can exceed 30s until `load`.
  workers: process.env.CI ? 1 : 4,
  // Default 30s is tight when navigation waits for full `load` under parallel load.
  timeout: 60_000,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL,
    locale: 'es-ES',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    actionTimeout: 15_000,
    navigationTimeout: 45_000,
  },
  expect: {
    timeout: 15_000,
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
  // Port 5174 + mode e2e so we never reuse `npm run dev` on 5173 (.env.development → 8080).
  webServer: {
    command: 'npm run dev:e2e',
    url: baseURL,
    reuseExistingServer: !process.env.CI,
    timeout: 180_000,
    env: {
      VITE_APP_API_URL: apiUrl,
    },
  },
});
