/**
 * Waits until the API-test back-end responds on baseUrl (default http://localhost:8081).
 */
const baseUrl = (process.env.API_TEST_BASE_URL ?? "http://localhost:8081").replace(/\/$/, "");
const timeoutMs = Number(process.env.API_TEST_WAIT_TIMEOUT_MS ?? 120_000);
const intervalMs = Number(process.env.API_TEST_WAIT_INTERVAL_MS ?? 2_000);
const probeUrl = `${baseUrl}/api/v1/application-overview`;

const deadline = Date.now() + timeoutMs;

async function probe() {
  const response = await fetch(probeUrl, { method: "GET" });
  return response.ok;
}

while (Date.now() < deadline) {
  try {
    if (await probe()) {
      console.log(`API test back-end ready at ${baseUrl}`);
      process.exit(0);
    }
  } catch {
    // not ready yet
  }
  await new Promise((resolve) => setTimeout(resolve, intervalMs));
}

console.error(
  `Timed out after ${timeoutMs}ms waiting for API test back-end at ${probeUrl}. ` +
    "Start it with: docker compose --profile api-test up -d " +
    "or: cd back-end && mvn spring-boot:run -Dspring-boot.run.profiles=api-test"
);
process.exit(1);
