package fr.sqli.Cantine.controller.admin.menus;

import java.util.Map;

public interface IMenuTest {
    final  String BASE_MENU_URL  =  "/cantine/api/admin/menus";
    final String ADD_MENU_URL = BASE_MENU_URL + "/add";


    String  IMAGE_MENU_FOR_TEST_NAME = "ImageMenuForTest.jpg";
    String IMAGE_MENU_DIRECTORY_PATH = "images/menus/";

    String  IMAGE_MENU_FORMAT_FOR_TEST="image/jpg";
    String   IMAGE_MENU_FOR_TEST_PATH = IMAGE_MENU_DIRECTORY_PATH+IMAGE_MENU_FOR_TEST_NAME;
    Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("Label", "LABEL_IS_MANDATORY"),
            Map.entry("InvalidArgument", "ARGUMENT NOT VALID"),
            Map.entry("LongLabelLength", "LABEL_IS_TOO_LONG"),
            Map.entry("ShortLabelLength", "LABEL_IS_TOO_SHORT"),
            Map.entry("Description", "DESCRIPTION_IS_MANDATORY"),
            Map.entry("ShortDescriptionLength", "DESCRIPTION_IS_TOO_SHORT"),
            Map.entry("LongDescriptionLength", "DESCRIPTION_IS_TOO_LONG"),
            Map.entry("Status", "STATUS_IS_MANDATORY"),
            Map.entry("OutSideStatusValue", "STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE")
    );
}
