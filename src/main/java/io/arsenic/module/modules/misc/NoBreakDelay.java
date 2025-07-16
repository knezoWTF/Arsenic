package io.arsenic.module.modules.misc;

import io.arsenic.module.Category;
import io.arsenic.module.Module;

public final class NoBreakDelay extends Module {
	public NoBreakDelay() {
		super("No Break Delay",
				"Removes the break delay from mining blocks",
				-1,
				Category.MISC);
	}
}
