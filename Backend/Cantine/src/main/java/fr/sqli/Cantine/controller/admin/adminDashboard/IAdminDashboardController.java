package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface IAdminDashboardController {

    String ADMIN_DASH_BOARD_BASIC_URL = "/cantine/admin/adminDashboard";

    String ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT =  "/signUp";


    public ResponseEntity<String> signUp(@ModelAttribute AdminDtoIn adminDtoIn);
}
