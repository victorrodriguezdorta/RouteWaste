import type { APIRequestContext } from '@playwright/test';
import { apiBaseUrl } from './api';

export async function createVehicle(
  request: APIRequestContext,
  name: string,
): Promise<{ id: string }> {
  const response = await request.post(`${apiBaseUrl()}vehicles/`, {
    headers: { 'Content-Type': 'application/json' },
    data: {
      name,
      vehicleType: 'COLLECTION_TRUCK',
      capacityKilograms: 5000,
      CapacityLiters: 12000,
      costPerKilometer: { amount: 0.45, currency: 'EUR' },
    },
  });
  if (!response.ok()) {
    throw new Error(`POST vehicles failed: ${response.status()}`);
  }
  return (await response.json()) as { id: string };
}

export async function createContainer(
  request: APIRequestContext,
  name: string,
): Promise<{ id: string }> {
  const response = await request.post(`${apiBaseUrl()}containers/`, {
    headers: { 'Content-Type': 'application/json' },
    data: {
      name,
      location: {
        latitude: 28.4636,
        longitude: -16.2518,
        postalAddress: 'Playwright Test Street 1',
        gisReference: 'GIS-PLAYWRIGHT-TEST',
      },
      wasteType: 'ORGANIC',
      capacityLiters: { liters: 1000 },
      dailyDemandLitersPerDay: { litersPerDay: 80 },
      serviceZone: 'NEIGHBORHOOD',
    },
  });
  if (!response.ok()) {
    throw new Error(`POST containers failed: ${response.status()}`);
  }
  return (await response.json()) as { id: string };
}

export async function createFacility(
  request: APIRequestContext,
  name: string,
): Promise<{ id: string }> {
  const response = await request.post(`${apiBaseUrl()}facilities/`, {
    headers: { 'Content-Type': 'application/json' },
    data: {
      name,
      facilityType: 'TRANSFER_STATION',
      location: {
        latitude: 28.47,
        longitude: -16.25,
        postalAddress: 'Playwright Test Avenue 10',
        gisReference: 'GIS-FACILITY-PW',
      },
      storageCapacity: { value: 10000 },
      processingCapacity: { value: 5000 },
      unloadingTime: { timeValue: 30 },
      openingFixedCost: { amount: 1500, currency: 'EUR' },
      status: 'CANDIDATE',
    },
  });
  if (!response.ok()) {
    throw new Error(`POST facilities failed: ${response.status()}`);
  }
  return (await response.json()) as { id: string };
}

export async function deleteEntity(
  request: APIRequestContext,
  resource: 'vehicles' | 'containers' | 'facilities',
  id: string,
): Promise<void> {
  await request.delete(`${apiBaseUrl()}${resource}/${id}`);
}
