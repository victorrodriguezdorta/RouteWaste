import { describe, expect, it } from 'vitest';
import { i18n } from '@/adapter/vuejs/i18n';
import {
  resolveBackendError,
  translateBackendMessage,
} from '@/adapter/vuejs/utils/translate-backend-error';

describe('translateBackendMessage', () => {
  it('translates known backend messages to Spanish', () => {
    i18n.global.locale.value = 'es';
    expect(translateBackendMessage('Total cost exceeds maximum budget')).toBe(
      'El coste total supera el presupuesto máximo'
    );
    expect(translateBackendMessage('Failed to persist the algorithm response')).toBe(
      'No se pudo guardar la respuesta del algoritmo'
    );
  });

  it('returns unknown messages unchanged', () => {
    expect(translateBackendMessage('Some custom backend text')).toBe('Some custom backend text');
  });
});

describe('resolveBackendError', () => {
  it('combines algorithm error message and details', () => {
    i18n.global.locale.value = 'es';
    const result = resolveBackendError(
      {
        status: 'error',
        message: 'Failed to persist the algorithm response',
        details: 'Total cost exceeds maximum budget',
      },
      'algorithm'
    );
    expect(result.title).toBe('Error');
    expect(result.message).toBe(
      'No se pudo guardar la respuesta del algoritmo. El coste total supera el presupuesto máximo'
    );
  });

  it('translates simple error field payloads', () => {
    i18n.global.locale.value = 'es';
    const result = resolveBackendError({ error: 'Total cost exceeds maximum budget' }, 'generic');
    expect(result.message).toBe('El coste total supera el presupuesto máximo');
  });
});
