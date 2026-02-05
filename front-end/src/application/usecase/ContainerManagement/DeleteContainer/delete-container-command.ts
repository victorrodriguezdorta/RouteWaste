import type { UllUUID } from "@ull-tfg/ull-tfg-typescript";

/**
 * Command object for deleting a container
 */
export interface DeleteContainerCommand {
    containerId: UllUUID;
}
