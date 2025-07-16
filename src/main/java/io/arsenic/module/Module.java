package io.arsenic.module;

import io.arsenic.Arsenic;
import io.arsenic.module.setting.Setting;

import net.minecraft.client.MinecraftClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Module implements Serializable {
	private final List<Setting<?>> settings = new ArrayList<>();
	protected MinecraftClient mc = MinecraftClient.getInstance();
	private String name;
	private String description;
	private boolean enabled;
	private int key;
	private Category category;

	public Module(String name, String description, int key, Category category) {
		this.name = name;
		this.description = description;
		this.enabled = false;
		this.key = key;
		this.category = category;
	}

	public void toggle() {
		enabled = !enabled;
		if (enabled) {
			onEnable();
			Arsenic.EVENT_BUS.subscribe(this);
		} else {
			onDisable();
			Arsenic.EVENT_BUS.unsubscribe(this);
		}
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getDescription() {
		return description;
	}

	public int getKey() {
		return key;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public List<Setting<?>> getSettings() {
		return settings;
	}

	public void onEnable() {}

	public void onDisable() {}

	public void addSetting(Setting<?> setting) {
		this.settings.add(setting);
	}

	public void addSettings(Setting<?>... settings) {
		this.settings.addAll(Arrays.asList(settings));
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			onEnable();
			Arsenic.EVENT_BUS.subscribe(this);
		} else {
			onDisable();
			Arsenic.EVENT_BUS.unsubscribe(this);
		}
	}

	public void setEnabledStatus(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			onEnable();
			Arsenic.EVENT_BUS.subscribe(this);
		} else {
			onDisable();
			Arsenic.EVENT_BUS.unsubscribe(this);
		}
	}

}
