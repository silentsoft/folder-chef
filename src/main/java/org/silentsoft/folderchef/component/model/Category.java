package org.silentsoft.folderchef.component.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7633665539473285507L;

	public enum Property {
		Directory,
		File,
		KeywordSet,
		Keyword
	};
	
	private String name;
	private String value;
	private ArrayList<String> relations;
	private Property property;
	
	public Category(String name, Property property) {
		this.name = name;
		this.value = "";
		this.relations = null;
		this.property = property;
	}
	
	public Category(String name, String value, Property property) {
		this.name = name;
		this.value = value;
		this.relations = null;
		this.property = property;
	}
	
	public Category(String name, String value, ArrayList<String> relations, Property property) {
		this.name = name;
		this.value = value;
		this.relations = relations;
		this.property = property;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public ArrayList<String> getRelations() {
		return relations;
	}
	
	public void setRelations(ArrayList<String> relations) {
		this.relations = relations;
	}
	
	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}
	
}
