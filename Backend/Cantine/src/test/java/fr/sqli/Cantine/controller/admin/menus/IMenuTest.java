package fr.sqli.Cantine.controller.admin.menus;

import java.util.Map;

public interface IMenuTest {
    final  String BASE_MENU_URL  =  "/cantine/api/admin/menus";
    final String ADD_MENU_URL = BASE_MENU_URL + "/add";
    Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("Label", "LABEL_IS_MANDATORY"),
            Map.entry("InvalidArgument", "ARGUMENT NOT VALID"),
            Map.entry("LongLabelLength", "LABEL_IS_TOO_LONG"),
            Map.entry("ShortLabelLength", "LABEL_IS_TOO_SHORT")
    );
}
