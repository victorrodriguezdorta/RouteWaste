import { execSync, spawnSync } from 'node:child_process';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');

execSync('node scripts/build-front-end-for-docker.mjs', {
  cwd: root,
  stdio: 'inherit',
});

console.log('Building algorithm image...');
const algorithmBuild = spawnSync('docker', ['compose', 'build', 'algorithm'], {
  cwd: root,
  stdio: 'inherit',
});
if (algorithmBuild.status !== 0) {
  process.exit(algorithmBuild.status ?? 1);
}

const result = spawnSync(
  'docker',
  ['compose', '--profile', 'full', 'up', '-d', '--build'],
  {
    cwd: root,
    stdio: 'inherit',
    env: { ...process.env, FRONTEND_DOCKERFILE: 'Dockerfile.dist' },
  },
);

process.exit(result.status ?? 1);
