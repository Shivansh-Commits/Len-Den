------------------------------------------------------------------------------------------------------------------------
                                        [   LEN-DEN PROJECT DETAILS   ]
------------------------------------------------------------------------------------------------------------------------

main screen dim - 1385 x 806

---------------------------------------
           [ JAR CREATION ]
---------------------------------------
[NOTE] - If your main class is in App.java, and you create a jar , it will not run because App class inherits 'Application' class
        which is a part of javafx library. hence we need to create a separate java class (launcher.java) and call the main method
        of App.java from the launcher class. In this way the external libraries are added with the jar.

STEP 1 - After the jar is created . open it with winrar and Inside META-INF folder, delete all files having extension
         [ .SF ] ,[ .DSA ] , [ .RSA ]. These are the signature files of other jars (dependencies) , but every jar can
         have only one signature file, hence deleting these dependencies sign files.
 
VIDEO REFERENCE - https://youtu.be/IoPXzopsmpE


---------------------------------------
           [ EXE CREATION ]
---------------------------------------
Tools Required - Launch4j

STEP 1 - Open Launch4j
STEP 2 - Fill in the details i.e Loc of exe , Loc of output dir,Icon etc
STEP 3 - In the JRE section, inside JRE path field , write -> "JRE"
STEP 4 - in the output Dir create a Folder called JRE and go to the java installation folder and copy all the files in
         JRE folder. (Our exe uses the JRE from this very folder, hence it should not be renamed or moved)

VIDEO REFERENCE - https://youtu.be/51iMSVUOQNM


---------------------------------------
     [ SETUP/INSTALLER CREATION ]
---------------------------------------
Tools Require - Inno Setup Compiler

STEP 1 - open inno script, choose option to "create a new script using script wizard"
STEP 2 - Fill all the basic details (next -> next -> next ...)
SETP 3 - On the "Application Files" page, mention location of EXE file, and below in the "other files section" click on
         "Add Folder" and select the "JRE" folder that we created earlier.
STEP 4 - Further add the basic details , and when installation wizard ends, you will be prompted with a dailouge box
         saying "would you like to compile the script now" click "NO" and the script editor will open up.

STEP 5 - (For First Source) In the script , find the "[Files]" section. In one of the "Sources" you will see the EXE Application's address mentioned.
STEP 6 - In that line we have to change the "DestDir" value. remove "{app}" and write this
         "C:\Program Files (x86)\LenDen" . (Basically we are bundling JRE with the EXE , because our exe needs
         JRE to run, on which ever system it will be installed).

STEP 7 - (FOR FIRST Source) In the script , find the "[Files]" section. In one of the "Sources" you will see the JRE's address mentioned.
STEP 8 - In that line we have to change the "DestDir" value. remove "{app}" and write this
         "C:\Program Files (x86)\LenDen\JRE" . (Basically we are bundling JRE with the EXE , because our exe needs
         JRE to run, on which ever system it will be installed).

STEP 9 - Save the script and click on Run.

VIDEO REFERENCE - https://youtu.be/k6m2a1OtfZ4


---------------------------------------
           [ TECHNOLOGY ]
---------------------------------------
LANGUAGE USED - JAVA

DATABASE - POSTGRES (Hosted via AWS)

BUILD & COMPILE - MAVEN

UI LIBRARY - JAVAFX

IDE USED - Idea - IntelliJ

UI DESIGNER TOOL - Scene Builder


---------------------------------------
           [ DATABASE ]
---------------------------------------

STRUCTURE DESCRIPTION -

Multi-tenant database format is used to store software data. Different tenant means different schema. One schema i.e.
"public" holds all the sensitive user credentials.

    [ SCHEMA 1 ] -->
    'PUBLIC' schema will be used to store the tenant data ( ID & Passwords ).
    ID of the tenant (which will be used to log in) is the name of schema of that
    tenant and is referred as 'tenantId' in the src code.

TABLES -
    1)tenants
        username [text] (Primary Key)
        password [text]
        subscriptionstartdate [text]
        subscriptionenddate [text]


    [ SCHEMA 2 ] (can be multiple depending on users) -->
    multiple schemas can exist depending on the customers.Their structure will be as defined below.
    (Every Tenant will have their own SCHEMA with same tables)

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
        tablenumber [text]
        modeofpayment [text]
        status [text]

        [---NOTE---] : One row in bills table should have all fields blank and "billnumber" field as 0 and "nextbillnumber"
        field as the starting number for generating bill numbers.

    2)billdetails
        id [Bigint] (i.e autoincrementing) (Primary Key) // because billnumber will repeat and cannot be set as Pkey
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
        variant[Text]

    4)menu
        id [Bigint] (Primary Key)
        fooditemname [text]
        fooditemcateogry [text]
        fooditemprice [integer]
        fooditemavaiability [boolean]
        stockquantity [integer]
        variants [text]
        istracked[text]

    5)outletdetails
        outletname [text] (Primary Key)
        outletaddress [text]
        gstnumber [text]
        phone [text]

    6)tableandarea
        area [text] (Primary Key)
        tables [integer]

        [---NOTE---] : Mention Area name and Table count

    7)reservedtables
        tablename [text] (Primary key)

    8)billsettings
        modeofpayment [text] (Primary key)
        defaultmodeofpayment [text]
        maxdiscount [numeric]
        defaultdiscount [numeric]
        gst [numeric]
        vat [numeric]
        servicecharge [numeric]

        [---NOTE---] : Mention GST Values and default Discount

    9)takeawayorderdetails
        ordernumber [integer]---|
                                |--> (Composite Primary Key)
        fooditemname [text] ----|
        fooditemquantity [integer]
        fooditemprice [numeric]
        status [text]
        variant [text]

    10)inventory
        id [bigint] (Primary Key)
        name [text]
        rate [integer]
        unit [text]
        quantity [integer]

    11)inventorypurchase
        purchaseid [bigserial] (Primary Key)
        inventoryitemid [integer]
        inventoryitemname [text]
        inventoryitemcost [integer]
        inventoryitemquantity [integer]
        inventoryitemunit [text]

    12)variants
        id [bigint]
        menuitemid [Integer]
        variantname [text]
        variantprice [double precision]





