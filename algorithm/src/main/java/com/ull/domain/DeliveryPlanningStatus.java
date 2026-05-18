package com.ull.domain;

/**
 * Possible statuses for the algorithm execution.
 */
public enum DeliveryPlanningStatus {

  /**
   * The solver found a provably optimal solution.
   */
  OPTIMAL,

  /**
   * The solver found a feasible but not provably optimal solution.
   */
  SUBOPTIMAL,

  /**
   * The problem has no feasible solution with the given constraints.
   */
  INFEASIBLE
}
