package es.ull.project.adapter.docker;

/**
 * CommandResult
 *
 * Simple immutable container for the stdout captured from a process execution.
 */
public record CommandResult(String standardOutput) {
}
