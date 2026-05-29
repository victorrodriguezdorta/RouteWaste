import { i18n } from '../i18n';

export type StoreErrorContext =
  | 'vehicle'
  | 'facility'
  | 'container'
  | 'algorithm'
  | 'infrastructurePlan'
  | 'generic';

export interface ResolvedBackendError {
  title: string;
  message: string;
}

type BackendErrorPayload = {
  kind?: string;
  error?: unknown;
  message?: unknown;
  details?: unknown;
  status?: string;
};

/** Exact English messages returned by the backend → i18n key under common.errors.backend */
const BACKEND_MESSAGE_KEYS: Record<string, string> = {
  'Total cost exceeds maximum budget': 'common.errors.backend.totalCostExceeded',
  'Planning period is not defined': 'common.errors.backend.planningPeriodNotDefined',
  'Maximum budget is not defined': 'common.errors.backend.maximumBudgetNotDefined',
  'Infrastructure plan validity state is not defined':
    'common.errors.backend.validityStateNotDefined',
  'Service assignment is invalid': 'common.errors.backend.invalidServiceAssignment',
  'Facility is discarded and cannot receive assignments':
    'common.errors.backend.facilityDiscarded',
  'Facility storage capacity exceeded': 'common.errors.backend.facilityStorageCapacityExceeded',
  'Validation failed': 'common.errors.backend.validationFailed',
  'Resource not found': 'common.errors.backend.resourceNotFound',
  'An internal error occurred. Please try again later.':
    'common.errors.backend.internalError',
  'Invalid request format': 'common.errors.backend.invalidRequestFormat',
  'Failed to send algorithm to runner': 'common.errors.backend.algorithmRunnerFailed',
  'Failed to parse algorithm response': 'common.errors.backend.algorithmParseFailed',
  'Failed to persist the algorithm response': 'common.errors.backend.algorithmPersistFailed',
  'Failed to serialize the processed algorithm payload':
    'common.errors.backend.algorithmSerializeFailed',
  'Failed to serialize the algorithm execution request':
    'common.errors.backend.algorithmSerializeRequestFailed',
  'Failed to parse the algorithm response': 'common.errors.backend.algorithmParseFailed',
  'Algorithm executed and persisted successfully':
    'common.errors.backend.algorithmPersistSuccess',
  'facilitiesWithVehicles is required': 'common.errors.backend.facilitiesWithVehiclesRequired',
  'Each facility selection must be defined':
    'common.errors.backend.facilitySelectionRequired',
  'A required identifier list is missing': 'common.errors.backend.requiredIdsMissing',
  'At least one facility with vehicles must be selected':
    'common.errors.backend.atLeastOneFacilityWithVehicles',
  'At least one container must be selected': 'common.errors.backend.atLeastOneContainer',
  'Each selected facility must include at least one vehicle':
    'common.errors.backend.eachFacilityNeedsVehicle',
  'Algorithm response is required': 'common.errors.backend.algorithmResponseRequired',
  'Facility node is required': 'common.errors.backend.facilityNodeRequired',
  'Vehicle node is required': 'common.errors.backend.vehicleNodeRequired',
  "Facility must be either a UUID string or an object with 'id' field":
    'common.errors.backend.facilityInvalidFormat',
  "Vehicle must be either a UUID string or an object with 'id' field":
    'common.errors.backend.vehicleInvalidFormat',
  'File is required': 'common.errors.backend.fileRequired',
  'File must not be empty': 'common.errors.backend.fileEmpty',
  'Invalid JSON file': 'common.errors.backend.invalidJsonFile',
};

const PREFIX_MESSAGE_KEYS: Array<{ prefix: string; key: string }> = [
  { prefix: 'Facility not found in MongoDB: ', key: 'common.errors.backend.facilityNotFoundInDb' },
  { prefix: 'Vehicle not found in MongoDB: ', key: 'common.errors.backend.vehicleNotFoundInDb' },
  {
    prefix: 'Facility not found in algorithm clusters: ',
    key: 'common.errors.backend.facilityNotFoundInClusters',
  },
  {
    prefix: 'Invalid JSON file: ',
    key: 'common.errors.backend.invalidJsonFileWithDetail',
  },
];

const KIND_FALLBACK_KEYS: Record<StoreErrorContext, Record<string, string>> = {
  vehicle: {
    ValidationError: 'common.errors.store.invalidVehicleData',
    ApiError: 'common.errors.store.invalidVehicleData',
    NotFoundError: 'common.errors.store.vehicleNotFound',
    ConflictError: 'common.errors.store.vehicleConflict',
  },
  facility: {
    ValidationError: 'common.errors.store.invalidFacilityData',
    ApiError: 'common.errors.store.invalidFacilityData',
    NotFoundError: 'common.errors.store.facilityNotFound',
    ConflictError: 'common.errors.store.facilityConflict',
    CapacityExceededError: 'common.errors.store.facilityCapacityExceeded',
  },
  container: {
    ValidationError: 'common.errors.store.invalidContainerData',
    ApiError: 'common.errors.store.invalidContainerData',
    NotFoundError: 'common.errors.store.containerNotFound',
    ConflictError: 'common.errors.store.containerConflict',
  },
  algorithm: {
    ValidationError: 'common.errors.store.invalidAlgorithmParameters',
    NotFoundError: 'common.errors.store.algorithmResourcesNotFound',
    ConflictError: 'common.errors.store.algorithmConflict',
    UnexpectedError: 'common.errors.store.algorithmUnexpected',
  },
  infrastructurePlan: {
    ValidationError: 'common.errors.store.invalidInfrastructurePlanData',
    NotFoundError: 'common.errors.store.infrastructurePlanNotFound',
    ConflictError: 'common.errors.store.infrastructurePlanConflict',
    BudgetExceededError: 'common.errors.backend.totalCostExceeded',
  },
  generic: {},
};

function t(key: string): string {
  const translated = i18n.global.t(key);
  return translated === key ? key : String(translated);
}

function normalizeText(value: unknown): string | undefined {
  if (typeof value === 'string') {
    const trimmed = value.trim();
    return trimmed.length > 0 ? trimmed : undefined;
  }
  if (value && typeof value === 'object') {
    const record = value as Record<string, unknown>;
    if (typeof record.value === 'string') {
      return normalizeText(record.value);
    }
    if (typeof record.message === 'string') {
      return normalizeText(record.message);
    }
  }
  return undefined;
}

function translateRawMessage(raw: string): string {
  const exactKey = BACKEND_MESSAGE_KEYS[raw];
  if (exactKey) {
    return t(exactKey);
  }

  for (const rule of PREFIX_MESSAGE_KEYS) {
    if (raw.startsWith(rule.prefix)) {
      const suffix = raw.slice(rule.prefix.length).trim();
      if (rule.key === 'common.errors.backend.invalidJsonFileWithDetail') {
        return t(rule.key).replace('{detail}', suffix);
      }
      return t(rule.key);
    }
  }

  return raw;
}

function extractDetailMessages(details: unknown): string[] {
  if (typeof details === 'string') {
    return details.trim() ? [translateRawMessage(details.trim())] : [];
  }

  if (!Array.isArray(details)) {
    return [];
  }

  const messages: string[] = [];
  for (const item of details) {
    if (!item || typeof item !== 'object') {
      continue;
    }
    const field = (item as { field?: string }).field;
    const issue =
      (item as { issue?: string }).issue ?? (item as { message?: string }).message;
    if (!issue) {
      continue;
    }
    const translatedIssue = translateRawMessage(issue);
    if (field && field !== 'general') {
      messages.push(`${field}: ${translatedIssue}`);
    } else {
      messages.push(translatedIssue);
    }
  }
  return messages;
}

function extractPayloadMessages(error: BackendErrorPayload): string[] {
  const messages: string[] = [];

  const primary = normalizeText(error.message);
  if (primary) {
    messages.push(translateRawMessage(primary));
  }

  const errorField = normalizeText(error.error);
  if (errorField && errorField !== 'ValidationError') {
    messages.push(translateRawMessage(errorField));
  }

  messages.push(...extractDetailMessages(error.details));

  return [...new Set(messages.filter(Boolean))];
}

/**
 * Translates a single backend message (e.g. algorithm success/error text).
 */
export function translateBackendMessage(raw: string | undefined | null): string {
  if (!raw || !raw.trim()) {
    return '';
  }
  return translateRawMessage(raw.trim());
}

/**
 * Resolves notification title and message for API / domain errors.
 */
export function resolveBackendError(
  error: unknown,
  context: StoreErrorContext = 'generic'
): ResolvedBackendError {
  const payload = (error ?? {}) as BackendErrorPayload;
  const messages = extractPayloadMessages(payload);

  let message: string;
  if (messages.length > 0) {
    message = messages.join('. ');
  } else {
    const kind = payload.kind ?? '';
    const kindKey = KIND_FALLBACK_KEYS[context]?.[kind];
    if (kindKey) {
      message = t(kindKey);
    } else if (context === 'algorithm') {
      message = t('common.errors.store.algorithmUnexpected');
    } else {
      message = t('common.errors.unexpected');
    }
  }

  const title =
    payload.kind === 'ValidationError' || payload.error === 'ValidationError'
      ? t('common.errors.titles.validation')
      : t('common.errors.titles.error');

  return { title, message };
}
