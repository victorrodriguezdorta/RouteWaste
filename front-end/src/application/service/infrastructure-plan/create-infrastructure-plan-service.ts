import type { InfrastructurePlanRepository } from '@/application/repository/infrastructure-plan-repository';
import type { CreateInfrastructurePlanCommand, CreateInfrastructurePlanResult, CreateInfrastructurePlanUseCase } from '@/application/usecase/infrastructure-plan-management/create-infrastructure-plan/create-infrastructure-plan-use-case';
import type { DataError, Either } from '@ull-tfg/ull-tfg-typescript';

/**
 * @brief Service implementing the CreateInfrastructurePlan use case.
 *
 * Implements the application-level use case by delegating persistence/communication
 * details to a provided `InfrastructurePlanRepository`. Repository methods are
 * assumed to be available and provided later by the infrastructure layer.
 */
export class CreateInfrastructurePlanService implements CreateInfrastructurePlanUseCase {
    /**
     * Repositorio para la persistencia y gestión de planes de infraestructura.
     */
    private readonly infrastructurePlanRepository: InfrastructurePlanRepository;

    /**
     * @brief Construct the service with a repository implementation.
     * @param infrastructurePlanRepository Repository used to persist or forward create requests.
     */
    constructor(infrastructurePlanRepository: InfrastructurePlanRepository) {
        this.infrastructurePlanRepository = infrastructurePlanRepository;
    }

    /**
     * @brief Ejecuta el caso de uso para crear un plan de infraestructura.
     * @param command Datos de entrada requeridos para crear el plan.
     * @returns Either un `DataError` o la entidad `InfrastructurePlan` creada.
     */
    async execute(command: CreateInfrastructurePlanCommand): Promise<Either<DataError, CreateInfrastructurePlanResult>> {
        return this.infrastructurePlanRepository.create(command);
    }
}
