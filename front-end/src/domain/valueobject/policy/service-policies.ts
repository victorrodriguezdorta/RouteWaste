/**
 * ServicePolicies
 *
 * Aggregates optional policy parameters for services.
 */
export class ServicePolicies {
  /**
   * Error message for negative policy values.
   */
  private static readonly ERROR_NEGATIVE = 'Policy values cannot be negative';

  /**
   * Maximum service distance in meters (nullable).
   */
  readonly maxServiceDistance?: number | null;

  /**
   * Maximum service time in minutes (nullable).
   */
  readonly maxServiceTime?: number | null;

  /**
   * Maximum infrastructure count (nullable).
   */
  readonly maxInfrastructureCount?: number | null;

  /**
   * Maximum emissions allowed (nullable).
   */
  readonly maxEmissions?: number | null;

  /**
   * Create policy container with optional numeric limits.
   * @param maxServiceDistance maximum distance in meters (nullable)
   * @param maxServiceTime maximum time in minutes (nullable)
   * @param maxInfrastructureCount maximum infrastructure count (nullable)
   * @param maxEmissions maximum emissions allowed (nullable)
   */
  constructor(maxServiceDistance?: number | null, maxServiceTime?: number | null, maxInfrastructureCount?: number | null, maxEmissions?: number | null) {
    this.validateNonNegative(maxServiceDistance);
    this.validateNonNegative(maxServiceTime);
    this.validateNonNegative(maxInfrastructureCount);
    this.validateNonNegative(maxEmissions);
    this.maxServiceDistance = maxServiceDistance ?? null;
    this.maxServiceTime = maxServiceTime ?? null;
    this.maxInfrastructureCount = maxInfrastructureCount ?? null;
    this.maxEmissions = maxEmissions ?? null;
  }

  /**
   * Validate that a provided numeric policy is non-negative.
   * @param value the value to validate
   * @throws Error if value is negative
   */
  private validateNonNegative(value?: number | null) {
    if (value != null && value < 0) throw new Error(ServicePolicies.ERROR_NEGATIVE);
  }

  /**
   * Return true when any policy value is present.
   * @returns true if any policy is set, false otherwise
   */
  hasAnyPolicy(): boolean {
    return this.maxServiceDistance != null || this.maxServiceTime != null || this.maxInfrastructureCount != null || this.maxEmissions != null;
  }

  /**
   * Validate a proposed service assignment against distance/time policies.
   * @param serviceDistance distance to check
   * @param serviceTime time to check
   * @returns null when valid, otherwise a human-readable violation message
   */
  validateServiceAssignment(serviceDistance: number, serviceTime: number): string | null {
    if (this.maxServiceDistance != null && serviceDistance > this.maxServiceDistance) {
      return `Service distance ${serviceDistance} exceeds maximum allowed ${this.maxServiceDistance}`;
    }
    if (this.maxServiceTime != null && serviceTime > this.maxServiceTime) {
      return `Service time ${serviceTime} minutes exceeds maximum allowed ${this.maxServiceTime} minutes`;
    }
    return null;
  }

  /**
   * Check compliance against all available policies.
   * @param serviceDistance the service distance to check
   * @param serviceTime the service time to check
   * @param infrastructureCount the infrastructure count to check
   * @param emissions the emissions to check
   * @returns true when all applicable policies are satisfied, false otherwise
   */
  isCompliant(serviceDistance: number, serviceTime: number, infrastructureCount: number, emissions: number): boolean {
    if (this.maxServiceDistance != null && serviceDistance > this.maxServiceDistance) return false;
    if (this.maxServiceTime != null && serviceTime > this.maxServiceTime) return false;
    if (this.maxInfrastructureCount != null && infrastructureCount > this.maxInfrastructureCount) return false;
    if (this.maxEmissions != null && emissions > this.maxEmissions) return false;
    return true;
  }

  /**
   * Equality by value of all fields.
   * @param other the object to compare with
   * @returns true if all fields are equal, false otherwise
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof ServicePolicies)) return false;
    return this.maxServiceDistance === other.maxServiceDistance && this.maxServiceTime === other.maxServiceTime && this.maxInfrastructureCount === other.maxInfrastructureCount && this.maxEmissions === other.maxEmissions;
  }

  /**
   * Human-readable representation.
   * @returns a string representation of the ServicePolicies object
   */
  toString(): string {
    return `ServicePolicies={maxServiceDistance=${this.maxServiceDistance}, maxServiceTime=${this.maxServiceTime}, maxInfrastructureCount=${this.maxInfrastructureCount}, maxEmissions=${this.maxEmissions}}`;
  }
}
