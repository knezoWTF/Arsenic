package io.arsenic.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.arsenic.module.modules.client.ClickGUI;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class TranslationUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String[] languages = new String[]{"es-ES"};
    private static final HashMap<String, JsonObject> registeredTranslations = new HashMap<>();

    static {
        for (String language : languages) {
            try {
                InputStream languageFile = TranslationUtil.class.getResourceAsStream("/assets/immediatelyfast/translations/" + language + ".tf");
                if (languageFile == null) {
                    System.err.println("Failed to load " + language);
                    break;
                }

                registeredTranslations.put(language, gson.fromJson(new InputStreamReader(languageFile), JsonObject.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getModuleNameOrReturnEnglish(String moduleName) {
        if (registeredTranslations.containsKey(ClickGUI.language.getMode().name)) {
            JsonObject language = registeredTranslations.get(ClickGUI.language.getMode().name);
            String formatted = formatName(moduleName) + ".moduleName";
            if (language.has(formatted)) return language.get(formatted).getAsString();
        }

        return moduleName;
    }

    public static String getModuleDescriptionOrReturnEnglish(String moduleName, String description) {
        if (registeredTranslations.containsKey(ClickGUI.language.getMode().name)) {
            JsonObject language = registeredTranslations.get(ClickGUI.language.getMode().name);
            String formatted = formatName(moduleName) + ".moduleDescription";
            if (language.has(formatted)) return language.get(formatted).getAsString();
        }

        return description;
    }

    public static String getSettingNameOrReturnEnglish(String moduleName, String settingName) {
        if (registeredTranslations.containsKey(ClickGUI.language.getMode().name)) {
            JsonObject language = registeredTranslations.get(ClickGUI.language.getMode().name);
            String formatted = formatName(moduleName) + ".settingName." + formatName(settingName);
            if (language.has(formatted)) return language.get(formatted).getAsString();
        }

        return settingName;
    }

    public static String getSettingDescriptionOrReturnEnglish(String moduleName, String settingName, String settingDescription) {
        if (registeredTranslations.containsKey(ClickGUI.language.getMode().name)) {
            JsonObject language = registeredTranslations.get(ClickGUI.language.getMode().name);
            String formatted = formatName(moduleName) + ".settingDescription." + formatName(settingName);
            if (language.has(formatted)) return language.get(formatted).getAsString();
        }

        return settingDescription;
    }

    /*public void dumpDefaults(){
        JsonObject collection = new JsonObject();
        Arsenic.INSTANCE.moduleManager.getModules().forEach(module -> {
            collection.addProperty(formatName(module.getName()) + ".moduleName",escapeValue(module.getName()));
            collection.addProperty(formatName(module.getName()) + ".moduleDescription",escapeValue(module.getDescription()));
            module.getSettings().forEach(setting -> {
                collection.addProperty(formatName(module.getName()) + ".settingName." + formatName(setting.getName()), escapeValue(setting.getName()));
                collection.addProperty(formatName(module.getName()) + ".settingDescription." + formatName(setting.getName()), escapeValue(setting.getDescription()));
            });
        });

        try {
            Files.write(new File("dump.tf").toPath(), gson.toJson(collection).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
     */

    private static String formatName(String name) {
        return name.replace(" ", "_");
    }

    private String escapeValue(String value){
        if (value == null) return "";
        return value.replace("\"", "\\\"");
    }
}
