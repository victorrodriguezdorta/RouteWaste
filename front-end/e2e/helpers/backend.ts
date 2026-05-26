import { isBackendAvailable } from './api';
import { test } from '@playwright/test';

export async function skipWithoutBackend(): Promise<void> {
  const backendUp = await isBackendAvailable();
  test.skip(
    !backendUp,
    'Back-end not available at localhost:8080 (run: docker compose --profile back-end up -d)',
  );
}
