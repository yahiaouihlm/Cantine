package fr.sqli.Cantine.controller.admin.menus;

import java.util.Map;

public interface IMenuTest {
    final  String BASE_MENU_URL  =  "/cantine/api/admin/menus";
    final String ADD_MENU_URL = BASE_MENU_URL + "/add";


    String  IMAGE_MENU_FOR_TEST_NAME = "ImageMenuForTest.jpg";
    String IMAGE_MENU_DIRECTORY_PATH = "images/menu/";

    String  IMAGE_MENU_FORMAT_FOR_TEST="image/jpg";
    String   IMAGE_MENU_FOR_TEST_PATH = IMAGE_MENU_DIRECTORY_PATH+IMAGE_MENU_FOR_TEST_NAME;
    Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("Label", "LABEL_IS_MANDATORY"),
            Map.entry("InvalidArgument", "ARGUMENT NOT VALID"),
            Map.entry("LongLabelLength", "LABEL_IS_TOO_LONG"),
            Map.entry("ShortLabelLength", "LABEL_IS_TOO_SHORT")
    );
}
