toc.dat                                                                                             0000600 0004000 0002000 00000052271 14727605711 0014460 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        PGDMP       !                |            postgres    17.2    17.2 J    `           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false         a           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false         b           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false         c           1262    5    postgres    DATABASE     {   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_India.1252';
    DROP DATABASE postgres;
                     postgres    false         d           0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                        postgres    false    4963                     2615    16395    admin    SCHEMA        CREATE SCHEMA admin;
    DROP SCHEMA admin;
                     postgres    false         �            1259    16435    billdetails    TABLE     �   CREATE TABLE admin.billdetails (
    id bigint NOT NULL,
    billnumber integer,
    tablenumber character varying(255),
    fooditemname character varying(255),
    fooditemquantity integer,
    fooditemprice integer
);
    DROP TABLE admin.billdetails;
       admin         heap r       postgres    false    6         �            1259    16456    billdetails_id    SEQUENCE     v   CREATE SEQUENCE admin.billdetails_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE admin.billdetails_id;
       admin               postgres    false    6    221         e           0    0    billdetails_id    SEQUENCE OWNED BY     C   ALTER SEQUENCE admin.billdetails_id OWNED BY admin.billdetails.id;
          admin               postgres    false    224         �            1259    16413    bills    TABLE     �  CREATE TABLE admin.bills (
    billnumber integer NOT NULL,
    billdate text,
    tablenumber text,
    outletname text,
    outletaddress text,
    gstnumber text,
    servicecharge integer,
    sgst numeric,
    cgst numeric,
    discount numeric,
    subtotal numeric,
    total numeric,
    grandtotal numeric,
    pax integer,
    nextbillnumber integer,
    modeofpayment text,
    status text
);
    DROP TABLE admin.bills;
       admin         heap r       postgres    false    6         �            1259    16412    bills_billnumber_seq    SEQUENCE     �   CREATE SEQUENCE admin.bills_billnumber_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 *   DROP SEQUENCE admin.bills_billnumber_seq;
       admin               postgres    false    220    6         f           0    0    bills_billnumber_seq    SEQUENCE OWNED BY     K   ALTER SEQUENCE admin.bills_billnumber_seq OWNED BY admin.bills.billnumber;
          admin               postgres    false    219         �            1259    16488    billsettings    TABLE     �   CREATE TABLE admin.billsettings (
    modeofpayment text NOT NULL,
    defaultmodeofpayment text,
    maxdiscount numeric,
    defaultdiscount numeric,
    gst numeric,
    vat numeric,
    servicecharge numeric
);
    DROP TABLE admin.billsettings;
       admin         heap r       postgres    false    6         �            1259    16503 	   inventory    TABLE        CREATE TABLE admin.inventory (
    id bigint NOT NULL,
    name text,
    rate integer,
    unit text,
    quantity integer
);
    DROP TABLE admin.inventory;
       admin         heap r       postgres    false    6         �            1259    16502    inventory_id_seq    SEQUENCE     x   CREATE SEQUENCE admin.inventory_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE admin.inventory_id_seq;
       admin               postgres    false    233    6         g           0    0    inventory_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE admin.inventory_id_seq OWNED BY admin.inventory.id;
          admin               postgres    false    232         �            1259    16512    inventorypurchases    TABLE     �   CREATE TABLE admin.inventorypurchases (
    purchaseid bigint NOT NULL,
    inventoryitemid integer,
    inventoryitemname text,
    inventoryitemcost integer,
    inventoryitemquantity integer,
    inventoryitemunit text,
    purchasedate text
);
 %   DROP TABLE admin.inventorypurchases;
       admin         heap r       postgres    false    6         �            1259    16511     inventorypurchase_purchaseid_seq    SEQUENCE     �   CREATE SEQUENCE admin.inventorypurchase_purchaseid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 6   DROP SEQUENCE admin.inventorypurchase_purchaseid_seq;
       admin               postgres    false    235    6         h           0    0     inventorypurchase_purchaseid_seq    SEQUENCE OWNED BY     d   ALTER SEQUENCE admin.inventorypurchase_purchaseid_seq OWNED BY admin.inventorypurchases.purchaseid;
          admin               postgres    false    234         �            1259    16458    menu    TABLE       CREATE TABLE admin.menu (
    id bigint NOT NULL,
    fooditemname text,
    fooditemcategory text,
    fooditemprice integer,
    fooditemavailability boolean,
    variants text,
    istracked text,
    isvariantexists text,
    isinventorytracked text,
    stockquantity text
);
    DROP TABLE admin.menu;
       admin         heap r       postgres    false    6         �            1259    16457    menu_id_seq    SEQUENCE     s   CREATE SEQUENCE admin.menu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 !   DROP SEQUENCE admin.menu_id_seq;
       admin               postgres    false    6    226         i           0    0    menu_id_seq    SEQUENCE OWNED BY     9   ALTER SEQUENCE admin.menu_id_seq OWNED BY admin.menu.id;
          admin               postgres    false    225         �            1259    16449    opentabledetails    TABLE     �   CREATE TABLE admin.opentabledetails (
    tablenumber text NOT NULL,
    fooditemname text NOT NULL,
    fooditemquantity integer,
    fooditemprice integer,
    variant text
);
 #   DROP TABLE admin.opentabledetails;
       admin         heap r       postgres    false    6         �            1259    16467    outletdetails    TABLE        CREATE TABLE admin.outletdetails (
    outletname text NOT NULL,
    outletaddress text,
    gstnumber text,
    phone text
);
     DROP TABLE admin.outletdetails;
       admin         heap r       postgres    false    6         �            1259    16536    recipe    TABLE     e   CREATE TABLE admin.recipe (
    menuitemid text NOT NULL,
    variant text,
    rawmaterials text
);
    DROP TABLE admin.recipe;
       admin         heap r       postgres    false    6         �            1259    16481    reservedtables    TABLE     C   CREATE TABLE admin.reservedtables (
    tablename text NOT NULL
);
 !   DROP TABLE admin.reservedtables;
       admin         heap r       postgres    false    6         �            1259    16474    tableandarea    TABLE     P   CREATE TABLE admin.tableandarea (
    area text NOT NULL,
    tables integer
);
    DROP TABLE admin.tableandarea;
       admin         heap r       postgres    false    6         �            1259    16495    takeawayordersdetails    TABLE     �   CREATE TABLE admin.takeawayordersdetails (
    ordernumber integer NOT NULL,
    fooditemname text NOT NULL,
    fooditemquantity integer,
    fooditemprice numeric,
    status text,
    variant text
);
 (   DROP TABLE admin.takeawayordersdetails;
       admin         heap r       postgres    false    6         �            1259    16521    variants    TABLE     �   CREATE TABLE admin.variants (
    id bigint NOT NULL,
    menuitemid integer,
    variantname text,
    variantprice double precision,
    stockquantity text
);
    DROP TABLE admin.variants;
       admin         heap r       postgres    false    6         �            1259    16520    variants_id_seq    SEQUENCE     w   CREATE SEQUENCE admin.variants_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE admin.variants_id_seq;
       admin               postgres    false    237    6         j           0    0    variants_id_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE admin.variants_id_seq OWNED BY admin.variants.id;
          admin               postgres    false    236         �            1259    16442    opentabledetails    TABLE     �   CREATE TABLE public.opentabledetails (
    tablenumber text NOT NULL,
    fooditemname text NOT NULL,
    fooditemquantity integer,
    fooditemprice integer,
    variant text
);
 $   DROP TABLE public.opentabledetails;
       public         heap r       postgres    false         �            1259    16388    tenants    TABLE     �   CREATE TABLE public.tenants (
    username text NOT NULL,
    password text,
    subscriptionstartdate text,
    subscriptionenddate text
);
    DROP TABLE public.tenants;
       public         heap r       postgres    false         �           2604    16416    bills billnumber    DEFAULT     r   ALTER TABLE ONLY admin.bills ALTER COLUMN billnumber SET DEFAULT nextval('admin.bills_billnumber_seq'::regclass);
 >   ALTER TABLE admin.bills ALTER COLUMN billnumber DROP DEFAULT;
       admin               postgres    false    219    220    220         �           2604    16506    inventory id    DEFAULT     j   ALTER TABLE ONLY admin.inventory ALTER COLUMN id SET DEFAULT nextval('admin.inventory_id_seq'::regclass);
 :   ALTER TABLE admin.inventory ALTER COLUMN id DROP DEFAULT;
       admin               postgres    false    233    232    233         �           2604    16515    inventorypurchases purchaseid    DEFAULT     �   ALTER TABLE ONLY admin.inventorypurchases ALTER COLUMN purchaseid SET DEFAULT nextval('admin.inventorypurchase_purchaseid_seq'::regclass);
 K   ALTER TABLE admin.inventorypurchases ALTER COLUMN purchaseid DROP DEFAULT;
       admin               postgres    false    234    235    235         �           2604    16461    menu id    DEFAULT     `   ALTER TABLE ONLY admin.menu ALTER COLUMN id SET DEFAULT nextval('admin.menu_id_seq'::regclass);
 5   ALTER TABLE admin.menu ALTER COLUMN id DROP DEFAULT;
       admin               postgres    false    226    225    226         �           2604    16524    variants id    DEFAULT     h   ALTER TABLE ONLY admin.variants ALTER COLUMN id SET DEFAULT nextval('admin.variants_id_seq'::regclass);
 9   ALTER TABLE admin.variants ALTER COLUMN id DROP DEFAULT;
       admin               postgres    false    237    236    237         L          0    16435    billdetails 
   TABLE DATA           p   COPY admin.billdetails (id, billnumber, tablenumber, fooditemname, fooditemquantity, fooditemprice) FROM stdin;
    admin               postgres    false    221       4940.dat K          0    16413    bills 
   TABLE DATA           �   COPY admin.bills (billnumber, billdate, tablenumber, outletname, outletaddress, gstnumber, servicecharge, sgst, cgst, discount, subtotal, total, grandtotal, pax, nextbillnumber, modeofpayment, status) FROM stdin;
    admin               postgres    false    220       4939.dat U          0    16488    billsettings 
   TABLE DATA           �   COPY admin.billsettings (modeofpayment, defaultmodeofpayment, maxdiscount, defaultdiscount, gst, vat, servicecharge) FROM stdin;
    admin               postgres    false    230       4949.dat X          0    16503 	   inventory 
   TABLE DATA           B   COPY admin.inventory (id, name, rate, unit, quantity) FROM stdin;
    admin               postgres    false    233       4952.dat Z          0    16512    inventorypurchases 
   TABLE DATA           �   COPY admin.inventorypurchases (purchaseid, inventoryitemid, inventoryitemname, inventoryitemcost, inventoryitemquantity, inventoryitemunit, purchasedate) FROM stdin;
    admin               postgres    false    235       4954.dat Q          0    16458    menu 
   TABLE DATA           �   COPY admin.menu (id, fooditemname, fooditemcategory, fooditemprice, fooditemavailability, variants, istracked, isvariantexists, isinventorytracked, stockquantity) FROM stdin;
    admin               postgres    false    226       4945.dat N          0    16449    opentabledetails 
   TABLE DATA           n   COPY admin.opentabledetails (tablenumber, fooditemname, fooditemquantity, fooditemprice, variant) FROM stdin;
    admin               postgres    false    223       4942.dat R          0    16467    outletdetails 
   TABLE DATA           S   COPY admin.outletdetails (outletname, outletaddress, gstnumber, phone) FROM stdin;
    admin               postgres    false    227       4946.dat ]          0    16536    recipe 
   TABLE DATA           B   COPY admin.recipe (menuitemid, variant, rawmaterials) FROM stdin;
    admin               postgres    false    238       4957.dat T          0    16481    reservedtables 
   TABLE DATA           2   COPY admin.reservedtables (tablename) FROM stdin;
    admin               postgres    false    229       4948.dat S          0    16474    tableandarea 
   TABLE DATA           3   COPY admin.tableandarea (area, tables) FROM stdin;
    admin               postgres    false    228       4947.dat V          0    16495    takeawayordersdetails 
   TABLE DATA           {   COPY admin.takeawayordersdetails (ordernumber, fooditemname, fooditemquantity, fooditemprice, status, variant) FROM stdin;
    admin               postgres    false    231       4950.dat \          0    16521    variants 
   TABLE DATA           [   COPY admin.variants (id, menuitemid, variantname, variantprice, stockquantity) FROM stdin;
    admin               postgres    false    237       4956.dat M          0    16442    opentabledetails 
   TABLE DATA           o   COPY public.opentabledetails (tablenumber, fooditemname, fooditemquantity, fooditemprice, variant) FROM stdin;
    public               postgres    false    222       4941.dat I          0    16388    tenants 
   TABLE DATA           a   COPY public.tenants (username, password, subscriptionstartdate, subscriptionenddate) FROM stdin;
    public               postgres    false    218       4937.dat k           0    0    billdetails_id    SEQUENCE SET     <   SELECT pg_catalog.setval('admin.billdetails_id', 1, false);
          admin               postgres    false    224         l           0    0    bills_billnumber_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('admin.bills_billnumber_seq', 1, true);
          admin               postgres    false    219         m           0    0    inventory_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('admin.inventory_id_seq', 5, true);
          admin               postgres    false    232         n           0    0     inventorypurchase_purchaseid_seq    SEQUENCE SET     M   SELECT pg_catalog.setval('admin.inventorypurchase_purchaseid_seq', 2, true);
          admin               postgres    false    234         o           0    0    menu_id_seq    SEQUENCE SET     8   SELECT pg_catalog.setval('admin.menu_id_seq', 7, true);
          admin               postgres    false    225         p           0    0    variants_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('admin.variants_id_seq', 3, true);
          admin               postgres    false    236         �           2606    16441    billdetails billdetails_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY admin.billdetails
    ADD CONSTRAINT billdetails_pkey PRIMARY KEY (id);
 E   ALTER TABLE ONLY admin.billdetails DROP CONSTRAINT billdetails_pkey;
       admin                 postgres    false    221         �           2606    16420    bills bills_pkey 
   CONSTRAINT     U   ALTER TABLE ONLY admin.bills
    ADD CONSTRAINT bills_pkey PRIMARY KEY (billnumber);
 9   ALTER TABLE ONLY admin.bills DROP CONSTRAINT bills_pkey;
       admin                 postgres    false    220         �           2606    16494    billsettings billsettings_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY admin.billsettings
    ADD CONSTRAINT billsettings_pkey PRIMARY KEY (modeofpayment);
 G   ALTER TABLE ONLY admin.billsettings DROP CONSTRAINT billsettings_pkey;
       admin                 postgres    false    230         �           2606    16510    inventory inventory_pkey 
   CONSTRAINT     U   ALTER TABLE ONLY admin.inventory
    ADD CONSTRAINT inventory_pkey PRIMARY KEY (id);
 A   ALTER TABLE ONLY admin.inventory DROP CONSTRAINT inventory_pkey;
       admin                 postgres    false    233         �           2606    16519 )   inventorypurchases inventorypurchase_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY admin.inventorypurchases
    ADD CONSTRAINT inventorypurchase_pkey PRIMARY KEY (purchaseid);
 R   ALTER TABLE ONLY admin.inventorypurchases DROP CONSTRAINT inventorypurchase_pkey;
       admin                 postgres    false    235         �           2606    16465    menu menu_pkey 
   CONSTRAINT     K   ALTER TABLE ONLY admin.menu
    ADD CONSTRAINT menu_pkey PRIMARY KEY (id);
 7   ALTER TABLE ONLY admin.menu DROP CONSTRAINT menu_pkey;
       admin                 postgres    false    226         �           2606    16455 &   opentabledetails opentabledetails_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY admin.opentabledetails
    ADD CONSTRAINT opentabledetails_pkey PRIMARY KEY (tablenumber, fooditemname);
 O   ALTER TABLE ONLY admin.opentabledetails DROP CONSTRAINT opentabledetails_pkey;
       admin                 postgres    false    223    223         �           2606    16473     outletdetails outletdetails_pkey 
   CONSTRAINT     e   ALTER TABLE ONLY admin.outletdetails
    ADD CONSTRAINT outletdetails_pkey PRIMARY KEY (outletname);
 I   ALTER TABLE ONLY admin.outletdetails DROP CONSTRAINT outletdetails_pkey;
       admin                 postgres    false    227         �           2606    16542    recipe recipe_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY admin.recipe
    ADD CONSTRAINT recipe_pkey PRIMARY KEY (menuitemid);
 ;   ALTER TABLE ONLY admin.recipe DROP CONSTRAINT recipe_pkey;
       admin                 postgres    false    238         �           2606    16487 "   reservedtables reservedtables_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY admin.reservedtables
    ADD CONSTRAINT reservedtables_pkey PRIMARY KEY (tablename);
 K   ALTER TABLE ONLY admin.reservedtables DROP CONSTRAINT reservedtables_pkey;
       admin                 postgres    false    229         �           2606    16480    tableandarea tableandarea_pkey 
   CONSTRAINT     ]   ALTER TABLE ONLY admin.tableandarea
    ADD CONSTRAINT tableandarea_pkey PRIMARY KEY (area);
 G   ALTER TABLE ONLY admin.tableandarea DROP CONSTRAINT tableandarea_pkey;
       admin                 postgres    false    228         �           2606    16501 /   takeawayordersdetails takeawayorderdetails_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY admin.takeawayordersdetails
    ADD CONSTRAINT takeawayorderdetails_pkey PRIMARY KEY (ordernumber, fooditemname);
 X   ALTER TABLE ONLY admin.takeawayordersdetails DROP CONSTRAINT takeawayorderdetails_pkey;
       admin                 postgres    false    231    231         �           2606    16528    variants variants_pkey 
   CONSTRAINT     S   ALTER TABLE ONLY admin.variants
    ADD CONSTRAINT variants_pkey PRIMARY KEY (id);
 ?   ALTER TABLE ONLY admin.variants DROP CONSTRAINT variants_pkey;
       admin                 postgres    false    237         �           2606    16448 &   opentabledetails opentabledetails_pkey 
   CONSTRAINT     {   ALTER TABLE ONLY public.opentabledetails
    ADD CONSTRAINT opentabledetails_pkey PRIMARY KEY (tablenumber, fooditemname);
 P   ALTER TABLE ONLY public.opentabledetails DROP CONSTRAINT opentabledetails_pkey;
       public                 postgres    false    222    222         �           2606    16394    tenants tenants_pkey 
   CONSTRAINT     X   ALTER TABLE ONLY public.tenants
    ADD CONSTRAINT tenants_pkey PRIMARY KEY (username);
 >   ALTER TABLE ONLY public.tenants DROP CONSTRAINT tenants_pkey;
       public                 postgres    false    218                                                                                                                                                                                                                                                                                                                                               4940.dat                                                                                            0000600 0004000 0002000 00000000005 14727605711 0014257 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           4939.dat                                                                                            0000600 0004000 0002000 00000000457 14727605711 0014302 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        1	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	1	\N	SUCCESS
2	00:41 03/12/2024	TAKE AWAY	\N	\N	\N	0	0	0	0	40	40	40	\N	\N		SUCCESS
0	00:33 03/12/2024	Table 3	\N	\N	\N	0	0	0	0	240	240	240	\N	4		SUCCESS
3	00:32 09/12/2024	Table 2	Uss Da Dhaba	Race Course	180076123	8	5.5	5.5	0	40	40	47.6	\N	\N	CARD	SUCCESS
\.


                                                                                                                                                                                                                 4949.dat                                                                                            0000600 0004000 0002000 00000000044 14727605711 0014273 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        CARD,CASH,UPI	CARD	10	0	11	2	8
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            4952.dat                                                                                            0000600 0004000 0002000 00000000042 14727605711 0014263 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        4	atta	0	kg	30
5	pasta	0	1	0
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              4954.dat                                                                                            0000600 0004000 0002000 00000000041 14727605711 0014264 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        1	4	atta	50	5	kg	2024-12-09
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               4945.dat                                                                                            0000600 0004000 0002000 00000000415 14727605711 0014271 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        2	pizza	Fast food	250	t	\N	\N	YES	OFF	0.0
3	Pasta	Fast food	150	t	\N	\N	NO	OFF	0.0
4	Chowmein	Fast food	30	t	\N	\N	YES	OFF	0.0
5	sandwich	Fast food	40	t	\N	\N	YES	OFF	0.0
6	Parantha	breakfast	40	t	\N	\N	YES	OFF	0.0
7	Garlic Bread	Fast food	200	t	\N	\N	YES	OFF	0.0
\.


                                                                                                                                                                                                                                                   4942.dat                                                                                            0000600 0004000 0002000 00000000102 14727605711 0014257 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        Table 4	Parantha (Aloo parantha)	1	40	{"Aloo parantha":40.0}
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                              4946.dat                                                                                            0000600 0004000 0002000 00000000063 14727605711 0014271 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        Uss Da Dhaba	Race Course	180076123	8871978821
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                             4957.dat                                                                                            0000600 0004000 0002000 00000000005 14727605711 0014267 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           4948.dat                                                                                            0000600 0004000 0002000 00000000005 14727605711 0014267 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           4947.dat                                                                                            0000600 0004000 0002000 00000000037 14727605711 0014273 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        Rooftop	5
Swimming Pool	2
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 4950.dat                                                                                            0000600 0004000 0002000 00000000005 14727605711 0014260 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           4956.dat                                                                                            0000600 0004000 0002000 00000000076 14727605711 0014276 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        1	5	cheese	50	0
2	6	Aloo parantha	40	0
3	7	Stuffed	230	0
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                  4941.dat                                                                                            0000600 0004000 0002000 00000000005 14727605711 0014260 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           4937.dat                                                                                            0000600 0004000 0002000 00000000063 14727605711 0014271 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        admin	admin	03:00 01/01/2024	03:00 01/01/2027
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                             restore.sql                                                                                         0000600 0004000 0002000 00000044262 14727605711 0015406 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        --
-- NOTE:
--
-- File paths need to be edited. Search for $$PATH$$ and
-- replace it with the path to the directory containing
-- the extracted data files.
--
--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2
-- Dumped by pg_dump version 17.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE postgres;
--
-- Name: postgres; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_India.1252';


ALTER DATABASE postgres OWNER TO postgres;

\connect postgres

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: DATABASE postgres; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON DATABASE postgres IS 'default administrative connection database';


--
-- Name: admin; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA admin;


ALTER SCHEMA admin OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: billdetails; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.billdetails (
    id bigint NOT NULL,
    billnumber integer,
    tablenumber character varying(255),
    fooditemname character varying(255),
    fooditemquantity integer,
    fooditemprice integer
);


ALTER TABLE admin.billdetails OWNER TO postgres;

--
-- Name: billdetails_id; Type: SEQUENCE; Schema: admin; Owner: postgres
--

CREATE SEQUENCE admin.billdetails_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE admin.billdetails_id OWNER TO postgres;

--
-- Name: billdetails_id; Type: SEQUENCE OWNED BY; Schema: admin; Owner: postgres
--

ALTER SEQUENCE admin.billdetails_id OWNED BY admin.billdetails.id;


--
-- Name: bills; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.bills (
    billnumber integer NOT NULL,
    billdate text,
    tablenumber text,
    outletname text,
    outletaddress text,
    gstnumber text,
    servicecharge integer,
    sgst numeric,
    cgst numeric,
    discount numeric,
    subtotal numeric,
    total numeric,
    grandtotal numeric,
    pax integer,
    nextbillnumber integer,
    modeofpayment text,
    status text
);


ALTER TABLE admin.bills OWNER TO postgres;

--
-- Name: bills_billnumber_seq; Type: SEQUENCE; Schema: admin; Owner: postgres
--

CREATE SEQUENCE admin.bills_billnumber_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE admin.bills_billnumber_seq OWNER TO postgres;

--
-- Name: bills_billnumber_seq; Type: SEQUENCE OWNED BY; Schema: admin; Owner: postgres
--

ALTER SEQUENCE admin.bills_billnumber_seq OWNED BY admin.bills.billnumber;


--
-- Name: billsettings; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.billsettings (
    modeofpayment text NOT NULL,
    defaultmodeofpayment text,
    maxdiscount numeric,
    defaultdiscount numeric,
    gst numeric,
    vat numeric,
    servicecharge numeric
);


ALTER TABLE admin.billsettings OWNER TO postgres;

--
-- Name: inventory; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.inventory (
    id bigint NOT NULL,
    name text,
    rate integer,
    unit text,
    quantity integer
);


ALTER TABLE admin.inventory OWNER TO postgres;

--
-- Name: inventory_id_seq; Type: SEQUENCE; Schema: admin; Owner: postgres
--

CREATE SEQUENCE admin.inventory_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE admin.inventory_id_seq OWNER TO postgres;

--
-- Name: inventory_id_seq; Type: SEQUENCE OWNED BY; Schema: admin; Owner: postgres
--

ALTER SEQUENCE admin.inventory_id_seq OWNED BY admin.inventory.id;


--
-- Name: inventorypurchases; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.inventorypurchases (
    purchaseid bigint NOT NULL,
    inventoryitemid integer,
    inventoryitemname text,
    inventoryitemcost integer,
    inventoryitemquantity integer,
    inventoryitemunit text,
    purchasedate text
);


ALTER TABLE admin.inventorypurchases OWNER TO postgres;

--
-- Name: inventorypurchase_purchaseid_seq; Type: SEQUENCE; Schema: admin; Owner: postgres
--

CREATE SEQUENCE admin.inventorypurchase_purchaseid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE admin.inventorypurchase_purchaseid_seq OWNER TO postgres;

--
-- Name: inventorypurchase_purchaseid_seq; Type: SEQUENCE OWNED BY; Schema: admin; Owner: postgres
--

ALTER SEQUENCE admin.inventorypurchase_purchaseid_seq OWNED BY admin.inventorypurchases.purchaseid;


--
-- Name: menu; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.menu (
    id bigint NOT NULL,
    fooditemname text,
    fooditemcategory text,
    fooditemprice integer,
    fooditemavailability boolean,
    variants text,
    istracked text,
    isvariantexists text,
    isinventorytracked text,
    stockquantity text
);


ALTER TABLE admin.menu OWNER TO postgres;

--
-- Name: menu_id_seq; Type: SEQUENCE; Schema: admin; Owner: postgres
--

CREATE SEQUENCE admin.menu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE admin.menu_id_seq OWNER TO postgres;

--
-- Name: menu_id_seq; Type: SEQUENCE OWNED BY; Schema: admin; Owner: postgres
--

ALTER SEQUENCE admin.menu_id_seq OWNED BY admin.menu.id;


--
-- Name: opentabledetails; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.opentabledetails (
    tablenumber text NOT NULL,
    fooditemname text NOT NULL,
    fooditemquantity integer,
    fooditemprice integer,
    variant text
);


ALTER TABLE admin.opentabledetails OWNER TO postgres;

--
-- Name: outletdetails; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.outletdetails (
    outletname text NOT NULL,
    outletaddress text,
    gstnumber text,
    phone text
);


ALTER TABLE admin.outletdetails OWNER TO postgres;

--
-- Name: recipe; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.recipe (
    menuitemid text NOT NULL,
    variant text,
    rawmaterials text
);


ALTER TABLE admin.recipe OWNER TO postgres;

--
-- Name: reservedtables; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.reservedtables (
    tablename text NOT NULL
);


ALTER TABLE admin.reservedtables OWNER TO postgres;

--
-- Name: tableandarea; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.tableandarea (
    area text NOT NULL,
    tables integer
);


ALTER TABLE admin.tableandarea OWNER TO postgres;

--
-- Name: takeawayordersdetails; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.takeawayordersdetails (
    ordernumber integer NOT NULL,
    fooditemname text NOT NULL,
    fooditemquantity integer,
    fooditemprice numeric,
    status text,
    variant text
);


ALTER TABLE admin.takeawayordersdetails OWNER TO postgres;

--
-- Name: variants; Type: TABLE; Schema: admin; Owner: postgres
--

CREATE TABLE admin.variants (
    id bigint NOT NULL,
    menuitemid integer,
    variantname text,
    variantprice double precision,
    stockquantity text
);


ALTER TABLE admin.variants OWNER TO postgres;

--
-- Name: variants_id_seq; Type: SEQUENCE; Schema: admin; Owner: postgres
--

CREATE SEQUENCE admin.variants_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE admin.variants_id_seq OWNER TO postgres;

--
-- Name: variants_id_seq; Type: SEQUENCE OWNED BY; Schema: admin; Owner: postgres
--

ALTER SEQUENCE admin.variants_id_seq OWNED BY admin.variants.id;


--
-- Name: opentabledetails; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.opentabledetails (
    tablenumber text NOT NULL,
    fooditemname text NOT NULL,
    fooditemquantity integer,
    fooditemprice integer,
    variant text
);


ALTER TABLE public.opentabledetails OWNER TO postgres;

--
-- Name: tenants; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tenants (
    username text NOT NULL,
    password text,
    subscriptionstartdate text,
    subscriptionenddate text
);


ALTER TABLE public.tenants OWNER TO postgres;

--
-- Name: bills billnumber; Type: DEFAULT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.bills ALTER COLUMN billnumber SET DEFAULT nextval('admin.bills_billnumber_seq'::regclass);


--
-- Name: inventory id; Type: DEFAULT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.inventory ALTER COLUMN id SET DEFAULT nextval('admin.inventory_id_seq'::regclass);


--
-- Name: inventorypurchases purchaseid; Type: DEFAULT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.inventorypurchases ALTER COLUMN purchaseid SET DEFAULT nextval('admin.inventorypurchase_purchaseid_seq'::regclass);


--
-- Name: menu id; Type: DEFAULT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.menu ALTER COLUMN id SET DEFAULT nextval('admin.menu_id_seq'::regclass);


--
-- Name: variants id; Type: DEFAULT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.variants ALTER COLUMN id SET DEFAULT nextval('admin.variants_id_seq'::regclass);


--
-- Data for Name: billdetails; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.billdetails (id, billnumber, tablenumber, fooditemname, fooditemquantity, fooditemprice) FROM stdin;
\.
COPY admin.billdetails (id, billnumber, tablenumber, fooditemname, fooditemquantity, fooditemprice) FROM '$$PATH$$/4940.dat';

--
-- Data for Name: bills; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.bills (billnumber, billdate, tablenumber, outletname, outletaddress, gstnumber, servicecharge, sgst, cgst, discount, subtotal, total, grandtotal, pax, nextbillnumber, modeofpayment, status) FROM stdin;
\.
COPY admin.bills (billnumber, billdate, tablenumber, outletname, outletaddress, gstnumber, servicecharge, sgst, cgst, discount, subtotal, total, grandtotal, pax, nextbillnumber, modeofpayment, status) FROM '$$PATH$$/4939.dat';

--
-- Data for Name: billsettings; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.billsettings (modeofpayment, defaultmodeofpayment, maxdiscount, defaultdiscount, gst, vat, servicecharge) FROM stdin;
\.
COPY admin.billsettings (modeofpayment, defaultmodeofpayment, maxdiscount, defaultdiscount, gst, vat, servicecharge) FROM '$$PATH$$/4949.dat';

--
-- Data for Name: inventory; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.inventory (id, name, rate, unit, quantity) FROM stdin;
\.
COPY admin.inventory (id, name, rate, unit, quantity) FROM '$$PATH$$/4952.dat';

--
-- Data for Name: inventorypurchases; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.inventorypurchases (purchaseid, inventoryitemid, inventoryitemname, inventoryitemcost, inventoryitemquantity, inventoryitemunit, purchasedate) FROM stdin;
\.
COPY admin.inventorypurchases (purchaseid, inventoryitemid, inventoryitemname, inventoryitemcost, inventoryitemquantity, inventoryitemunit, purchasedate) FROM '$$PATH$$/4954.dat';

--
-- Data for Name: menu; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.menu (id, fooditemname, fooditemcategory, fooditemprice, fooditemavailability, variants, istracked, isvariantexists, isinventorytracked, stockquantity) FROM stdin;
\.
COPY admin.menu (id, fooditemname, fooditemcategory, fooditemprice, fooditemavailability, variants, istracked, isvariantexists, isinventorytracked, stockquantity) FROM '$$PATH$$/4945.dat';

--
-- Data for Name: opentabledetails; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.opentabledetails (tablenumber, fooditemname, fooditemquantity, fooditemprice, variant) FROM stdin;
\.
COPY admin.opentabledetails (tablenumber, fooditemname, fooditemquantity, fooditemprice, variant) FROM '$$PATH$$/4942.dat';

--
-- Data for Name: outletdetails; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.outletdetails (outletname, outletaddress, gstnumber, phone) FROM stdin;
\.
COPY admin.outletdetails (outletname, outletaddress, gstnumber, phone) FROM '$$PATH$$/4946.dat';

--
-- Data for Name: recipe; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.recipe (menuitemid, variant, rawmaterials) FROM stdin;
\.
COPY admin.recipe (menuitemid, variant, rawmaterials) FROM '$$PATH$$/4957.dat';

--
-- Data for Name: reservedtables; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.reservedtables (tablename) FROM stdin;
\.
COPY admin.reservedtables (tablename) FROM '$$PATH$$/4948.dat';

--
-- Data for Name: tableandarea; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.tableandarea (area, tables) FROM stdin;
\.
COPY admin.tableandarea (area, tables) FROM '$$PATH$$/4947.dat';

--
-- Data for Name: takeawayordersdetails; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.takeawayordersdetails (ordernumber, fooditemname, fooditemquantity, fooditemprice, status, variant) FROM stdin;
\.
COPY admin.takeawayordersdetails (ordernumber, fooditemname, fooditemquantity, fooditemprice, status, variant) FROM '$$PATH$$/4950.dat';

--
-- Data for Name: variants; Type: TABLE DATA; Schema: admin; Owner: postgres
--

COPY admin.variants (id, menuitemid, variantname, variantprice, stockquantity) FROM stdin;
\.
COPY admin.variants (id, menuitemid, variantname, variantprice, stockquantity) FROM '$$PATH$$/4956.dat';

--
-- Data for Name: opentabledetails; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.opentabledetails (tablenumber, fooditemname, fooditemquantity, fooditemprice, variant) FROM stdin;
\.
COPY public.opentabledetails (tablenumber, fooditemname, fooditemquantity, fooditemprice, variant) FROM '$$PATH$$/4941.dat';

--
-- Data for Name: tenants; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tenants (username, password, subscriptionstartdate, subscriptionenddate) FROM stdin;
\.
COPY public.tenants (username, password, subscriptionstartdate, subscriptionenddate) FROM '$$PATH$$/4937.dat';

--
-- Name: billdetails_id; Type: SEQUENCE SET; Schema: admin; Owner: postgres
--

SELECT pg_catalog.setval('admin.billdetails_id', 1, false);


--
-- Name: bills_billnumber_seq; Type: SEQUENCE SET; Schema: admin; Owner: postgres
--

SELECT pg_catalog.setval('admin.bills_billnumber_seq', 1, true);


--
-- Name: inventory_id_seq; Type: SEQUENCE SET; Schema: admin; Owner: postgres
--

SELECT pg_catalog.setval('admin.inventory_id_seq', 5, true);


--
-- Name: inventorypurchase_purchaseid_seq; Type: SEQUENCE SET; Schema: admin; Owner: postgres
--

SELECT pg_catalog.setval('admin.inventorypurchase_purchaseid_seq', 2, true);


--
-- Name: menu_id_seq; Type: SEQUENCE SET; Schema: admin; Owner: postgres
--

SELECT pg_catalog.setval('admin.menu_id_seq', 7, true);


--
-- Name: variants_id_seq; Type: SEQUENCE SET; Schema: admin; Owner: postgres
--

SELECT pg_catalog.setval('admin.variants_id_seq', 3, true);


--
-- Name: billdetails billdetails_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.billdetails
    ADD CONSTRAINT billdetails_pkey PRIMARY KEY (id);


--
-- Name: bills bills_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.bills
    ADD CONSTRAINT bills_pkey PRIMARY KEY (billnumber);


--
-- Name: billsettings billsettings_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.billsettings
    ADD CONSTRAINT billsettings_pkey PRIMARY KEY (modeofpayment);


--
-- Name: inventory inventory_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.inventory
    ADD CONSTRAINT inventory_pkey PRIMARY KEY (id);


--
-- Name: inventorypurchases inventorypurchase_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.inventorypurchases
    ADD CONSTRAINT inventorypurchase_pkey PRIMARY KEY (purchaseid);


--
-- Name: menu menu_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.menu
    ADD CONSTRAINT menu_pkey PRIMARY KEY (id);


--
-- Name: opentabledetails opentabledetails_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.opentabledetails
    ADD CONSTRAINT opentabledetails_pkey PRIMARY KEY (tablenumber, fooditemname);


--
-- Name: outletdetails outletdetails_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.outletdetails
    ADD CONSTRAINT outletdetails_pkey PRIMARY KEY (outletname);


--
-- Name: recipe recipe_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.recipe
    ADD CONSTRAINT recipe_pkey PRIMARY KEY (menuitemid);


--
-- Name: reservedtables reservedtables_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.reservedtables
    ADD CONSTRAINT reservedtables_pkey PRIMARY KEY (tablename);


--
-- Name: tableandarea tableandarea_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.tableandarea
    ADD CONSTRAINT tableandarea_pkey PRIMARY KEY (area);


--
-- Name: takeawayordersdetails takeawayorderdetails_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.takeawayordersdetails
    ADD CONSTRAINT takeawayorderdetails_pkey PRIMARY KEY (ordernumber, fooditemname);


--
-- Name: variants variants_pkey; Type: CONSTRAINT; Schema: admin; Owner: postgres
--

ALTER TABLE ONLY admin.variants
    ADD CONSTRAINT variants_pkey PRIMARY KEY (id);


--
-- Name: opentabledetails opentabledetails_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.opentabledetails
    ADD CONSTRAINT opentabledetails_pkey PRIMARY KEY (tablenumber, fooditemname);


--
-- Name: tenants tenants_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tenants
    ADD CONSTRAINT tenants_pkey PRIMARY KEY (username);


--
-- PostgreSQL database dump complete
--

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              