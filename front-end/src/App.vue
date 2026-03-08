<script setup lang="ts">
import { VehicleHttpRepository } from '@/adapter/http/vehicle-http-repository';
import { Vehicle } from '@/domain/entity/vehicle';
import { TimeUnit } from '@/domain/enumerate/time-unit';
import { VehicleType } from '@/domain/enumerate/vehicle-type';
import { TransportationVariableCost } from '@/domain/valueobject/cost/transportation-variable-cost';
import { Capacity } from '@/domain/valueobject/demand/capacity';
import { QuantityUnit } from '@/domain/valueobject/demand/quantity-unit';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';
import { onMounted, ref } from 'vue';

const vehicleRepo = new VehicleHttpRepository();
const vehicleList = ref<Vehicle[]>([]);
const vehicleDetails = ref<Vehicle | null>(null);

// Métodos de prueba
const getVehicles = async () => {
  console.log('=== Testing list vehicles ===');
  const result = await vehicleRepo.list({ page: 0, pageSize: 10 });
  result.fold(
    error => console.error('Error getting vehicles:', error),
    data => {
      console.log('Vehicles retrieved successfully:', data);
      vehicleList.value = data;
    }
  );
};

const getVehicleById = async (id: string) => {
  console.log('=== Testing get vehicle by ID ===');
  try {
    const vehicleId = new UllUUID(id);
    const result = await vehicleRepo.getById({ vehicleId });
    result.fold(
      error => console.error('Error getting vehicle:', error),
      data => {
        console.log('Vehicle retrieved successfully:', data);
        vehicleDetails.value = data;
      }
    );
  } catch (error) {
    console.error('Error parsing UUID:', error);
  }
};

const registerVehicle = async () => {
  console.log('=== Testing register vehicle ===');
  const result = await vehicleRepo.create({
    vehicleType: VehicleType.COLLECTION_TRUCK,
    transportCapacity: new Capacity(5, new QuantityUnit('tons'), TimeUnit.DAY),
    costPerKilometer: new TransportationVariableCost(1.50)
  });
  result.fold(
    error => console.error('Error registering vehicle:', error),
    data => console.log('Vehicle registered successfully:', data)
  );
};

const updateVehicle = async (id: string) => {
  console.log('=== Testing update vehicle ===');
  try {
    const vehicleId = new UllUUID(id);
    const result = await vehicleRepo.update({
      vehicleId,
      updatedFields: {
        vehicleType: VehicleType.TRANSFER_TRUCK,
        transportCapacity: new Capacity(10, new QuantityUnit('tons'), TimeUnit.DAY),
        costPerKilometer: new TransportationVariableCost(2.00)
      }
    });
    result.fold(
      error => console.error('Error updating vehicle:', error),
      data => console.log('Vehicle updated successfully:', data)
    );
  } catch (error) {
    console.error('Error parsing UUID:', error);
  }
};

const deleteVehicle = async (id: string) => {
  console.log('=== Testing delete vehicle ===');
  try {
    const vehicleId = new UllUUID(id);
    const result = await vehicleRepo.delete({ vehicleId });
    result.fold(
      error => console.error('Error deleting vehicle:', error),
      data => console.log('Vehicle deleted successfully:', data)
    );
  } catch (error) {
    console.error('Error parsing UUID:', error);
  }
};

// Ejecutar pruebas al cargar el componente
onMounted(() => {
  getVehicles();
  // NOTA: Usa un ID existente de tu base de datos
  const testId = '96f712e9-4d17-4214-b15e-598078f32518';
  getVehicleById(testId);
  registerVehicle();
  // Comentadas para no modificar/eliminar datos existentes
  // updateVehicle(testId);
  // deleteVehicle(testId);
});
</script>

<template>
  <div>
    <h1>HTTP Repository Test - Vehicle</h1>
    
    <h2>Vehicles List:</h2>
    <ul v-if="vehicleList.length > 0">
      <li v-for="vehicle in vehicleList" :key="vehicle.getId().toString()">
        <strong>ID:</strong> {{ vehicle.getId().toString() }}<br />
        <strong>Type:</strong> {{ vehicle.getVehicleType() }}<br />
        <strong>Capacity:</strong> {{ vehicle.getTransportCapacity().getValue() }} 
        {{ vehicle.getTransportCapacity().quantityUnit.value }}/{{ vehicle.getTransportCapacity().timeUnit }}<br />
        <strong>Cost per km:</strong> {{ vehicle.getCostPerKilometer().amount }} 
        {{ vehicle.getCostPerKilometer().currency.code }}
      </li>
    </ul>
    <p v-else>No vehicles found or still loading...</p>

    <h2>Vehicle Details (by ID):</h2>
    <div v-if="vehicleDetails">
      <p>
        <strong>ID:</strong> {{ vehicleDetails.getId().toString() }}<br />
        <strong>Type:</strong> {{ vehicleDetails.getVehicleType() }}<br />
        <strong>Capacity:</strong> {{ vehicleDetails.getTransportCapacity().getValue() }} 
        {{ vehicleDetails.getTransportCapacity().quantityUnit.value }}/{{ vehicleDetails.getTransportCapacity().timeUnit }}<br />
        <strong>Cost per km:</strong> {{ vehicleDetails.getCostPerKilometer().amount }} 
        {{ vehicleDetails.getCostPerKilometer().currency.code }}
      </p>
    </div>
    <p v-else>No vehicle details available or still loading...</p>

    <p><em>Check the browser console for detailed test results</em></p>
  </div>
</template>

<style scoped>
/* Asegurar que todo el texto sea negro y legible */
div {
  color: #000000;
}

p, li, strong, em {
  color: #000000;
}

h1 {
  color: #42b883;
  margin-bottom: 20px;
}

h2 {
  color: #2c3e50;
  margin-top: 30px;
  margin-bottom: 10px;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  background: #f4f4f4;
  margin: 10px 0;
  padding: 15px;
  border-radius: 5px;
  border-left: 4px solid #42b883;
  color: #000000;
}

div > p {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 5px;
  border-left: 4px solid #646cff;
  color: #000000;
}
</style>
