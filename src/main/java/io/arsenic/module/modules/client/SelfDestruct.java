package io.arsenic.module.modules.client;

import com.sun.jna.Memory;
import io.arsenic.Arsenic;
import io.arsenic.gui.ClickGui;
import io.arsenic.module.Category;
import io.arsenic.module.Module;
import io.arsenic.module.setting.*;
import io.arsenic.module.setting.BooleanSetting;
import io.arsenic.module.setting.Setting;
import io.arsenic.module.setting.StringSetting;
import io.arsenic.utils.Utils;

import java.io.File;

@SuppressWarnings("all")
public final class SelfDestruct extends Module {
	public static boolean destruct = false;

	private final BooleanSetting replaceMod = new BooleanSetting("Replace Mod", true)
			.setDescription("Repalces the mod with the original JAR file of the ImmediatelyFast mod");

	private final BooleanSetting saveLastModified = new BooleanSetting("Save Last Modified", true)
			.setDescription("Saves the last modified date after self destruct");

	private final StringSetting downloadURL = new StringSetting("Replace URL", "https://cdn.modrinth.com/data/5ZwdcRci/versions/FEOsWs1E/ImmediatelyFast-Fabric-1.2.11%2B1.20.4.jar");

	public SelfDestruct() {
		super("Self Destruct",
				"Removes the client from your game |Credits to lwes for deletion|",
				-1,
				Category.CLIENT);
		addSettings(replaceMod, saveLastModified, downloadURL);
	}

	@Override
	public void onEnable() {
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
				String modUrl = downloadURL.getValue();
				File currentJar = Utils.getCurrentJarPath();

				if (currentJar.exists())
                    Utils.replaceModFile(modUrl, Utils.getCurrentJarPath());
			} catch (Exception ignored) {}
		}

		for (Module module : Arsenic.INSTANCE.getModuleManager().getModules()) {
			module.setEnabled(false);

			module.setName(null);
			module.setDescription(null);

			for (Setting<?> setting : module.getSettings()) {
				setting.setName(null);
				setting.setDescription(null);

				if(setting instanceof StringSetting set)
					set.setValue(null);
			}
			module.getSettings().clear();
		}

		Runtime runtime = Runtime.getRuntime();

		if (saveLastModified.getValue())
			Arsenic.INSTANCE.resetModifiedDate();

		for (int i = 0; i <= 10; i++) {
			runtime.gc();
			runtime.runFinalization();

			try {
				Thread.sleep(100 * i);
				Memory.purge();
				Memory.disposeAll();
			} catch (InterruptedException ignored) {}
		}
	}
}