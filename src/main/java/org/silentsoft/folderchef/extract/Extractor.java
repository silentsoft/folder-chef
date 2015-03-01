package org.silentsoft.folderchef.extract;

public abstract class Extractor {

	public abstract void init();
	
	public abstract void extract();
	
	public Extractor() {
		init();
		extract();
	}
}
