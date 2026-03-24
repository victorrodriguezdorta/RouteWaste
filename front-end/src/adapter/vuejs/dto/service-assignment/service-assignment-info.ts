import { ServiceAssignment } from '@/domain/entity/service-assignment';

/**
 * ServiceAssignmentInfo
 * 
 * Data Transfer Object for displaying ServiceAssignment entity information.
 * 
 * This DTO is designed to be used in Vue.js views for displaying service assignment details.
 * It includes all assignment data plus formatted strings and utility methods for the UI layer.
 * 
 * This DTO is read-only and focused on presentation.
 * 
 * All attributes are public to allow direct binding with Vue.js display components.
 */
export class ServiceAssignmentInfo {
  /**
   * Unique identifier for the service assignment.
   */
  public id: string;

  /**
   * Container identifier (UUID).
   */
  public containerId: string;

  /**
   * Facility identifier (UUID).
   */
  public facilityId: string;

  /**
   * Waste demand value.
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
   * Distance in meters.
   */
  public distanceInMeters: number;

  /**
   * Service time in minutes.
   */
  public serviceTimeInMinutes: number;

  /**
   * Transportation cost per kilometer.
   */
  public transportCostPerKilometer: number;

  /**
   * ISO 4217 currency code for transportation cost.
   */
  public currencyCode: string;

  /**
   * Create a new ServiceAssignmentInfo DTO.
   * 
   * @param id Unique identifier for the service assignment
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
    id: string,
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
    this.validate<string>(id, 'Service assignment ID is not defined');
    this.validate<string>(containerId, 'Container ID is not defined');
    this.validate<string>(facilityId, 'Facility ID is not defined');
    this.validate<number>(wasteDemandValue, 'Waste demand value is not defined');
    this.validate<string>(wasteDemandQuantityUnit, 'Waste demand quantity unit is not defined');
    this.validate<string>(wasteDemandTimeUnit, 'Waste demand time unit is not defined');
    this.validate<number>(distanceInMeters, 'Distance is not defined');
    this.validate<number>(serviceTimeInMinutes, 'Service time is not defined');
    this.validate<number>(transportCostPerKilometer, 'Transport cost per kilometer is not defined');
    this.validate<string>(currencyCode, 'Currency code is not defined');

    this.id = id;
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
   * Create a ServiceAssignmentInfo DTO from a ServiceAssignment domain entity.
   * 
   * @param serviceAssignment ServiceAssignment domain entity
   * @returns A new ServiceAssignmentInfo DTO with values extracted from the entity
   */
  static fromServiceAssignment(serviceAssignment: ServiceAssignment): ServiceAssignmentInfo {
    return new ServiceAssignmentInfo(
      serviceAssignment.id.getValue(),
      serviceAssignment.container.getId().getValue(),
      serviceAssignment.facility.getId().getValue(),
      serviceAssignment.wasteDemand.getValue(),
      serviceAssignment.wasteDemand.getQuantityUnit().getValue(),
      serviceAssignment.wasteDemand.getTimeUnit() as string,
      serviceAssignment.distance.getValue(),
      serviceAssignment.serviceTime.getValue(),
      serviceAssignment.transportCost.getAmount(),
      serviceAssignment.transportCost.getCurrency().getCode()
    );
  }

  /**
   * Get distance in kilometers (converted from meters).
   * 
   * @returns Distance in kilometers
   */
  getDistanceInKilometers(): number {
    return parseFloat((this.distanceInMeters / 1000).toFixed(2));
  }

  /**
   * Get formatted distance string showing both meters and kilometers.
   * 
   * @returns Formatted distance string (e.g., "5,420.50 m (5.42 km)")
   */
  getFormattedDistance(): string {
    const meters = this.distanceInMeters.toLocaleString('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
    const kilometers = this.getDistanceInKilometers();
    return `${meters} m (${kilometers} km)`;
  }

  /**
   * Get service time in hours (converted from minutes).
   * 
   * @returns Service time in hours
   */
  getServiceTimeInHours(): number {
    return parseFloat((this.serviceTimeInMinutes / 60).toFixed(2));
  }

  /**
   * Get formatted service time string showing both minutes and hours.
   * 
   * @returns Formatted service time string (e.g., "75.00 min (1.25 h)")
   */
  getFormattedServiceTime(): string {
    const minutes = this.serviceTimeInMinutes.toFixed(2);
    const hours = this.getServiceTimeInHours();
    return `${minutes} min (${hours} h)`;
  }

  /**
   * Get formatted waste demand string with units.
   * 
   * @returns Formatted waste demand string (e.g., "5.50 TONNES/DAY")
   */
  getFormattedWasteDemand(): string {
    const value = this.wasteDemandValue.toFixed(2);
    return `${value} ${this.wasteDemandQuantityUnit}/${this.wasteDemandTimeUnit}`;
  }

  /**
   * Calculate total transportation cost based on distance.
   * 
   * @returns Total transport cost for this assignment
   */
  getTotalTransportCost(): number {
    const distanceKm = this.getDistanceInKilometers();
    return parseFloat((this.transportCostPerKilometer * distanceKm).toFixed(2));
  }

  /**
   * Get formatted transportation cost per kilometer.
   * 
   * @returns Formatted cost per kilometer with currency (e.g., "€1.50/km")
   */
  getFormattedTransportCostPerKilometer(): string {
    const symbol = this.getCurrencySymbol();
    const cost = this.transportCostPerKilometer.toFixed(2);
    return `${symbol}${cost}/km`;
  }

  /**
   * Get formatted total transportation cost.
   * 
   * @returns Formatted total transport cost with currency (e.g., "€8.13")
   */
  getFormattedTotalTransportCost(): string {
    const symbol = this.getCurrencySymbol();
    const cost = this.getTotalTransportCost().toFixed(2);
    return `${symbol}${cost}`;
  }

  /**
   * Check if the service assignment is for a short distance (< 5 km).
   * 
   * @returns true if distance is less than 5 kilometers
   */
  isShortDistance(): boolean {
    return this.getDistanceInKilometers() < 5;
  }

  /**
   * Check if the service assignment is for a medium distance (5-20 km).
   * 
   * @returns true if distance is between 5 and 20 kilometers
   */
  isMediumDistance(): boolean {
    const km = this.getDistanceInKilometers();
    return km >= 5 && km <= 20;
  }

  /**
   * Check if the service assignment is for a long distance (> 20 km).
   * 
   * @returns true if distance is greater than 20 kilometers
   */
  isLongDistance(): boolean {
    return this.getDistanceInKilometers() > 20;
  }

  /**
   * Get distance classification as a descriptive string.
   * 
   * @returns Distance classification ("Short", "Medium", or "Long")
   */
  getDistanceClassification(): string {
    if (this.isShortDistance()) {
      return 'Short';
    } else if (this.isMediumDistance()) {
      return 'Medium';
    } else {
      return 'Long';
    }
  }

  /**
   * Check if the service assignment has a short service time (< 30 minutes).
   * 
   * @returns true if service time is less than 30 minutes
   */
  isQuickService(): boolean {
    return this.serviceTimeInMinutes < 30;
  }

  /**
   * Check if the service assignment has a long service time (> 60 minutes).
   * 
   * @returns true if service time is greater than 60 minutes
   */
  isSlowService(): boolean {
    return this.serviceTimeInMinutes > 60;
  }

  /**
   * Get service time classification as a descriptive string.
   * 
   * @returns Service time classification ("Quick", "Normal", or "Slow")
   */
  getServiceTimeClassification(): string {
    if (this.isQuickService()) {
      return 'Quick';
    } else if (this.isSlowService()) {
      return 'Slow';
    } else {
      return 'Normal';
    }
  }

  /**
   * Get currency symbol based on currency code.
   * 
   * @returns Currency symbol (defaults to currency code if not found)
   */
  private getCurrencySymbol(): string {
    const currencySymbols: { [key: string]: string } = {
      EUR: '€',
      USD: '$',
      GBP: '£',
      JPY: '¥',
    };

    return currencySymbols[this.currencyCode] || this.currencyCode;
  }

  /**
   * Get efficiency rating based on cost per kilometer.
   * 
   * @returns Efficiency rating ("Efficient", "Average", or "Expensive")
   */
  getEfficiencyRating(): string {
    if (this.transportCostPerKilometer < 1.0) {
      return 'Efficient';
    } else if (this.transportCostPerKilometer < 3.0) {
      return 'Average';
    } else {
      return 'Expensive';
    }
  }

  /**
   * Get comprehensive assignment summary as a formatted string.
   * 
   * @returns Multi-line summary of the service assignment
   */
  getAssignmentSummary(): string {
    return [
      `Service Assignment: ${this.id}`,
      `Container: ${this.containerId} → Facility: ${this.facilityId}`,
      `Waste Demand: ${this.getFormattedWasteDemand()}`,
      `Distance: ${this.getFormattedDistance()} (${this.getDistanceClassification()})`,
      `Service Time: ${this.getFormattedServiceTime()} (${this.getServiceTimeClassification()})`,
      `Transport Cost: ${this.getFormattedTransportCostPerKilometer()} (${this.getEfficiencyRating()})`,
      `Total Cost: ${this.getFormattedTotalTransportCost()}`,
    ].join('\n');
  }
}
