package fr.sqli.Cantine.service.admin.menus;

import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.service.admin.meals.IMealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;

@ExtendWith(MockitoExtension.class)
class GetMenuTest {
    @Mock
    private IMenuDao iMenuDao;
    @Mock
    private IMealService iMealService;

    @InjectMocks
    private  MenuService menuService;

    @Mock
    MockEnvironment environment;

    @BeforeEach
    void setUp() {
        this.environment = new MockEnvironment();
        this.environment.setProperty("sqli.cantine.images.url.menus", "http://localhost:8080/images/menus/");
        this.menuService = new MenuService(environment, iMenuDao, iMealService);
    }


}












