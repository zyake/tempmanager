CREATE OR REPLACE FUNCTION update_temprature(temprature real, loc_id int) RETURNS integer AS $$
DECLARE
 returned_loc_id int;
BEGIN
SELECT INTO returned_loc_id COUNT(*) FROM locations WHERE id = loc_id;
IF returned_loc_id = 0 THEN
 RAISE EXCEPTION 'Invalid location id %', loc_id USING HINT = 'location id must exist.';
END IF;

INSERT INTO
temprature_records(id, recorded_timestamp, timestamp_date, temprature, locations_id)
VALUES(
    NEXTVAL('temprature_records_seq'),
    current_timestamp,
    current_timestamp::date,
    temprature,
    loc_id
);
REFRESH MATERIALIZED VIEW temprature_daily_summary;
REFRESH MATERIALIZED VIEW temprature_weekly_summary;
REFRESH MATERIALIZED VIEW temprature_monthly_summary;
REFRESH MATERIALIZED VIEW temprature_total_count;
RETURN 1;
END;
$$ LANGUAGE plpgsql;
