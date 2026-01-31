#!/bin/bash

# Script para renombrar archivos de PascalCase a kebab-case
# y actualizar todas las importaciones automáticamente

# Directorio del frontend
FRONTEND_DIR="front-end/src"

echo "🔄 Iniciando renombrado de archivos a kebab-case..."
echo "=================================================="

# Función para convertir PascalCase a kebab-case
to_kebab_case() {
    echo "$1" | sed -E 's/([a-z0-9])([A-Z])/\1-\2/g' | tr '[:upper:]' '[:lower:]'
}

# Array con todos los archivos a renombrar (ruta relativa desde front-end/src)
declare -A FILES_TO_RENAME

# application/usecase/ContainerManagement
FILES_TO_RENAME["application/usecase/ContainerManagement/CreateContainerUseCase.ts"]="application/usecase/ContainerManagement/create-container-use-case.ts"
FILES_TO_RENAME["application/usecase/ContainerManagement/DeleteContainerUseCase.ts"]="application/usecase/ContainerManagement/delete-container-use-case.ts"
FILES_TO_RENAME["application/usecase/ContainerManagement/FilterContainersUseCase.ts"]="application/usecase/ContainerManagement/filter-containers-use-case.ts"
FILES_TO_RENAME["application/usecase/ContainerManagement/GetContainerUseCase.ts"]="application/usecase/ContainerManagement/get-container-use-case.ts"
FILES_TO_RENAME["application/usecase/ContainerManagement/ListContainersUseCase.ts"]="application/usecase/ContainerManagement/list-containers-use-case.ts"
FILES_TO_RENAME["application/usecase/ContainerManagement/UpdateContainerUseCase.ts"]="application/usecase/ContainerManagement/update-container-use-case.ts"

# application/usecase/FacilityManagement
FILES_TO_RENAME["application/usecase/FacilityManagement/CreateFacilityUseCase.ts"]="application/usecase/FacilityManagement/create-facility-use-case.ts"
FILES_TO_RENAME["application/usecase/FacilityManagement/DeleteFacilityUseCase.ts"]="application/usecase/FacilityManagement/delete-facility-use-case.ts"
FILES_TO_RENAME["application/usecase/FacilityManagement/FilterFacilitiesUseCase.ts"]="application/usecase/FacilityManagement/filter-facilities-use-case.ts"
FILES_TO_RENAME["application/usecase/FacilityManagement/GetFacilityUseCase.ts"]="application/usecase/FacilityManagement/get-facility-use-case.ts"
FILES_TO_RENAME["application/usecase/FacilityManagement/ListFacilitiesUseCase.ts"]="application/usecase/FacilityManagement/list-facilities-use-case.ts"
FILES_TO_RENAME["application/usecase/FacilityManagement/UpdateFacilityUseCase.ts"]="application/usecase/FacilityManagement/update-facility-use-case.ts"

# application/usecase/InfrastructurePlanManagement
FILES_TO_RENAME["application/usecase/InfrastructurePlanManagement/CreateInfrastructurePlanUseCase.ts"]="application/usecase/InfrastructurePlanManagement/create-infrastructure-plan-use-case.ts"
FILES_TO_RENAME["application/usecase/InfrastructurePlanManagement/DeleteInfrastructurePlanUseCase.ts"]="application/usecase/InfrastructurePlanManagement/delete-infrastructure-plan-use-case.ts"
FILES_TO_RENAME["application/usecase/InfrastructurePlanManagement/GetInfrastructurePlanUseCase.ts"]="application/usecase/InfrastructurePlanManagement/get-infrastructure-plan-use-case.ts"
FILES_TO_RENAME["application/usecase/InfrastructurePlanManagement/ListInfrastructurePlansUseCase.ts"]="application/usecase/InfrastructurePlanManagement/list-infrastructure-plans-use-case.ts"
FILES_TO_RENAME["application/usecase/InfrastructurePlanManagement/UpdateInfrastructurePlanUseCase.ts"]="application/usecase/InfrastructurePlanManagement/update-infrastructure-plan-use-case.ts"
FILES_TO_RENAME["application/usecase/InfrastructurePlanManagement/ValidateInfrastructurePlanUseCase.ts"]="application/usecase/InfrastructurePlanManagement/validate-infrastructure-plan-use-case.ts"

# application/usecase/OptimizationAndReports
FILES_TO_RENAME["application/usecase/OptimizationAndReports/CalculateCostsUseCase.ts"]="application/usecase/OptimizationAndReports/calculate-costs-use-case.ts"
FILES_TO_RENAME["application/usecase/OptimizationAndReports/ExportReportUseCase.ts"]="application/usecase/OptimizationAndReports/export-report-use-case.ts"
FILES_TO_RENAME["application/usecase/OptimizationAndReports/GenerateReportUseCase.ts"]="application/usecase/OptimizationAndReports/generate-report-use-case.ts"
FILES_TO_RENAME["application/usecase/OptimizationAndReports/OptimizeRoutesUseCase.ts"]="application/usecase/OptimizationAndReports/optimize-routes-use-case.ts"

# application/usecase/ServiceAssignmentManagement
FILES_TO_RENAME["application/usecase/ServiceAssignmentManagement/AssignContainerToFacilityUseCase.ts"]="application/usecase/ServiceAssignmentManagement/assign-container-to-facility-use-case.ts"
FILES_TO_RENAME["application/usecase/ServiceAssignmentManagement/ListServiceAssignmentsUseCase.ts"]="application/usecase/ServiceAssignmentManagement/list-service-assignments-use-case.ts"
FILES_TO_RENAME["application/usecase/ServiceAssignmentManagement/RemoveServiceAssignmentUseCase.ts"]="application/usecase/ServiceAssignmentManagement/remove-service-assignment-use-case.ts"
FILES_TO_RENAME["application/usecase/ServiceAssignmentManagement/UpdateServiceAssignmentUseCase.ts"]="application/usecase/ServiceAssignmentManagement/update-service-assignment-use-case.ts"

# application/usecase/VehicleManagement
FILES_TO_RENAME["application/usecase/VehicleManagement/CreateVehicleUseCase.ts"]="application/usecase/VehicleManagement/create-vehicle-use-case.ts"
FILES_TO_RENAME["application/usecase/VehicleManagement/DeleteVehicleUseCase.ts"]="application/usecase/VehicleManagement/delete-vehicle-use-case.ts"
FILES_TO_RENAME["application/usecase/VehicleManagement/GetVehicleUseCase.ts"]="application/usecase/VehicleManagement/get-vehicle-use-case.ts"
FILES_TO_RENAME["application/usecase/VehicleManagement/ListVehiclesUseCase.ts"]="application/usecase/VehicleManagement/list-vehicles-use-case.ts"
FILES_TO_RENAME["application/usecase/VehicleManagement/UpdateVehicleUseCase.ts"]="application/usecase/VehicleManagement/update-vehicle-use-case.ts"

# domain/entity
FILES_TO_RENAME["domain/entity/Container.ts"]="domain/entity/container.ts"
FILES_TO_RENAME["domain/entity/Facility.ts"]="domain/entity/facility.ts"
FILES_TO_RENAME["domain/entity/InfrastructurePlan.ts"]="domain/entity/infrastructure-plan.ts"
FILES_TO_RENAME["domain/entity/ServiceAssignment.ts"]="domain/entity/service-assignment.ts"
FILES_TO_RENAME["domain/entity/Vehicle.ts"]="domain/entity/vehicle.ts"

# domain/enumerate
FILES_TO_RENAME["domain/enumerate/FacilityStatus.ts"]="domain/enumerate/facility-status.ts"
FILES_TO_RENAME["domain/enumerate/FacilityType.ts"]="domain/enumerate/facility-type.ts"
FILES_TO_RENAME["domain/enumerate/ServiceZone.ts"]="domain/enumerate/service-zone.ts"
FILES_TO_RENAME["domain/enumerate/TimeUnit.ts"]="domain/enumerate/time-unit.ts"
FILES_TO_RENAME["domain/enumerate/VehicleType.ts"]="domain/enumerate/vehicle-type.ts"
FILES_TO_RENAME["domain/enumerate/WasteType.ts"]="domain/enumerate/waste-type.ts"

# domain/valueobject/cost
FILES_TO_RENAME["domain/valueobject/cost/Currency.ts"]="domain/valueobject/cost/currency.ts"
FILES_TO_RENAME["domain/valueobject/cost/MaximumBudget.ts"]="domain/valueobject/cost/maximum-budget.ts"
FILES_TO_RENAME["domain/valueobject/cost/OpeningFixedCost.ts"]="domain/valueobject/cost/opening-fixed-cost.ts"
FILES_TO_RENAME["domain/valueobject/cost/TotalCost.ts"]="domain/valueobject/cost/total-cost.ts"
FILES_TO_RENAME["domain/valueobject/cost/TransportationVariableCost.ts"]="domain/valueobject/cost/transportation-variable-cost.ts"

# domain/valueobject/demand
FILES_TO_RENAME["domain/valueobject/demand/Capacity.ts"]="domain/valueobject/demand/capacity.ts"
FILES_TO_RENAME["domain/valueobject/demand/QuantityUnit.ts"]="domain/valueobject/demand/quantity-unit.ts"
FILES_TO_RENAME["domain/valueobject/demand/WasteDemand.ts"]="domain/valueobject/demand/waste-demand.ts"

# domain/valueobject/location
FILES_TO_RENAME["domain/valueobject/location/Distance.ts"]="domain/valueobject/location/distance.ts"
FILES_TO_RENAME["domain/valueobject/location/Location.ts"]="domain/valueobject/location/location.ts"
FILES_TO_RENAME["domain/valueobject/location/ServiceRadius.ts"]="domain/valueobject/location/service-radius.ts"
FILES_TO_RENAME["domain/valueobject/location/ServiceTime.ts"]="domain/valueobject/location/service-time.ts"

# domain/valueobject/policy
FILES_TO_RENAME["domain/valueobject/policy/ServicePolicies.ts"]="domain/valueobject/policy/service-policies.ts"

# domain/valueobject/time
FILES_TO_RENAME["domain/valueobject/time/PlanningPeriod.ts"]="domain/valueobject/time/planning-period.ts"
FILES_TO_RENAME["domain/valueobject/time/ValidityPeriod.ts"]="domain/valueobject/time/validity-period.ts"

# Contador de archivos procesados
RENAMED_COUNT=0
IMPORT_UPDATES=0

echo ""
echo "📝 PASO 1: Actualizando importaciones en todos los archivos..."
echo "--------------------------------------------------------------"

# Primero actualizamos todas las importaciones ANTES de renombrar los archivos
for OLD_PATH in "${!FILES_TO_RENAME[@]}"; do
    NEW_PATH="${FILES_TO_RENAME[$OLD_PATH]}"
    
    # Extraer solo el nombre del archivo sin extensión
    OLD_FILENAME=$(basename "$OLD_PATH" .ts)
    NEW_FILENAME=$(basename "$NEW_PATH" .ts)
    
    # Buscar y reemplazar en todos los archivos .ts y .vue
    # Patrones de importación comunes:
    # import { X } from './path/FileName'
    # import { X } from '../path/FileName'
    # import { X } from '@/path/FileName'
    
    # Buscar archivos que contengan el nombre del archivo viejo
    FILES_WITH_IMPORT=$(grep -rl "/${OLD_FILENAME}'" "${FRONTEND_DIR}" --include="*.ts" --include="*.vue" 2>/dev/null || true)
    FILES_WITH_IMPORT2=$(grep -rl "/${OLD_FILENAME}\"" "${FRONTEND_DIR}" --include="*.ts" --include="*.vue" 2>/dev/null || true)
    
    ALL_FILES=$(echo -e "${FILES_WITH_IMPORT}\n${FILES_WITH_IMPORT2}" | sort -u | grep -v '^$' || true)
    
    if [ -n "$ALL_FILES" ]; then
        while IFS= read -r file; do
            if [ -f "$file" ]; then
                # Reemplazar el nombre del archivo en las importaciones
                sed -i "s|/${OLD_FILENAME}'|/${NEW_FILENAME}'|g" "$file"
                sed -i "s|/${OLD_FILENAME}\"|/${NEW_FILENAME}\"|g" "$file"
                echo "  ✓ Actualizado import en: $file"
                ((IMPORT_UPDATES++))
            fi
        done <<< "$ALL_FILES"
    fi
done

echo ""
echo "📁 PASO 2: Renombrando archivos..."
echo "-----------------------------------"

# Ahora renombramos los archivos
for OLD_PATH in "${!FILES_TO_RENAME[@]}"; do
    NEW_PATH="${FILES_TO_RENAME[$OLD_PATH]}"
    
    FULL_OLD_PATH="${FRONTEND_DIR}/${OLD_PATH}"
    FULL_NEW_PATH="${FRONTEND_DIR}/${NEW_PATH}"
    
    if [ -f "$FULL_OLD_PATH" ]; then
        # Usar git mv si estamos en un repositorio git, sino usar mv normal
        if git rev-parse --is-inside-work-tree > /dev/null 2>&1; then
            git mv "$FULL_OLD_PATH" "$FULL_NEW_PATH" 2>/dev/null || mv "$FULL_OLD_PATH" "$FULL_NEW_PATH"
        else
            mv "$FULL_OLD_PATH" "$FULL_NEW_PATH"
        fi
        echo "  ✓ Renombrado: $(basename "$OLD_PATH") → $(basename "$NEW_PATH")"
        ((RENAMED_COUNT++))
    else
        echo "  ⚠ No encontrado: $FULL_OLD_PATH"
    fi
done

echo ""
echo "=================================================="
echo "✅ COMPLETADO"
echo "   - Archivos renombrados: $RENAMED_COUNT"
echo "   - Importaciones actualizadas: $IMPORT_UPDATES"
echo ""
echo "🔍 Ahora ejecuta 'npm run build' o 'npm run dev' para verificar"
echo "   que no hay errores de importación."
echo ""
echo "📋 Si usas Git, no olvides hacer commit de los cambios:"
echo "   git add ."
echo "   git commit -m 'refactor: rename files to kebab-case'"
echo "=================================================="
