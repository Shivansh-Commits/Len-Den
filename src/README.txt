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
        billnumber (Primary Key)
        billdate
        tablenumber
        outletname
        outletaddress
        gstnumber
        servicecharge
        sgst
        cgst
        discount
        subtotal
        total
        grandtotal
        pax

    2)billdetails
        billnumber (Primary Key)
        tablenumber
        fooditemname
        fooditemquantity
        fooditemprice

    3)menu
        fooditemname (Primary Key)
        fooditemcateogry
        fooditemprice
        fooditemavaiability

    4)outletdetails
        outletname
        outletaddress
        gstnumber

    5)taxex
        cgst
        sgst
        vat
        servicecharge






