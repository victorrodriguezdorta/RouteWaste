/**
 * Resolves Vuetify semantic color names (e.g. `blue` on `v-chip`) to hex for SVG charts.
 */
const SEMANTIC_HEX_FALLBACK: Record<string, string> = {
  blue: '#2196F3',
  green: '#4CAF50',
  orange: '#FF9800',
  amber: '#FFC107',
  brown: '#795548',
  red: '#F44336',
  grey: '#9E9E9E',
  gray: '#9E9E9E',
};

export function vuetifyColorToHex(
  color: string,
  themeColors?: Record<string, string | undefined>,
): string {
  const trimmed = color.trim();
  if (trimmed.startsWith('#')) {
    return trimmed;
  }
  if (trimmed.startsWith('rgb')) {
    return trimmed;
  }

  const fromTheme = themeColors?.[trimmed];
  if (fromTheme) {
    return fromTheme;
  }

  return SEMANTIC_HEX_FALLBACK[trimmed] ?? SEMANTIC_HEX_FALLBACK.grey;
}
