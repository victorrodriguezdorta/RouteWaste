import { execSync } from 'node:child_process';
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const envPath = resolve(root, '.env');
const frontEndDir = resolve(root, 'front-end');

function readGithubToken() {
  let token = process.env.GITHUB_TOKEN?.trim();
  if (token) {
    return token;
  }

  try {
    const env = readFileSync(envPath, 'utf8');
    const match = env.match(/^GITHUB_TOKEN=(.+)$/m);
    token = match?.[1]?.trim();
  } catch {
    // .env missing
  }

  if (!token) {
    throw new Error(
      'Set GITHUB_TOKEN in .env (copy .env.example). Required for @ull-tfg npm packages.',
    );
  }

  return token;
}

const token = readGithubToken();
writeFileSync(
  resolve(frontEndDir, '.npmrc'),
  `@ull-tfg:registry=https://npm.pkg.github.com/\n//npm.pkg.github.com/:_authToken=${token}\n`,
);

execSync('npm install', { cwd: frontEndDir, stdio: 'inherit' });
execSync('npm run build', { cwd: frontEndDir, stdio: 'inherit' });

console.log('Front-end built in front-end/dist');
