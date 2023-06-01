package fr.sqli.Cantine.service.admin.adminDashboard.work;

import fr.sqli.Cantine.dao.IAdminDao;
import fr.sqli.Cantine.dao.IConfirmationTokenDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.service.images.ImageService;
import fr.sqli.Cantine.service.mailer.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminFunctionService  implements  IAdminFunctionService{

    private IAdminDao adminDao;
    private IFunctionDao functionDao;
    private ImageService imageService;
    private Environment environment;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private IConfirmationTokenDao confirmationTokenDao;
    private EmailSenderService emailSenderService;

    @Autowired
    public AdminFunctionService(IAdminDao adminDao,
                                IFunctionDao functionDao, ImageService imageService, Environment environment,
                                BCryptPasswordEncoder bCryptPasswordEncoder, IConfirmationTokenDao confirmationTokenDao,
                                EmailSenderService emailSenderService) {
    }




}
