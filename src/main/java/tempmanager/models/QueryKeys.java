package tempmanager.models;

import tempmanager.db.QueryKey;

public enum QueryKeys implements QueryKey {
    getTempratureStatus,
    listTempratureHistories,
    getTimezone,
    recordTemprature,
    getRecordCount,
    refreshTempratureSummary,
    refreshTotalCount;

    @Override
    public String toString() {
        return name();
    }
}
