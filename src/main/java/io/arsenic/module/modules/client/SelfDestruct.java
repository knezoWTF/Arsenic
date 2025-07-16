package io.arsenic.module.modules.client;

import io.arsenic.gui.ClickGui;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.KeybindSetting;
import io.arsenic.module.setting.Setting;
import io.arsenic.module.setting.StringSetting;
import io.arsenic.Arsenic;
import io.arsenic.utils.EncryptedString;
import io.arsenic.utils.Utils;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public final class SelfDestruct extends Module {
	public static boolean destruct = false;

	private final BooleanSetting replaceMod = new BooleanSetting(EncryptedString.of("Replace Mod"), true)
			.setDescription(EncryptedString.of("Replaces the mod with the original JAR file"));

	private final BooleanSetting saveLastModified = new BooleanSetting(EncryptedString.of("Save Last Modified"), true)
			.setDescription(EncryptedString.of("Saves the last modified date after self-destruct"));

	private final StringSetting downloadURL = new StringSetting(EncryptedString.of("Replace URL"),
			"https://cdn.modrinth.com/data/5ZwdcRci/versions/FEOsWs1E/ImmediatelyFast-Fabric-1.2.11%2B1.20.4.jar");

	public SelfDestruct() {
		super(EncryptedString.of("Self Destruct"),
				EncryptedString.of("Kills the system and destroys all traces of using this client. The client mod will be replaced as ImmediatelyFast Mod"),
				-1,
				Category.CLIENT);
		addSettings(replaceMod, saveLastModified, downloadURL);
	}

	@Override
	public void onEnable() {
		if (!mc.player.isSneaking()) {
			setEnabled(false);
			return;
		}

		destruct = true;
		Arsenic.INSTANCE.getModuleManager().getModule(ClickGUI.class).setEnabled(false);
		setEnabled(false);


		Arsenic.INSTANCE.getProfileManager().saveProfile();

		if (mc.currentScreen instanceof ClickGui) {
			Arsenic.INSTANCE.guiInitialized = false;
			mc.currentScreen.close();
		}

		if (replaceMod.getValue()) {
			try {
				Utils.replaceModFile(downloadURL.getValue(), Utils.getCurrentJarPath());
			} catch (Exception ignored) {}
		}

		for (Module module : Arsenic.INSTANCE.getModuleManager().getModules()) {
			module.setEnabled(false);
			module.setName(null);
			module.setDescription(null);
			for (Setting<?> setting : module.getSettings()) {
				setting.setName(null);
				setting.setDescription(null);
				if (setting instanceof StringSetting set) set.setValue(null);
			}
			module.getSettings().clear();
		}

		if (saveLastModified.getValue()) Arsenic.INSTANCE.resetModifiedDate();
		Runtime.getRuntime().gc();
	}
}