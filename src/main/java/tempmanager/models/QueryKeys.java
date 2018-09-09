package tempmanager.models;

import tempmanager.db.QueryKey;

public enum QueryKeys implements QueryKey {
    getTempratureStatus,
    listTempratureHistories,
    getTimezone,
    getRecordCount,
    updateTemprature,
    listTempratureRecords,
    maintenanceIndexes,
    listTempratureWeeklySummary,
    listTempratureMonthlySummary,
    insertLogStatus,
    getPassword
    ;

    @Override
    public String toString() {
        return name();
    }
}
