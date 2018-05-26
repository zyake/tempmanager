SELECT temprature, recorded_timestamp
    FROM temprature_records
    WHERE id = ( SELECT MAX(id) FROM temprature_records )