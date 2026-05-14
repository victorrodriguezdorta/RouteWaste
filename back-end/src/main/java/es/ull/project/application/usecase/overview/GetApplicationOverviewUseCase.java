package es.ull.project.application.usecase.overview;

import es.ull.project.domain.readmodel.ApplicationOverview;

/**
 * Provides a lightweight snapshot of persisted entities for dashboards (for example the home screen).
 */
public interface GetApplicationOverviewUseCase {

    /**
     * Returns entity counts and the most recently executed infrastructure plans (up to three),
     * ordered by {@code executedAt} descending. Plans without an execution timestamp are listed after dated ones.
     *
     * @return aggregate overview; never null
     */
    ApplicationOverview fetch();
}
