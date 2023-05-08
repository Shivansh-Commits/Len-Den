-----------------------------------
LEN-DEN PROJECT DETAILS
-----------------------------------

-------------------
[ TECHNOLOGY ]
-------------------
LANGUAGE USED - JAVA

BUILD & COMPILE - MAVEN

UI LIBRARY - JAVAFX

IDE USED - IntelliJ

UI DESIGNER TOOL - Scene Builder


-------------------
[ DATABASE ]
-------------------

DB USED - POSTGRES DATABASE

STUCTURE DESCRIPTION -

    Multi-tenant database format is used to store software data.

    One schema will be used to store the tenant data ( ID & Passwords ).
    ID of the tenant (which will be used to login) is the name of schema of that
    tenant and is refered as 'tenantId' in the src code.

    Every Tenant will have their own SCHEMA with same tables.

TABLES -
    1)bills
        billnumber [integer] (Primary Key)
        billdate [text]
        tablenumber [text]
        outletname [text]
        outletaddress [text]
        gstnumber [text]
        servicecharge [integer]
        sgst [numeric]
        cgst [numeric]
        discount [numeric]
        subtotal [numeric]
        total [numeric]
        grandtotal [numeric]
        pax [integer]
        nextbillnumber [integer] (is incremented from code)

        ---NOTE--- : One row in bills table should have all fields blank and "billnumber" field as 0 and "nextbillnumber"
        field as the starting number for generating bill numbers.

    2)billdetails
        id [bigserial] (i.e autoincrementing) (Primary Key) // because billnumber will repeat and cannot be set as Pkey
        billnumber [integer]
        tablenumber [text]
        fooditemname [text]
        fooditemquantity [integer]
        fooditemprice [integer]

    3)opentabledetails
        tablenumber [text]---|
                             |---> (COMPOSITE PRIMARY KEY)
        fooditemname [text]--|
        fooditemquantity [integer]
        fooditemprice[integer]

    4)menu
        fooditemname [text] (Primary Key)
        fooditemcateogry [text]
        fooditemprice [integer]
        fooditemavaiability [boolean]

    5)outletdetails
        id [bigserial] (i.e autoincrementing) (Primary Key)
        outletname [text]
        outletaddress [text]
        gstnumber [text]
        phone [text]

    6)taxes
        id  [bigserial] (Primary Key)
        cgst [numeric]
        sgst [numeric]
        vat [numeric]
        servicecharge [numeric]

    7)settings
        tables [integer] (Primary Key)
        areas [integer]






