import { FacilityStatus, facilityStatusIsDiscarded } from '@/domain/enumerate/facility-status';
import { FacilityType } from '@/domain/enumerate/facility-type';
import { TimeUnit } from '@/domain/enumerate/time-unit';
import { ProcessingCapacityKilogramsPerDay } from '@/domain/valueobject/capacity/processing-capacity-kilograms-per-day';
import { StorageCapacityKilograms } from '@/domain/valueobject/capacity/storage-capacity-kilograms';
import { UnloadingTime } from '@/domain/valueobject/capacity/unloading-time';
import { OpeningFixedCost } from '@/domain/valueobject/cost/opening-fixed-cost';
import { Capacity } from '@/domain/valueobject/demand/capacity';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { QuantityUnit } from '@/domain/valueobject/demand/quantity-unit';
import { WasteDemand } from '@/domain/valueobject/demand/waste-demand';
import { Location } from '@/domain/valueobject/location/location';
import { Name } from '@/domain/valueobject/name/name';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * Facility
 *
 * Represents a waste management infrastructure (aggregate root).
 *
 * Manages assigned demand, capacity and operational status. Provides
 * helpers to accept demand assignments from `Container` entities and
 * to update facility attributes used by the application layer.
 */
export class Facility {
  /**
   * Identificador único de la instalación.
   */
  readonly id: UllUUID;

  /**
   * Nombre legible de la instalación.
   */
  private name: Name;

  /**
   * Tipo de instalación (clasificación).
   */
  private facilityType: FacilityType;

  /**
   * Ubicación geográfica de la instalación.
   */
  private location: Location;

  /**
   * Capacidad de almacenamiento máxima de la instalación en kilogramos.
   */
  private storageCapacity: StorageCapacityKilograms;

  /**
   * Capacidad de procesamiento de la instalación en kilogramos por día.
   */
  private processingCapacity: ProcessingCapacityKilogramsPerDay;

  /**
   * Tiempo de descarga de camiones en la instalación en minutos.
   */
  private unloadingTime: UnloadingTime;

  /**
   * Coste fijo de apertura de la instalación.
   */
  private openingFixedCost: OpeningFixedCost;

  /**
   * Estado actual de la instalación.
   */
  private status: FacilityStatus;

  /**
   * Nivel de llenado actual de la instalación en litros por día.
   */
  private currentFillingLevel: DailyWasteDemandLitersPerDay;

  /**
   * Capacidad máxima de la instalación (legacy - mantenido para compatibilidad).
   */
  private capacity: Capacity;

  /**
   * Demanda de residuos asignada actualmente a la instalación (legacy).
   */
  private assignedWasteDemand: WasteDemand;

  /**
   * Create a new `Facility` aggregate.
   * @param name nombre de la instalación
   * @param facilityType facility classification
   * @param location facility geographic location
   * @param storageCapacity storage capacity value object in kilograms
   * @param processingCapacity processing capacity value object in kilograms per day
   * @param unloadingTime unloading time value object in minutes
   * @param openingFixedCost fixed opening cost value object
   * @param status current facility status
   * @param id optional explicit id (generated if omitted)
   * @param currentFillingLevel optional current filling level in liters per day
   * @throws Error when required parameters are missing
   */
  constructor(
    name: Name,
    facilityType: FacilityType,
    location: Location,
    storageCapacity: StorageCapacityKilograms,
    processingCapacity: ProcessingCapacityKilogramsPerDay,
    unloadingTime: UnloadingTime,
    openingFixedCost: OpeningFixedCost,
    status: FacilityStatus,
    id?: UllUUID,
    currentFillingLevel?: DailyWasteDemandLitersPerDay
  ) {
    if (!name) throw new Error('Facility name is not defined');
    if (!facilityType) throw new Error('Facility type is not defined');
    if (!location) throw new Error('Facility location is not defined');
    if (!storageCapacity) throw new Error('Facility storage capacity is not defined');
    if (!processingCapacity) throw new Error('Facility processing capacity is not defined');
    if (!unloadingTime) throw new Error('Facility unloading time is not defined');
    if (!openingFixedCost) throw new Error('Opening fixed cost is not defined');
    if (!status) throw new Error('Facility status is not defined');

    this.id = id ?? UllUUID.random();
    this.name = name;
    this.facilityType = facilityType;
    this.location = location;
    this.storageCapacity = storageCapacity;
    this.processingCapacity = processingCapacity;
    this.unloadingTime = unloadingTime;
    this.openingFixedCost = openingFixedCost;
    this.status = status;
    this.currentFillingLevel = currentFillingLevel ?? new DailyWasteDemandLitersPerDay(0);
    // Legacy capacity - no longer needed with new VO architecture
    this.capacity = undefined as any;
    this.assignedWasteDemand = WasteDemand.withDefaultUnit(0);
  }


  /**
   * Devuelve el identificador único de la instalación.
   * @returns El identificador único de la instalación.
   */
  getId(): UllUUID { return this.id; }

  /**
   * Devuelve el nombre de la instalación.
   *
   * @returns El nombre de la instalación
   */
  getName(): Name { return this.name; }

  /**
   * Actualiza el nombre de la instalación.
   *
   * @param name Nuevo nombre de la instalación
   */
  updateName(name: Name): void {
    if (!name) throw new Error('Facility name is not defined');
    this.name = name;
  }

  /**
   * Assign additional daily waste demand to the facility (liters/day).
   * Mirrors backend behavior: updates the `currentFillingLevel` VO.
   * @param demand daily demand value object to add
   * @throws Error when facility is discarded
   */
  assignWasteDemand(demand: DailyWasteDemandLitersPerDay): void {
    if (facilityStatusIsDiscarded(this.status)) throw new Error('Facility is discarded and cannot receive assignments');
    const newTotal = this.currentFillingLevel.add(demand);
    this.currentFillingLevel = newTotal;
  }


  /**
   * Devuelve el tipo de instalación.
   * @returns El tipo de instalación.
   */
  getFacilityType(): FacilityType { return this.facilityType; }

  /**
   * Devuelve la capacidad de almacenamiento máxima de la instalación.
   * @returns La capacidad de almacenamiento en kilogramos.
   */
  getStorageCapacity(): StorageCapacityKilograms { return this.storageCapacity; }

  /**
   * Devuelve la capacidad de procesamiento de la instalación.
   * @returns La capacidad de procesamiento en kilogramos por día.
   */
  getProcessingCapacity(): ProcessingCapacityKilogramsPerDay { return this.processingCapacity; }

  /**
   * Devuelve el tiempo de descarga de la instalación.
   * @returns El tiempo de descarga en minutos.
   */
  getUnloadingTime(): UnloadingTime { return this.unloadingTime; }

  /**
   * Devuelve el nivel de llenado actual de la instalación.
   * @returns El nivel de llenado actual en litros por día.
   */
  getCurrentFillingLevel(): DailyWasteDemandLitersPerDay { return this.currentFillingLevel; }

  /**
   * Devuelve la capacidad máxima de la instalación (legacy).
   * @returns La capacidad máxima de la instalación.
   */
  getCapacity(): Capacity { return this.capacity; }

  /**
   * Devuelve la ubicación de la instalación.
   * @returns La ubicación de la instalación.
   */
  getLocation(): Location { return this.location; }

  /**
   * Devuelve el coste fijo de apertura de la instalación.
   * @returns El coste fijo de apertura de la instalación.
   */
  getOpeningFixedCost(): OpeningFixedCost { return this.openingFixedCost; }

  /**
   * Devuelve el estado actual de la instalación.
   * @returns El estado actual de la instalación.
   */
  getStatus(): FacilityStatus { return this.status; }

  /**
   * Devuelve la demanda de residuos actualmente asignada a la instalación (legacy).
   * @returns La demanda de residuos asignada.
   */
  getAssignedWasteDemand(): WasteDemand { return this.assignedWasteDemand; }


  /**
   * Actualiza el tipo de instalación.
   * @param ft Nuevo tipo de instalación.
   */
  updateFacilityType(ft: FacilityType): void {
    if (!ft) throw new Error('Facility type is not defined');
    this.facilityType = ft;
  }

  /**
   * Actualiza la ubicación de la instalación.
   * @param loc Nueva ubicación de la instalación.
   */
  updateLocation(loc: Location): void {
    if (!loc) throw new Error('Facility location is not defined');
    this.location = loc;
  }

  /**
   * Actualiza la capacidad de almacenamiento de la instalación.
   * @param capacity Nueva capacidad de almacenamiento.
   */
  updateStorageCapacity(capacity: StorageCapacityKilograms): void {
    if (!capacity) throw new Error('Facility storage capacity is not defined');
    this.storageCapacity = capacity;
    this.capacity = this.createLegacyCapacity();
  }

  /**
   * Creates a legacy Capacity object from current storage capacity.
   * This is a helper method for backward compatibility.
   * @returns A new Capacity instance based on storage capacity
   * @private
   */
  private createLegacyCapacity(): Capacity {
    // Storage capacity in kilograms, converted to tons/day as default legacy format
    const quantityUnit = new QuantityUnit('tons');
    const value = this.storageCapacity.getKilograms() / 1000; // Convert kg to tons
    return new Capacity(value, quantityUnit, TimeUnit.DAY);
  }

  /**
   * Actualiza la capacidad de procesamiento de la instalación.
   * @param capacity Nueva capacidad de procesamiento.
   */
  updateProcessingCapacity(capacity: ProcessingCapacityKilogramsPerDay): void {
    if (!capacity) throw new Error('Facility processing capacity is not defined');
    this.processingCapacity = capacity;
  }

  /**
   * Actualiza el tiempo de descarga de la instalación.
   * @param time Nuevo tiempo de descarga.
   */
  updateUnloadingTime(time: UnloadingTime): void {
    if (!time) throw new Error('Facility unloading time is not defined');
    this.unloadingTime = time;
  }

  /**
   * Actualiza el nivel de llenado actual de la instalación.
   * @param fillingLevel Nuevo nivel de llenado.
   */
  updateCurrentFillingLevel(fillingLevel: DailyWasteDemandLitersPerDay): void {
    if (!fillingLevel) throw new Error('Facility current filling level is not defined');
    this.currentFillingLevel = fillingLevel;
  }

  /**
   * Actualiza la capacidad máxima de la instalación (legacy).
   * @param cap Nueva capacidad máxima.
   */
  updateCapacity(cap: Capacity): void {
    if (!cap) throw new Error('Facility capacity is not defined');
    this.capacity = cap;
  }

  /**
   * Actualiza el coste fijo de apertura de la instalación.
   * @param cost Nuevo coste fijo de apertura.
   */
  updateOpeningFixedCost(cost: OpeningFixedCost): void {
    if (!cost) throw new Error('Opening fixed cost is not defined');
    this.openingFixedCost = cost;
  }

  /**
   * Actualiza el estado de la instalación.
   * @param status Nuevo estado de la instalación.
   */
  updateStatus(status: FacilityStatus): void {
    if (!status) throw new Error('Facility status is not defined');
    this.status = status;
  }


  /**
   * Compara si la instalación es igual a otra por id.
   * @param other Otra instalación a comparar.
   * @returns True si los ids son iguales, false en caso contrario.
   */
  equals(other: unknown): boolean {
    if (this === other) return true;
    if (!(other instanceof Facility)) return false;
    return this.id.equals(other.id);
  }

  /**
   * Devuelve una representación legible de la instalación para depuración.
   * @returns Cadena representando la instalación.
   */
  toString(): string {
    return `Facility={id=${this.id}, name=${this.name}, type=${this.facilityType}, location=${this.location}, capacity=${this.capacity}, assignedDemand=${this.assignedWasteDemand}, openingCost=${this.openingFixedCost}, status=${this.status}`;
  }
}
