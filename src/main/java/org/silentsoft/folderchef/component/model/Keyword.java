package org.silentsoft.folderchef.component.model;

public class Keyword {
	private Object keyword;
	private Object count;
	
	public Keyword(Object keyword, Object count) {
		this.keyword = keyword;
		this.count = count;
	}

	public Object getKeyword() {
		return keyword;
	}

	public void setKeyword(Object keyword) {
		this.keyword = keyword;
	}

	public Object getCount() {
		return count;
	}

	public void setCount(Object count) {
		this.count = count;
	}
}
