export interface TimelineColumn {
  /** Unique sortable value combining day and time of day. */
  key: number;
  day: number;
  minutes: number;
}
