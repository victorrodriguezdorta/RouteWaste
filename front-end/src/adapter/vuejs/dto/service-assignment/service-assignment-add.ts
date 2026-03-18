import { Container } from '@/domain/entity/container';
import { Facility } from '@/domain/entity/facility';
import { ServiceAssignment } from '@/domain/entity/service-assignment';
import { TimeUnit } from '@/domain/enumerate/time-unit';
import { Currency } from '@/domain/valueobject/cost/currency';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { QuantityUnit } from '@/domain/valueobject/demand/quantity-unit';
import { WasteDemand } from '@/domain/valueobject/demand/waste-demand';
import { Distance } from '@/domain/valueobject/location/distance';
import { ServiceTime } from '@/domain/valueobject/location/service-time';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

/**
 * ServiceAssignmentAdd
 * 
 * Data Transfer Object for adding a new ServiceAssignment entity.
 * 
 * This DTO is designed to be used in Vue.js forms for creating new service assignments
 * between containers and facilities. It contains primitive types that can be easily
 * bound to v-model directives and includes validation methods for form field validation.
 * 
 * All attributes are public to allow direct binding with Vue.js form components.
 */
export class ServiceAssignmentAdd {
  /**
   * Container identifier (UUID).
   */
  public containerId: string;

  /**
   * Facility identifier (UUID).
   */
  public facilityId: string;

  /**
   * Waste demand value (must be >= 0).
   */
  public wasteDemandValue: number;

  /**
   * Quantity unit for waste demand (e.g., TONNES, KILOGRAMS).
   */
  public wasteDemandQuantityUnit: string;

  /**
   * Time unit for waste demand (e.g., DAY, WEEK, MONTH).
   */
  public wasteDemandTimeUnit: string;

  /**
   * Distance in meters (must be >= 0).
   */
  public distanceInMeters: number;

  /**
   * Service time in minutes (must be >= 0).
   */
  public serviceTimeInMinutes: number;

  /**
   * Transportation cost per kilometer (must be >= 0).
   */
  public transportCostPerKilometer: number;

  /**
   * ISO 4217 currency code for transportation cost (e.g., 'EUR', 'USD').
   */
  public currencyCode: string;

  /**
   * Create a new ServiceAssignmentAdd DTO.
   * 
   * @param containerId Container identifier
   * @param facilityId Facility identifier
   * @param wasteDemandValue Waste demand value
   * @param wasteDemandQuantityUnit Quantity unit for waste demand
   * @param wasteDemandTimeUnit Time unit for waste demand
   * @param distanceInMeters Distance in meters
   * @param serviceTimeInMinutes Service time in minutes
   * @param transportCostPerKilometer Transportation cost per kilometer
   * @param currencyCode ISO 4217 currency code
   * @throws Error if any required attribute is undefined or null
   */
  constructor(
    containerId: string,
    facilityId: string,
    wasteDemandValue: number,
    wasteDemandQuantityUnit: string,
    wasteDemandTimeUnit: string,
    distanceInMeters: number,
    serviceTimeInMinutes: number,
    transportCostPerKilometer: number,
    currencyCode: string
  ) {
    this.validate<string>(containerId, 'Container ID is not defined');
    this.validate<string>(facilityId, 'Facility ID is not defined');
    this.validate<number>(wasteDemandValue, 'Waste demand value is not defined');
    this.validate<string>(wasteDemandQuantityUnit, 'Waste demand quantity unit is not defined');
    this.validate<string>(wasteDemandTimeUnit, 'Waste demand time unit is not defined');
    this.validate<number>(distanceInMeters, 'Distance is not defined');
    this.validate<number>(serviceTimeInMinutes, 'Service time is not defined');
    this.validate<number>(transportCostPerKilometer, 'Transport cost per kilometer is not defined');
    this.validate<string>(currencyCode, 'Currency code is not defined');

    this.containerId = containerId;
    this.facilityId = facilityId;
    this.wasteDemandValue = wasteDemandValue;
    this.wasteDemandQuantityUnit = wasteDemandQuantityUnit;
    this.wasteDemandTimeUnit = wasteDemandTimeUnit;
    this.distanceInMeters = distanceInMeters;
    this.serviceTimeInMinutes = serviceTimeInMinutes;
    this.transportCostPerKilometer = transportCostPerKilometer;
    this.currencyCode = currencyCode;
  }

  /**
   * Validate that an attribute is defined (not null or undefined).
   * 
   * @param attribute Attribute to validate
   * @param errorMessage Error message to throw if validation fails
   * @throws Error with the provided message if attribute is null or undefined
   */
  private validate<T>(
    attribute: T | undefined,
    errorMessage: string
  ): asserts attribute is T {
    if (attribute === undefined || attribute === null) {
      throw new Error(errorMessage);
    }
  }

  /**
   * Validate container ID for form fields.
   * 
   * @param value Container ID string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateContainerId(value: string): boolean | string {
    try {
      new UllUUID(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate facility ID for form fields.
   * 
   * @param value Facility ID string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateFacilityId(value: string): boolean | string {
    try {
      new UllUUID(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate waste demand value for form fields.
   * 
   * @param value Waste demand value to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateWasteDemandValue(value: number): boolean | string {
    try {
      if (value < 0) {
        throw new Error('Waste demand value cannot be negative');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate waste demand quantity unit for form fields.
   * 
   * @param value Quantity unit string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateWasteDemandQuantityUnit(value: string): boolean | string {
    try {
      new QuantityUnit(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate waste demand time unit for form fields.
   * 
   * @param value Time unit string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateWasteDemandTimeUnit(value: string): boolean | string {
    try {
      const validUnits = Object.values(TimeUnit);
      if (!validUnits.includes(value as TimeUnit)) {
        throw new Error(`Invalid time unit. Must be one of: ${validUnits.join(', ')}`);
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate distance in meters for form fields.
   * 
   * @param value Distance value to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateDistanceInMeters(value: number): boolean | string {
    try {
      Distance.fromMeters(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate service time in minutes for form fields.
   * 
   * @param value Service time value to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateServiceTimeInMinutes(value: number): boolean | string {
    try {
      new ServiceTime(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate transport cost per kilometer for form fields.
   * 
   * @param value Transport cost value to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateTransportCostPerKilometer(value: number): boolean | string {
    try {
      if (value < 0) {
        throw new Error('Transport cost per kilometer cannot be negative');
      }
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Validate currency code for form fields.
   * 
   * @param value Currency code string to validate
   * @returns true if valid, error message string if invalid
   */
  static externalValidateCurrencyCode(value: string): boolean | string {
    try {
      new Currency(value);
      return true;
    } catch (error: any) {
      return error.message;
    }
  }

  /**
   * Generate a random ServiceAssignmentAdd instance for testing purposes.
   * 
   * @returns A new ServiceAssignmentAdd instance with random valid values
   */
  static random(): ServiceAssignmentAdd {
    const randomContainerId = UllUUID.random().getValue();
    const randomFacilityId = UllUUID.random().getValue();
    const randomWasteDemandValue = parseFloat((Math.random() * 10 + 1).toFixed(2));
    const randomQuantityUnit = 'tons';
    const timeUnits = Object.values(TimeUnit);
    const randomTimeUnit = timeUnits[Math.floor(Math.random() * timeUnits.length)] as TimeUnit;
    const randomDistance = parseFloat((Math.random() * 50000 + 1000).toFixed(2));
    const randomServiceTime = parseFloat((Math.random() * 120 + 10).toFixed(2));
    const randomCostPerKm = parseFloat((Math.random() * 5 + 0.5).toFixed(2));
    const randomCurrency = 'EUR';

    return new ServiceAssignmentAdd(
      randomContainerId,
      randomFacilityId,
      randomWasteDemandValue,
      randomQuantityUnit,
      randomTimeUnit as string,
      randomDistance,
      randomServiceTime,
      randomCostPerKm,
      randomCurrency
    );
  }

  /**
   * Convert this DTO to a ServiceAssignment domain entity.
   * 
   * Note: This method requires the actual Container and Facility entities to be provided,
   * as ServiceAssignment holds references to full entities, not just IDs.
   * 
   * @param serviceAssignmentAdd ServiceAssignmentAdd DTO instance
   * @param container Container entity instance
   * @param facility Facility entity instance
   * @returns A new ServiceAssignment domain entity
   * @throws Error if any value object validation fails
   */
  static toServiceAssignment(
    serviceAssignmentAdd: ServiceAssignmentAdd,
    container: Container,
    facility: Facility
  ): ServiceAssignment {
    const quantityUnit = new QuantityUnit(serviceAssignmentAdd.wasteDemandQuantityUnit);
    const wasteDemand = new WasteDemand(
      serviceAssignmentAdd.wasteDemandValue,
      quantityUnit,
      serviceAssignmentAdd.wasteDemandTimeUnit as TimeUnit
    );
    const distance = Distance.fromMeters(serviceAssignmentAdd.distanceInMeters);
    const serviceTime = new ServiceTime(serviceAssignmentAdd.serviceTimeInMinutes);
    const transportCost = new TransportationVariableCost(
      serviceAssignmentAdd.transportCostPerKilometer,
      serviceAssignmentAdd.currencyCode
    );

    return new ServiceAssignment(
      container,
      facility,
      wasteDemand,
      distance,
      serviceTime,
      transportCost
    );
  }
}
