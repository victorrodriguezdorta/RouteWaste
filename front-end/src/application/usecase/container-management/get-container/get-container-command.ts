import type { UllUUID } from "@ull-tfg/ull-tfg-typescript";

/**
 * Command object for getting a container by id
 */
export interface GetContainerCommand {
    containerId: UllUUID;
}
