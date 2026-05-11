package com.ull.domain.enumerate;

/**
 * Status of a container for a given day.
 */
public enum ContainerStatus {
  /** The container is within its normal capacity. */
  CORRECT,
  /** The container has exceeded its capacity. */
  OVERFLOWED
}
