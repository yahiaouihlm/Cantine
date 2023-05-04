package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static fr.sqli.Cantine.controller.admin.adminDashboard.IAdminDashboardController.ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT;

@RestController
@RequestMapping(ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT)
public class AdminController  implements IAdminDashboardController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @Override
    public ResponseEntity<String> signUp(AdminDtoIn adminDtoIn) {
        this.adminService.signUp(adminDtoIn);
    }
}
