package org.silentsoft.folderchef.load;

public abstract class Loader {

	public abstract void init();
	
	public abstract void load();
	
	public Loader() {
		init();
		load();
	}
}
