/** Datum for ull-tfg-vue PieChart (`name` + `value`). */
export type PieChartDatum = Record<string, string | number> & {
  name: string;
  value: number;
};
