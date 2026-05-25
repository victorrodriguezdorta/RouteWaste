/**
 * Converts an enum constant (e.g. PAPER_CARDBOARD) to a camelCase locale key segment (paperCardboard).
 */
export function enumToLocaleKey(enumValue: string): string {
  const parts = enumValue.toLowerCase().split('_');
  return parts
    .map((part, index) => (index === 0 ? part : part.charAt(0).toUpperCase() + part.slice(1)))
    .join('');
}
