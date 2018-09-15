--
-- PostgreSQL database dump
--

-- Dumped from database version 10.4
-- Dumped by pg_dump version 10.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: fn_getlastdayofmonth(date); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_getlastdayofmonth(date) RETURNS date
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
SELECT (date_trunc('MONTH', $1) + INTERVAL '1 MONTH - 1 day')::DATE;
$_$;


--
-- Name: get_sum_of(anyelement, anyelement, anyelement); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_sum_of(anyelement, anyelement, anyelement) RETURNS anyelement
    LANGUAGE plpgsql
    AS $_$
BEGIN
   RETURN $1 + $2 + $3;
END;
$_$;


--
-- Name: mainteinance_indexes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.mainteinance_indexes() RETURNS integer
    LANGUAGE plpgsql
    AS $$

DECLARE
mschemaname varchar;
mindexname varchar;
mdensity float;
mcur CURSOR FOR SELECT schemaname, indexrelname FROM pg_stat_user_indexes;
BEGIN
open mcur;
LOOP
FETCH FROM mcur into mschemaname, mindexname;
EXIT WHEN NOT FOUND;
SELECT avg_leaf_density INTO mdensity FROM pgstatindex(mschemaname || '.' || mindexname);
IF mdensity < 90 THEN

END IF;
EXECUTE 'REINDEX INDEX ' || mschemaname || '.' || mindexname;
RAISE NOTICE 'index: %, %, %', mschemaname, mindexname, mdensity;
END LOOP;
    RETURN 1;
END;
$$;

--
-- Name: maintenanceindexes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.maintenanceindexes() RETURNS integer
    LANGUAGE plpgsql
    AS $$
BEGIN
    RAISE LOG 'start clustering...';
    CLUSTER VERBOSE temprature_records USING temprature_records_timestamp_date_index;
    RAISE LOG 'end clustering...';
    RETURN 1;
END;
$$;

--
-- Name: select_by_id(integer, anyelement, anyelement, anyelement); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.select_by_id(integer, anyelement, anyelement, anyelement) RETURNS anyelement
    LANGUAGE plpgsql
    AS $_$
DECLARE
 id ALIAS FOR $1;
BEGIN
   IF id = 1 THEN
    RETURN $2;
   END IF;
   IF id = 2 THEN
    RETURN $3;
   END IF;
   IF id = 3 THEN
    RETURN $4;
   END IF;
END;
$_$;

--
-- Name: selectone(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.selectone(id integer) RETURNS integer
    LANGUAGE sql
    AS $$
SELECT id;
$$;

--
-- Name: update_temprature(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_temprature(loc_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
 returned_loc_id int;
BEGIN
SELECT INTO returned_loc_id COUNT(*) FROM locations WHERE id = loc_id;
IF returned_loc_id = 0 THEN
 RETURN -1;
END IF;
RETURN returned_loc_id;
END;
$$;

--
-- Name: update_temprature(real, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_temprature(temprature real, loc_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
$$;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: temprature_adjust; Type: TABLE; Schema: public; Owner: hoge
--

CREATE TABLE public.temprature_adjust (
    id integer NOT NULL,
    name character varying(30) NOT NULL,
    adjust double precision NOT NULL
);


ALTER TABLE public.temprature_adjust OWNER TO hoge;

--
-- Name: temprature_records; Type: TABLE; Schema: public; Owner: hoge
--

CREATE TABLE public.temprature_records (
    id integer NOT NULL,
    recorded_timestamp timestamp with time zone,
    temprature real,
    locations_id integer,
    timestamp_date date
);


ALTER TABLE public.temprature_records OWNER TO hoge;

--
-- Name: temprature_daily_summary; Type: MATERIALIZED VIEW; Schema: public; Owner: hoge
--

CREATE MATERIALIZED VIEW public.temprature_daily_summary AS
 SELECT max(temprature_records.temprature) AS max,
    min(temprature_records.temprature) AS min,
    avg(temprature_records.temprature) AS avg,
    temprature_records.timestamp_date,
    count(temprature_records.timestamp_date) AS count
   FROM public.temprature_records
  GROUP BY temprature_records.timestamp_date
  ORDER BY temprature_records.timestamp_date DESC
  WITH NO DATA;


ALTER TABLE public.temprature_daily_summary OWNER TO hoge;

--
-- Name: temprature_monthly_summary; Type: MATERIALIZED VIEW; Schema: public; Owner: hoge
--

CREATE MATERIALIZED VIEW public.temprature_monthly_summary AS
 SELECT max(temprature_daily_summary.max) AS max,
    min(temprature_daily_summary.min) AS min,
    avg(temprature_daily_summary.avg) AS avg,
    date_part('year'::text, temprature_daily_summary.timestamp_date) AS year,
    date_part('month'::text, temprature_daily_summary.timestamp_date) AS month,
    sum(temprature_daily_summary.count) AS sum
   FROM public.temprature_daily_summary
  GROUP BY (date_part('year'::text, temprature_daily_summary.timestamp_date)), (date_part('month'::text, temprature_daily_summary.timestamp_date))
  WITH NO DATA;


ALTER TABLE public.temprature_monthly_summary OWNER TO hoge;

--
-- Name: adjusted_temprature_monthly_summary; Type: VIEW; Schema: public; Owner: hoge
--

CREATE VIEW public.adjusted_temprature_monthly_summary AS
 SELECT ((temprature_monthly_summary.min + ( SELECT temprature_adjust.adjust
           FROM public.temprature_adjust
          WHERE (temprature_adjust.id = 1))))::integer AS min,
    ((temprature_monthly_summary.max + ( SELECT temprature_adjust.adjust
           FROM public.temprature_adjust
          WHERE (temprature_adjust.id = 1))))::integer AS max,
    ((temprature_monthly_summary.avg + ( SELECT temprature_adjust.adjust
           FROM public.temprature_adjust
          WHERE (temprature_adjust.id = 1))))::integer AS avg,
    temprature_monthly_summary.year,
    temprature_monthly_summary.month,
    temprature_monthly_summary.sum
   FROM public.temprature_monthly_summary;


ALTER TABLE public.adjusted_temprature_monthly_summary OWNER TO hoge;

--
-- Name: adjusted_temprature_summary; Type: VIEW; Schema: public; Owner: hoge
--

CREATE VIEW public.adjusted_temprature_summary AS
 SELECT (temprature_daily_summary.max + ( SELECT temprature_adjust.adjust
           FROM public.temprature_adjust
          WHERE (temprature_adjust.id = 1))) AS max,
    (temprature_daily_summary.min + ( SELECT temprature_adjust.adjust
           FROM public.temprature_adjust
          WHERE (temprature_adjust.id = 1))) AS min,
    (temprature_daily_summary.avg + ( SELECT temprature_adjust.adjust
           FROM public.temprature_adjust
          WHERE (temprature_adjust.id = 1))) AS avg,
    temprature_daily_summary.timestamp_date,
    temprature_daily_summary.count
   FROM public.temprature_daily_summary;


ALTER TABLE public.adjusted_temprature_summary OWNER TO hoge;

--
-- Name: temprature_weekly_summary; Type: MATERIALIZED VIEW; Schema: public; Owner: hoge
--

CREATE MATERIALIZED VIEW public.temprature_weekly_summary AS
 SELECT (min(last_seven_days.temprature))::integer AS min,
    (max(last_seven_days.temprature))::integer AS max,
    (avg(last_seven_days.temprature))::integer AS avg,
    last_seven_days.timestamp_date,
    count(last_seven_days.timestamp_date) AS count
   FROM ( SELECT temprature_records.temprature,
            temprature_records.timestamp_date
           FROM public.temprature_records
          WHERE ((temprature_records.timestamp_date >= (CURRENT_DATE - '7 days'::interval)) AND (temprature_records.timestamp_date <= CURRENT_DATE))) last_seven_days
  GROUP BY last_seven_days.timestamp_date
  ORDER BY last_seven_days.timestamp_date DESC
  WITH NO DATA;


ALTER TABLE public.temprature_weekly_summary OWNER TO hoge;

--
-- Name: adjusted_temprature_weekly_summary; Type: VIEW; Schema: public; Owner: hoge
--

CREATE VIEW public.adjusted_temprature_weekly_summary AS
 SELECT (((temprature_weekly_summary.min)::double precision + ( SELECT temprature_adjust.adjust
           FROM public.temprature_adjust
          WHERE (temprature_adjust.id = 1))))::integer AS min,
    (((temprature_weekly_summary.max)::double precision + ( SELECT temprature_adjust.adjust
           FROM public.temprature_adjust
          WHERE (temprature_adjust.id = 1))))::integer AS max,
    (((temprature_weekly_summary.avg)::double precision + ( SELECT temprature_adjust.adjust
           FROM public.temprature_adjust
          WHERE (temprature_adjust.id = 1))))::integer AS avg,
    temprature_weekly_summary.timestamp_date,
    temprature_weekly_summary.count
   FROM public.temprature_weekly_summary;


ALTER TABLE public.adjusted_temprature_weekly_summary OWNER TO hoge;

--
-- Name: ids; Type: TABLE; Schema: public; Owner: hoge
--

CREATE TABLE public.ids (
    id integer
);


ALTER TABLE public.ids OWNER TO hoge;

--
-- Name: locations; Type: TABLE; Schema: public; Owner: hoge
--

CREATE TABLE public.locations (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    address character varying(200) NOT NULL,
    temprature_limit integer DEFAULT 0 NOT NULL,
    password bytea
);


ALTER TABLE public.locations OWNER TO hoge;

--
-- Name: locations_id_seq; Type: SEQUENCE; Schema: public; Owner: hoge
--

CREATE SEQUENCE public.locations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.locations_id_seq OWNER TO hoge;

--
-- Name: log_status; Type: TABLE; Schema: public; Owner: hoge
--

CREATE TABLE public.log_status (
    recorded_timestamp timestamp without time zone NOT NULL,
    user_agent character varying NOT NULL,
    url character varying NOT NULL
);


ALTER TABLE public.log_status OWNER TO hoge;

--
-- Name: temprature_records_seq; Type: SEQUENCE; Schema: public; Owner: hoge
--

CREATE SEQUENCE public.temprature_records_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.temprature_records_seq OWNER TO hoge;

--
-- Name: temprature_total_count; Type: MATERIALIZED VIEW; Schema: public; Owner: hoge
--

CREATE MATERIALIZED VIEW public.temprature_total_count AS
 SELECT count(*) AS count
   FROM public.temprature_records
  WITH NO DATA;


ALTER TABLE public.temprature_total_count OWNER TO hoge;

--
-- Name: locations locations_pkey; Type: CONSTRAINT; Schema: public; Owner: hoge
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (id);


--
-- Name: temprature_adjust temprature_adjust_pkey; Type: CONSTRAINT; Schema: public; Owner: hoge
--

ALTER TABLE ONLY public.temprature_adjust
    ADD CONSTRAINT temprature_adjust_pkey PRIMARY KEY (id);


--
-- Name: temprature_records temprature_records_id_index; Type: CONSTRAINT; Schema: public; Owner: hoge
--

ALTER TABLE ONLY public.temprature_records
    ADD CONSTRAINT temprature_records_id_index PRIMARY KEY (id);


--
-- Name: temprature_records_timestamp_date_index; Type: INDEX; Schema: public; Owner: hoge
--

CREATE INDEX temprature_records_timestamp_date_index ON public.temprature_records USING btree (timestamp_date);

ALTER TABLE public.temprature_records CLUSTER ON temprature_records_timestamp_date_index;

