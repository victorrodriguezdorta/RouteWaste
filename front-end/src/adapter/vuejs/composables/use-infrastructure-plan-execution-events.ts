import {
  infrastructurePlanExecutionStateFromString,
  InfrastructurePlanExecutionState,
} from '@/domain/enumerate/infrastructure-plan-execution-state';
import { onMounted, onUnmounted, ref } from 'vue';

export interface InfrastructurePlanExecutionEventPayload {
  planId: string;
  executionState: InfrastructurePlanExecutionState;
  failureReason?: string | null;
}

const SSE_EVENT_PLAN_UPDATED = 'plan-updated';
const RECONNECT_BASE_MS = 3000;
const RECONNECT_MAX_MS = 30000;

/**
 * Subscribes to backend SSE notifications for infrastructure plan execution state changes.
 */
export function useInfrastructurePlanExecutionEvents(
  onPlanUpdated: (event: InfrastructurePlanExecutionEventPayload) => void,
) {
  const eventSource = ref<EventSource | null>(null);
  let reconnectAttempts = 0;
  let reconnectTimer: ReturnType<typeof setTimeout> | undefined;

  const buildUrl = (): string => {
    const base = import.meta.env.VITE_APP_API_URL ?? '';
    return `${base}infrastructure-plans/execution-events`;
  };

  const parsePayload = (raw: string): InfrastructurePlanExecutionEventPayload | undefined => {
    try {
      const data = JSON.parse(raw) as {
        planId?: string;
        executionState?: string;
        failureReason?: string | null;
      };
      if (!data.planId) {
        return undefined;
      }
      return {
        planId: data.planId,
        executionState: infrastructurePlanExecutionStateFromString(data.executionState),
        failureReason: data.failureReason ?? null,
      };
    } catch {
      return undefined;
    }
  };

  const scheduleReconnect = () => {
    if (reconnectTimer) {
      return;
    }
    const delay = Math.min(RECONNECT_BASE_MS * 2 ** reconnectAttempts, RECONNECT_MAX_MS);
    reconnectAttempts += 1;
    reconnectTimer = setTimeout(() => {
      reconnectTimer = undefined;
      connect();
    }, delay);
  };

  const connect = () => {
    if (eventSource.value) {
      eventSource.value.close();
    }
    const source = new EventSource(buildUrl());
    eventSource.value = source;

    source.addEventListener(SSE_EVENT_PLAN_UPDATED, (message: MessageEvent<string>) => {
      const payload = parsePayload(message.data);
      if (!payload) {
        return;
      }
      if (
        payload.executionState === InfrastructurePlanExecutionState.COMPLETED
        || payload.executionState === InfrastructurePlanExecutionState.FAILED
      ) {
        onPlanUpdated(payload);
      }
    });

    source.onopen = () => {
      reconnectAttempts = 0;
    };

    source.onerror = () => {
      source.close();
      eventSource.value = null;
      scheduleReconnect();
    };
  };

  const disconnect = () => {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer);
      reconnectTimer = undefined;
    }
    if (eventSource.value) {
      eventSource.value.close();
      eventSource.value = null;
    }
  };

  onMounted(() => {
    connect();
  });

  onUnmounted(() => {
    disconnect();
  });

  return { disconnect };
}
