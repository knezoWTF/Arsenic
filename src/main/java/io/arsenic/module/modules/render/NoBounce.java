package io.arsenic.module.modules.render;

import io.arsenic.module.Category;
import io.arsenic.module.Module;

public final class NoBounce extends Module {
	public NoBounce() {
		super("No Bounce",
				"Removes the crystal bounce",
				-1,
				Category.RENDER);
	}
}
