package fr.sqli.Cantine.controller.admin.menus;

import java.util.Map;

public interface IMenuTest {
    Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("Label", "LABEL_IS_MANDATORY"),
            Map.entry("InvalidArgument", "ARGUMENT NOT VALID")
    );
}
