package io.arsenic;

import io.arsenic.gui.ClickGui;
import io.arsenic.managers.FriendManager;
import io.arsenic.module.ModuleManager;
import io.arsenic.managers.ProfileManager;
import io.arsenic.utils.rotation.RotatorManager;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.*;

@SuppressWarnings("all")
public final class Arsenic {
	public RotatorManager rotatorManager;
	public ProfileManager profileManager;
	public ModuleManager moduleManager;
	public FriendManager friendManager;
	public static MinecraftClient mc;
	public String version = " b1.3";
	public static boolean BETA;
	public static Arsenic INSTANCE;
	public boolean guiInitialized;
	public ClickGui clickGui;
	public Screen previousScreen = null;
	public long lastModified;
	public File arsenicJar;
	public static final IEventBus EVENT_BUS = new EventBus();

	public Arsenic() throws InterruptedException, IOException {
		INSTANCE = this;
		EVENT_BUS.registerLambdaFactory("io.arsenic", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		this.moduleManager = new ModuleManager();
		this.clickGui = new ClickGui();
		this.rotatorManager = new RotatorManager();
		this.profileManager = new ProfileManager();
		this.friendManager = new FriendManager();

		this.getProfileManager().loadProfile();
		this.setLastModified();

		this.guiInitialized = false;
		mc = MinecraftClient.getInstance();
	}

	public ProfileManager getProfileManager() {
		return profileManager;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public FriendManager getFriendManager() {
		return friendManager;
	}

	public ClickGui getClickGui() {
		return clickGui;
	}

	public void resetModifiedDate() {
		this.arsenicJar.setLastModified(lastModified);
	}

	public String getVersion() {
		return version;
	}

	public void setLastModified() {
		try {
			this.arsenicJar = new File(Arsenic.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			// Comment out when debugging
			this.lastModified = arsenicJar.lastModified();
		} catch (URISyntaxException ignored) {}
	}
}