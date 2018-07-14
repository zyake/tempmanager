CREATE OR REPLACE FUNCTION MaintenanceIndexes()
RETURNS INTEGER AS
$$
BEGIN
    RAISE LOG 'start clustering...';
    CLUSTER VERBOSE temprature_records USING temprature_records_timestamp_date_index;
    RAISE LOG 'end clustering...';
    RETURN 1;
END;
$$ LANGUAGE plpgsql;