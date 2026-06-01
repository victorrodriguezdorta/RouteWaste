import {
  InfrastructurePlanValidityState,
  infrastructurePlanValidityStateColor,
  infrastructurePlanValidityStateFromString,
} from './infrastructure-plan-validity-state';

const INFRASTRUCTURE_PLAN_EXECUTION_I18N_PREFIX = 'infrastructurePlan.list.table.execution';

/**
 * Lifecycle state of an infrastructure plan while the delivery algorithm runs.
 */
export enum InfrastructurePlanExecutionState {
  RUNNING = 'RUNNING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
}

/**
 * Parses API execution state strings into {@link InfrastructurePlanExecutionState}.
 * Legacy or missing values default to COMPLETED.
 */
export function infrastructurePlanExecutionStateFromString(
  raw: string | undefined | null,
): InfrastructurePlanExecutionState {
  if (raw == null || raw.trim() === '') {
    return InfrastructurePlanExecutionState.COMPLETED;
  }
  const normalized = raw.trim().toUpperCase();
  if (normalized === InfrastructurePlanExecutionState.RUNNING) {
    return InfrastructurePlanExecutionState.RUNNING;
  }
  if (normalized === InfrastructurePlanExecutionState.FAILED) {
    return InfrastructurePlanExecutionState.FAILED;
  }
  return InfrastructurePlanExecutionState.COMPLETED;
}

/** Locale key segment under execution.* in i18n JSON. */
export function infrastructurePlanExecutionStateLocaleKey(
  state: InfrastructurePlanExecutionState,
): string {
  switch (state) {
    case InfrastructurePlanExecutionState.RUNNING:
      return 'running';
    case InfrastructurePlanExecutionState.FAILED:
      return 'failed';
    case InfrastructurePlanExecutionState.COMPLETED:
    default:
      return 'completed';
  }
}

/**
 * Returns the translated label for an infrastructure plan execution state.
 * COMPLETED has no dedicated list label; callers typically show validity instead.
 */
export function infrastructurePlanExecutionStateLabel(
  t: (key: string) => string,
  state: InfrastructurePlanExecutionState | string | null | undefined,
): string | null {
  const value = typeof state === 'string'
    ? infrastructurePlanExecutionStateFromString(state)
    : (state ?? InfrastructurePlanExecutionState.COMPLETED);

  if (value === InfrastructurePlanExecutionState.COMPLETED) {
    return null;
  }

  return t(
    `${INFRASTRUCTURE_PLAN_EXECUTION_I18N_PREFIX}.${infrastructurePlanExecutionStateLocaleKey(value)}`,
  );
}

/**
 * Returns the Vuetify color for an infrastructure plan execution state chip.
 * COMPLETED has no dedicated chip color; use validity color instead.
 */
export function infrastructurePlanExecutionStateColor(
  state: InfrastructurePlanExecutionState | string,
): string | null {
  const value = typeof state === 'string'
    ? infrastructurePlanExecutionStateFromString(state)
    : state;

  switch (value) {
    case InfrastructurePlanExecutionState.RUNNING:
      return 'warning';
    case InfrastructurePlanExecutionState.FAILED:
      return 'error';
    case InfrastructurePlanExecutionState.COMPLETED:
    default:
      return null;
  }
}

/**
 * Resolves chip color for the combined plan status shown in list/dashboard views.
 * Mirrors list UI rules: running validity, then failed execution, otherwise validity.
 */
export function infrastructurePlanPlanStatusChipColor(
  validityState: InfrastructurePlanValidityState | string,
  executionState?: InfrastructurePlanExecutionState | string | null,
): string {
  const validity = typeof validityState === 'string'
    ? infrastructurePlanValidityStateFromString(validityState)
    : validityState;

  if (validity === InfrastructurePlanValidityState.RUNNING) {
    return infrastructurePlanValidityStateColor(validity);
  }

  if (executionState != null) {
    const execution = typeof executionState === 'string'
      ? infrastructurePlanExecutionStateFromString(executionState)
      : executionState;
    if (execution === InfrastructurePlanExecutionState.FAILED) {
      return infrastructurePlanExecutionStateColor(execution)!;
    }
  }

  return infrastructurePlanValidityStateColor(validity);
}
