package fr.sqli.Cantine.controller.order;

import fr.sqli.Cantine.controller.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dao.IOrderDao;
import fr.sqli.Cantine.dao.IStudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@AutoConfigureMockMvc
public class CancelOrderTest  extends AbstractContainerConfig implements   IOrderTest {
    @Autowired
    private IOrderDao orderDao;
    @Autowired
    private IStudentDao studentDao;
    @Autowired
    private IMealDao mealDao;

    @Autowired
    private IMenuDao menuDao;






}
