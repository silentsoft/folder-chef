package org.silentsoft.folderchef.transform;

public abstract class Transformer {

	public abstract void init();
	
	public abstract void transform();
	
	public Transformer(){
		init();
		transform();
	}
}
