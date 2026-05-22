const DEFAULT_API_BASE = 'http://localhost:8080/api/v1/';

export function apiBaseUrl(): string {
  return process.env.VITE_APP_API_URL ?? DEFAULT_API_BASE;
}

/** Returns true when the Spring back-end responds on application-overview. */
export async function isBackendAvailable(): Promise<boolean> {
  const url = `${apiBaseUrl()}application-overview`;
  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
    return response.ok;
  } catch {
    return false;
  }
}
