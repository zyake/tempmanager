--
-- Data for Name: locations; Type: TABLE DATA; Schema: public; Owner: hoge
--
!REPLACE THE VALUE!
COPY public.locations (id, name, address, temprature_limit, password) FROM stdin;
1	home	130-0011	25	!REPLACE THE VALUE!
2	Tokyo	tokyo prefecture	0	\N
\.

--
-- Data for Name: temprature_adjust; Type: TABLE DATA; Schema: public; Owner: hoge
--

COPY public.temprature_adjust (id, name, adjust) FROM stdin;
1	home	-10
\.