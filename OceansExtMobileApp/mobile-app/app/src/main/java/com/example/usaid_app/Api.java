package com.example.usaid_app;

public class Api {
    // API calls tailor-fitted with the database

//    private static final String ROOT_URL = "http://p2c.ics.uplb.edu.ph/mobile_ids/v1/Api.php?apicall=";
    private static final String ROOT_URL = "http://192.168.1.10/MobileIDSDB/v1/Api.php?apicall=";

    public static final String URL_CREATE_USER = ROOT_URL + "createUser";       //for signup
    public static final String URL_READ_USER = ROOT_URL + "getUser&UserName=";  //for login
    public static final String URL_READ_USER_PASSWORD = ROOT_URL + "getUserPassword&UserName=";  //for checking of password
    public static final String URL_READ_EFFORTCOORDINATES = ROOT_URL + "getAllEffortCoordinates&Start_Date=";   //for fishing coordinate heatmap
    public static final String URL_READ_CATCHDATE = ROOT_URL + "getCatchDate&CatchID=";   //for effort heatmap

    //from wishlist
    public static final String URL_DELETE_USER = ROOT_URL + "deleteuser&UserId=";
    public static final String URL_READ_CPUEBYGEAR = ROOT_URL + "getCPUEbyGear&gears=";
    public static final String URL_READ_FISHINGCOORDINATESBYGEAR = ROOT_URL + "getFishingCoordinatesbyGear&gears=";
    public static final String URL_READ_SPECIESDISTRIBUTIONBYGEAR = ROOT_URL + "getSpeciesDistributionbyGear&gears=";
    public static final String URL_READ_CPUEBYCOMPANYBOAT = ROOT_URL + "getCPUEbyCompanyBoat&Owner=";
    public static final String URL_READ_COMPANYNAMES = ROOT_URL + "getCompanyNames";
    public static final String URL_READ_GEARS = ROOT_URL + "getGears";
    public static final String URL_READ_ALLCATCHDETAILS = ROOT_URL + "getAllCatchDetails";
    public static final String URL_READ_USERNOTVERIFIED = ROOT_URL + "getUserNotVerified";
    public static final String URL_CREATE_FISHING_EFFORT = ROOT_URL + "createFishingEffort";
    public static final String URL_READ_ALLCATCHERVESSEL = ROOT_URL + "getAllCatcherVessel";
    public static final String URL_READ_ALLCARRIERVESSEL = ROOT_URL + "getAllCarrierVessel";
    public static final String URL_GET_COMPANY_OF_USER = ROOT_URL + "getCompanyGivenUserName&UserName=";
    public static final String URL_GET_CATCH_COMPANY = ROOT_URL + "getAllCatchDetailsGivenCompany";
    public static final String URL_READ_LATESTCATCHID = ROOT_URL + "getLatestCatchID";
    public static final String URL_UPDATE_VERIFIEDUSER = ROOT_URL + "updateVerifiedUser&UserId=";
    //private static final String FAME_URL = "http://www.fame.systems:8082/api/positions/?";

    //public static final String FAMEURL_GETCOORDS = FAME_URL+"from=";

}