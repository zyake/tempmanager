SELECT temprature + (SELECT adjust FROM  temprature_adjust WHERE id = 1), recorded_timestamp FROM temprature_records WHERE timestamp_date >= ?::date AND timestamp_date <= ?::date