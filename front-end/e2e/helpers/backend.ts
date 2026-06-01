import { isBackendAvailable } from './api';
import { test } from '@playwright/test';

export async function skipWithoutBackend(): Promise<void> {
  const backendUp = await isBackendAvailable();
  test.skip(
    !backendUp,
    'Back-end not available at localhost:8081 (run: docker compose --profile api-test up -d)',
  );
}
