SELECT temprature + (SELECT adjust FROM temprature_adjust WHERE id = 1), recorded_timestamp
    FROM temprature_records
    WHERE id = ( SELECT MAX(id) FROM temprature_records )