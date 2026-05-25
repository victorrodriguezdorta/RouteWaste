/**
 * Result object returned by the backend after attempting to execute
 * and persist the infrastructure plan.
 */
export interface CreateAlgorithmResult {
    /** Backend execution status. */
    status: 'success' | 'error';

    /** Human-readable backend message. */
    message: string;

    /** Optional backend error details. */
    details?: string;

    /** Persisted infrastructure plan identifier when the operation succeeds. */
    infrastructurePlanId?: string;
}
