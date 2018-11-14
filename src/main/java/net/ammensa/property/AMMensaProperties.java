package net.ammensa.property;

import java.util.HashMap;
import java.util.Map;

public abstract class AMMensaProperties {

	private static final Map<String, String> propertiesMap = new HashMap<String, String>();

	static {
		propertiesMap.put("datastoreMenuKey", "todayMenu");
		propertiesMap.put("adisuHostUrl", "http://www.adisu.sa.it");
		propertiesMap.put("menuPageUrl", propertiesMap.get("adisuHostUrl")
				+ "/4/servizi-adisu/ristorazione/menu-del-giorno.html");
		propertiesMap.put("menuPdfFolderPath", "fileadmin/user_upload/menu/");
	}

	static public String retrieveProperty(String key) {
		return propertiesMap.get(key);
	}

}
