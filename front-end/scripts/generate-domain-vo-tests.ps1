$ErrorActionPreference = 'Stop'
$root = Join-Path (Join-Path $PSScriptRoot '..') 'src\test\domain\valueobject'

$specs = @(
  @{ Subdir = 'demand'; File = 'container-capacity-liters'; Import = '@/domain/valueobject/demand/container-capacity-liters'; Class = 'ContainerCapacityLiters'; Read = '.getLiters()'; NegativeMsg = 'Container capacity cannot be negative' },
  @{ Subdir = 'demand'; File = 'daily-waste-demand-liters-per-day'; Import = '@/domain/valueobject/demand/daily-waste-demand-liters-per-day'; Class = 'DailyWasteDemandLitersPerDay'; Read = '.getLitersPerDay()'; NegativeMsg = 'Daily waste demand cannot be negative'; Extra = @'
  it('should add two demands', () => {
    const a = new DailyWasteDemandLitersPerDay(10);
    const b = new DailyWasteDemandLitersPerDay(5);
    expect(a.add(b).getLitersPerDay()).toBe(15);
  });
'@ },
  @{ Subdir = 'capacity'; File = 'collected-volume-liters'; Import = '@/domain/valueobject/capacity/collected-volume-liters'; Class = 'CollectedVolumeLiters'; Create = 'CollectedVolumeLiters.fromLiters'; Read = '.getLiters()'; NegativeMsg = 'Collected volume cannot be negative' },
  @{ Subdir = 'capacity'; File = 'collected-weight-kilograms'; Import = '@/domain/valueobject/capacity/collected-weight-kilograms'; Class = 'CollectedWeightKilograms'; Create = 'CollectedWeightKilograms.fromKilograms'; Read = '.getKilograms()'; NegativeMsg = 'Collected weight cannot be negative' },
  @{ Subdir = 'capacity'; File = 'processing-capacity-kilograms-per-day'; Import = '@/domain/valueobject/capacity/processing-capacity-kilograms-per-day'; Class = 'ProcessingCapacityKilogramsPerDay'; Read = '.getKilogramsPerDay()'; NegativeMsg = 'Processing capacity cannot be negative' },
  @{ Subdir = 'capacity'; File = 'storage-capacity-kilograms'; Import = '@/domain/valueobject/capacity/storage-capacity-kilograms'; Class = 'StorageCapacityKilograms'; Read = '.getKilograms()'; NegativeMsg = 'Storage capacity cannot be negative' },
  @{ Subdir = 'capacity'; File = 'unloading-time'; Import = '@/domain/valueobject/capacity/unloading-time'; Class = 'UnloadingTime'; Read = '.getMinutes()'; NegativeMsg = 'Unloading time cannot be negative' },
  @{ Subdir = 'capacity'; File = 'vehicle-capacity-kilograms'; Import = '@/domain/valueobject/capacity/vehicle-capacity-kilograms'; Class = 'VehicleCapacityKilograms'; Read = '.getKilograms()'; NegativeMsg = 'Vehicle capacity cannot be negative' },
  @{ Subdir = 'capacity'; File = 'vehicle-capacity-liters'; Import = '@/domain/valueobject/capacity/vehicle-capacity-liters'; Class = 'VehicleCapacityLiters'; Read = '.getLiters()'; NegativeMsg = 'Vehicle capacity cannot be negative' },
  @{ Subdir = 'location'; File = 'distance'; Import = '@/domain/valueobject/location/distance'; Class = 'Distance'; Create = 'Distance.fromMeters'; Read = '.toMeters()'; NegativeMsg = 'Distance cannot be negative' },
  @{ Subdir = 'location'; File = 'service-radius'; Import = '@/domain/valueobject/location/service-radius'; Class = 'ServiceRadius'; Read = '.value'; NegativeMsg = 'Service radius cannot be negative' },
  @{ Subdir = 'location'; File = 'service-time'; Import = '@/domain/valueobject/location/service-time'; Class = 'ServiceTime'; Read = '.getValue()'; NegativeMsg = 'Service time cannot be negative' }
)

foreach ($s in $specs) {
  $dir = Join-Path $root $s.Subdir
  New-Item -ItemType Directory -Force -Path $dir | Out-Null
  $create = if ($s.Create) { $s.Create } else { "new $($s.Class)" }
  $extra = if ($s.Extra) { $s.Extra } else { '' }
  $content = @"
import { describe, expect, it } from 'vitest';
import { $($s.Class) } from '$($s.Import)';

describe('$($s.Class)', () => {
  it('should create with zero and positive values', () => {
    expect($create(0)$($s.Read)).toBe(0);
    expect($create(42.5)$($s.Read)).toBe(42.5);
  });

  it('should reject negative values', () => {
    expect(() => $create(-1)).toThrow('$($s.NegativeMsg)');
  });

  it('should compare equality', () => {
    const left = $create(10);
    const right = $create(10);
    const other = $create(20);
    expect(left.equals(right)).toBe(true);
    expect(left.equals(other)).toBe(false);
    expect(left.equals(null)).toBe(false);
  });
$extra});
"@
  $path = Join-Path $dir "$($s.File).test.ts"
  Set-Content -Path $path -Value $content -Encoding UTF8
}

Write-Host "Generated $($specs.Count) VO test files"
