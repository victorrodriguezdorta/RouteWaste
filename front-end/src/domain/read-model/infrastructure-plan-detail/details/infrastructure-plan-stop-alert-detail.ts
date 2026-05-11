/**
 * Read-only alert attached to a stop.
 */
export class InfrastructurePlanStopAlertDetail {
  constructor(
    public readonly type: string,
    public readonly message: string,
    public readonly value: number | null = null,
  ) {}
}
