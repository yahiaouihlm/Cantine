package fr.sqli.Cantine.controller.admin.adminDashboard;

import java.util.Map;

public interface IAdminTest {
    String ADMIN_DASH_BOARD_BASIC_URL = "/cantine/admin/adminDashboard";
    String ADMIN_SIGN_UP = ADMIN_DASH_BOARD_BASIC_URL +  "/signUp";
    String ADMIN_ADDED_SUCCESSFULLY = "ADMIN ADDED SUCCESSFULLY";

    final Map<String, String> exceptionsMap = Map.ofEntries(

    );
}
