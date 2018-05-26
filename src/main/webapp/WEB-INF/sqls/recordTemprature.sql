INSERT INTO
temprature_records(id, recorded_timestamp, timestamp_date, temprature, locations_id)
VALUES(
    NEXTVAL('temprature_records_seq'),
    current_timestamp,
    current_timestamp::date,
    ?,
    ?
)