package io.arsenic.module.setting;

public abstract class Setting<T extends Setting<T>> {
	private String name;
	public String description;

	public Setting(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public T setDescription(String desc) {
		this.description = desc;
		//noinspection unchecked
		return (T) this;
	}
}
