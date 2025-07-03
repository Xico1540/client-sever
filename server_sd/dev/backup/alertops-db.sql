--
-- PostgreSQL database dump
--

-- Dumped from database version 13.16 (Debian 13.16-1.pgdg120+1)
-- Dumped by pg_dump version 14.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: channel_users; Type: TABLE; Schema: public; Owner: alertops_user
--

CREATE TABLE public.channel_users (
    user_id integer NOT NULL,
    channel_id integer NOT NULL
);


ALTER TABLE public.channel_users OWNER TO alertops_user;

--
-- Name: channels; Type: TABLE; Schema: public; Owner: alertops_user
--

CREATE TABLE public.channels (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    description text
);


ALTER TABLE public.channels OWNER TO alertops_user;

--
-- Name: channels_id_seq; Type: SEQUENCE; Schema: public; Owner: alertops_user
--

CREATE SEQUENCE public.channels_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.channels_id_seq OWNER TO alertops_user;

--
-- Name: channels_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: alertops_user
--

ALTER SEQUENCE public.channels_id_seq OWNED BY public.channels.id;


--
-- Name: messages; Type: TABLE; Schema: public; Owner: alertops_user
--

CREATE TABLE public.messages (
    id integer NOT NULL,
    sender_id integer NOT NULL,
    sender_name character varying(255) NOT NULL,
    recipient_id integer NOT NULL,
    channel_id integer NOT NULL,
    content text NOT NULL,
    "timestamp" timestamp without time zone NOT NULL
);


ALTER TABLE public.messages OWNER TO alertops_user;

--
-- Name: messages_id_seq; Type: SEQUENCE; Schema: public; Owner: alertops_user
--

CREATE SEQUENCE public.messages_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_id_seq OWNER TO alertops_user;

--
-- Name: messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: alertops_user
--

ALTER SEQUENCE public.messages_id_seq OWNED BY public.messages.id;


--
-- Name: notifications; Type: TABLE; Schema: public; Owner: alertops_user
--

CREATE TABLE public.notifications (
    id integer NOT NULL,
    user_id integer,
    message text NOT NULL,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.notifications OWNER TO alertops_user;

--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: alertops_user
--

CREATE SEQUENCE public.notifications_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.notifications_id_seq OWNER TO alertops_user;

--
-- Name: notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: alertops_user
--

ALTER SEQUENCE public.notifications_id_seq OWNED BY public.notifications.id;


--
-- Name: reports; Type: TABLE; Schema: public; Owner: alertops_user
--

CREATE TABLE public.reports (
    id integer NOT NULL,
    content text NOT NULL,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.reports OWNER TO alertops_user;

--
-- Name: reports_id_seq; Type: SEQUENCE; Schema: public; Owner: alertops_user
--

CREATE SEQUENCE public.reports_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.reports_id_seq OWNER TO alertops_user;

--
-- Name: reports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: alertops_user
--

ALTER SEQUENCE public.reports_id_seq OWNED BY public.reports.id;


--
-- Name: requests; Type: TABLE; Schema: public; Owner: alertops_user
--

CREATE TABLE public.requests (
    id integer NOT NULL,
    senderid character varying(255) NOT NULL,
    status character varying(50) NOT NULL,
    type character varying(50) NOT NULL,
    "timestamp" timestamp without time zone NOT NULL
);


ALTER TABLE public.requests OWNER TO alertops_user;

--
-- Name: requests_id_seq; Type: SEQUENCE; Schema: public; Owner: alertops_user
--

CREATE SEQUENCE public.requests_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.requests_id_seq OWNER TO alertops_user;

--
-- Name: requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: alertops_user
--

ALTER SEQUENCE public.requests_id_seq OWNED BY public.requests.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: alertops_user
--

CREATE TABLE public.users (
    id integer NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    email character varying(100) NOT NULL,
    role character varying(50) NOT NULL
);


ALTER TABLE public.users OWNER TO alertops_user;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: alertops_user
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO alertops_user;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: alertops_user
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: channels id; Type: DEFAULT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.channels ALTER COLUMN id SET DEFAULT nextval('public.channels_id_seq'::regclass);


--
-- Name: messages id; Type: DEFAULT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.messages ALTER COLUMN id SET DEFAULT nextval('public.messages_id_seq'::regclass);


--
-- Name: notifications id; Type: DEFAULT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.notifications ALTER COLUMN id SET DEFAULT nextval('public.notifications_id_seq'::regclass);


--
-- Name: reports id; Type: DEFAULT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.reports ALTER COLUMN id SET DEFAULT nextval('public.reports_id_seq'::regclass);


--
-- Name: requests id; Type: DEFAULT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.requests ALTER COLUMN id SET DEFAULT nextval('public.requests_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: channel_users; Type: TABLE DATA; Schema: public; Owner: alertops_user
--

COPY public.channel_users (user_id, channel_id) FROM stdin;
1	34
2	35
2	36
2	37
2	34
5	37
5	34
3	36
3	37
3	34
6	34
4	35
4	36
4	37
4	34
\.


--
-- Data for Name: channels; Type: TABLE DATA; Schema: public; Owner: alertops_user
--

COPY public.channels (id, name, description) FROM stdin;
34	Candidatos	Este grupo é destinado a candidatos
35	Coordenadores de Emergencia	Este grupo é destinado a coordenadores de emergência
36	Supervisores de Emergencia	Este Grupo destina se a supevisores de emergencia
37	Agentes de Emergencia	Este grupo destina se a agentes de emergencia
\.


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: alertops_user
--

COPY public.messages (id, sender_id, sender_name, recipient_id, channel_id, content, "timestamp") FROM stdin;
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: alertops_user
--

COPY public.notifications (id, user_id, message, "timestamp") FROM stdin;
\.


--
-- Data for Name: reports; Type: TABLE DATA; Schema: public; Owner: alertops_user
--

COPY public.reports (id, content, "timestamp") FROM stdin;
\.


--
-- Data for Name: requests; Type: TABLE DATA; Schema: public; Owner: alertops_user
--

COPY public.requests (id, senderid, status, type, "timestamp") FROM stdin;
6	3	ACCEPTED	ATIVACAO_DE_COMUNICACOES_DE_EMERGENCIA	2024-11-24 20:39:45.195646
7	3	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-11-24 20:44:18.633928
9	3	ACCEPTED	ATIVACAO_DE_COMUNICACOES_DE_EMERGENCIA	2024-11-25 09:19:30.465709
8	2	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-11-25 09:18:00.093798
11	2	ACCEPTED	ATIVACAO_DE_COMUNICACOES_DE_EMERGENCIA	2024-12-04 23:18:01.367
12	4	ACCEPTED	DISTRIBUICAO_DE_RECURSOS_DE_EMERGENCIA	2024-12-04 23:56:34.46
13	1	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-12-06 16:59:45.106
14	1	ACCEPTED	ATIVACAO_DE_COMUNICACOES_DE_EMERGENCIA	2024-12-06 17:00:46.741
15	1	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-12-06 17:05:14.994
16	1	ACCEPTED	ATIVACAO_DE_COMUNICACOES_DE_EMERGENCIA	2024-12-06 17:05:44.517
17	1	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-12-06 17:44:45.559
18	1	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-12-06 17:51:36.59
19	1	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-12-06 17:57:30.374
20	1	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-12-06 18:09:25.702
21	4	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-12-10 20:09:37.189
22	4	ACCEPTED	ATIVACAO_DE_COMUNICACOES_DE_EMERGENCIA	2024-12-10 20:10:15.733
23	1	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-12-11 12:34:19.178
3	3	ACCEPTED	ATIVACAO_DE_COMUNICACOES_DE_EMERGENCIA	2024-11-24 20:25:18.819702
4	3	ACCEPTED	ATIVACAO_DE_COMUNICACOES_DE_EMERGENCIA	2024-11-24 20:32:08.546977
5	3	ACCEPTED	OPERACAO_DE_EVACUACAO_EM_MASSA	2024-11-24 20:34:21.322282
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: alertops_user
--

COPY public.users (id, username, password, email, role) FROM stdin;
1	pedro	pedro	pedro	CANDIDATO
2	Xico	xico	xico	COORDENADOR_DE_EMERGENCIA
5	carlos	carlos	carlos@email.com	AGENTE_DE_EMERGENCIA
3	joao	joao	joao	SUPERVISOR_DE_EMERGENCIA
6	rita	rita	rita@email.com	CANDIDATO
4	paulo	paulo	paulo@email.com	COORDENADOR_DE_EMERGENCIA
\.


--
-- Name: channels_id_seq; Type: SEQUENCE SET; Schema: public; Owner: alertops_user
--

SELECT pg_catalog.setval('public.channels_id_seq', 37, true);


--
-- Name: messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: alertops_user
--

SELECT pg_catalog.setval('public.messages_id_seq', 71, true);


--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: alertops_user
--

SELECT pg_catalog.setval('public.notifications_id_seq', 1, false);


--
-- Name: reports_id_seq; Type: SEQUENCE SET; Schema: public; Owner: alertops_user
--

SELECT pg_catalog.setval('public.reports_id_seq', 1, false);


--
-- Name: requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: alertops_user
--

SELECT pg_catalog.setval('public.requests_id_seq', 23, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: alertops_user
--

SELECT pg_catalog.setval('public.users_id_seq', 6, true);


--
-- Name: channel_users channel_users_pkey; Type: CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.channel_users
    ADD CONSTRAINT channel_users_pkey PRIMARY KEY (user_id, channel_id);


--
-- Name: channels channels_pkey; Type: CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.channels
    ADD CONSTRAINT channels_pkey PRIMARY KEY (id);


--
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: reports reports_pkey; Type: CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (id);


--
-- Name: requests requests_pkey; Type: CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: channel_users channel_users_channel_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.channel_users
    ADD CONSTRAINT channel_users_channel_id_fkey FOREIGN KEY (channel_id) REFERENCES public.channels(id) ON DELETE CASCADE;


--
-- Name: channel_users channel_users_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.channel_users
    ADD CONSTRAINT channel_users_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: notifications notifications_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: alertops_user
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

