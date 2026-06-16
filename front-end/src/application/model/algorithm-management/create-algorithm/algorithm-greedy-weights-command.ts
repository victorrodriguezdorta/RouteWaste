/**
 * Distance and fill weights used by the greedy scoring of the algorithm.
 * Both weights must add up to 1.
 */
export interface AlgorithmGreedyWeightsCommand {
  distanceWeight: number;
  fillWeight: number;
}
