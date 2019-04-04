package net.ammensa.property;

import java.util.HashMap;
import java.util.Map;

public abstract class AMMensaProperties {

	private static final Map<String, String> propertiesMap = new HashMap<String, String>();

	static {
		propertiesMap.put("datastoreMenuKey", "todayMenu");
		propertiesMap.put("adisuHostUrl", "https://www.adisurcampania.it");
		propertiesMap.put("menuPageUrl", propertiesMap.get("adisuHostUrl")
				+ "/archivio2_aree-tematiche_0_8.html");
		propertiesMap.put("menuPdfFolderPath", "fileadmin/user_upload/menu/");
	}

	static public String retrieveProperty(String key) {
		return propertiesMap.get(key);
	}

}
