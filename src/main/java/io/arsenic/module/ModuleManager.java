package io.arsenic.module;

import io.arsenic.Arsenic;
import io.arsenic.event.events.ButtonEvent;
import io.arsenic.module.modules.client.ClickGUI;
import io.arsenic.module.modules.client.Friends;
import io.arsenic.module.modules.client.SelfDestruct;
import io.arsenic.module.modules.combat.*;
import io.arsenic.module.modules.misc.*;
import io.arsenic.module.modules.render.*;
import io.arsenic.module.setting.KeybindSetting;

import meteordevelopment.orbit.EventHandler;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

	public ModuleManager() {
		addModules();
		addKeybinds();
	}

	public void addModules() {
		//Combat
		add(new AimAssist());
		add(new AnchorMacro());
		add(new AutoCrystal());
		add(new AutoDoubleHand());
		add(new AutoHitCrystal());
		add(new AutoInventoryTotem());
		add(new TriggerBot());
		add(new AutoPot());
		add(new AutoPotRefill());
		add(new AutoWTap());
		add(new CrystalOptimizer());
		add(new DoubleAnchor());
		add(new HoverTotem());
		add(new NoMissDelay());
		add(new ShieldDisabler());
		add(new TotemOffhand());
		add(new AutoJumpReset());

		//Misc
		add(new Prevent());
		add(new AutoXP());
		add(new NoJumpDelay());
		add(new PingSpoof());
		add(new FakeLag());
		add(new AutoClicker());
		add(new KeyPearl());
		add(new NoBreakDelay());
		add(new Freecam());
		add(new PackSpoof());
		add(new Sprint());

		//Render
		add(new HUD());
		add(new NoBounce());
		add(new PlayerESP());
		add(new StorageEsp());
		add(new TargetHud());

		//Client
		add(new ClickGUI());
		add(new Friends());
		add(new SelfDestruct());
	}

	public List<Module> getEnabledModules() {
		return modules.stream()
				.filter(Module::isEnabled)
				.toList();
	}


	public List<Module> getModules() {
		return modules;
	}

	public void addKeybinds() {
		Arsenic.EVENT_BUS.subscribe(this);

		for (Module module : modules)
			module.addSetting(new KeybindSetting("Keybind", module.getKey(), true).setDescription("Key to enabled the module"));
	}

	public List<Module> getModulesInCategory(Category category) {
		return modules.stream()
				.filter(module -> module.getCategory() == category)
				.toList();
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> T getModule(Class<T> moduleClass) {
		return (T) modules.stream()
				.filter(moduleClass::isInstance)
				.findFirst()
				.orElse(null);
	}

	public void add(Module module) {
		modules.add(module);
	}

	@EventHandler
	private void onButtonPressEvent(ButtonEvent event) {
		if(!SelfDestruct.destruct) {
			modules.forEach(module -> {
				if(module.getKey() == event.button && event.action == GLFW.GLFW_PRESS)
					module.toggle();
			});
		}
	}
}
