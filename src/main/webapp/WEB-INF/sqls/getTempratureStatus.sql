SELECT temprature + (SELECT adjust FROM temprature_adjust WHERE id = 1), recorded_timestamp, (SELECT temprature_limit FROM locations WHERE id = 1) as temp_limit
    FROM temprature_records
    WHERE id = ( SELECT MAX(id) FROM temprature_records )