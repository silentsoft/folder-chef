package org.silentsoft.folderchef.component.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.silentsoft.folderchef.component.model.Category;

public class CategoryNode extends Category implements HierarchyData<CategoryNode>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7405531034719472885L;

	private List<CategoryNode> children = new ArrayList<CategoryNode>();
	
	public CategoryNode(String name, Property property) {
		super(name, property);
	}
	
	public CategoryNode(String name, String value, Property property) {
		super(name, value, property);
	}
	
	public CategoryNode(String name, String value, ArrayList<String> relations, Property property) {
		super(name, value, relations, property);
	}
	
	@Override
	public ObservableList<CategoryNode> getChildren() {
		return FXCollections.observableList(children);
	}
	
	public boolean isExistsInsideSameCategoryNode(CategoryNode targetNode) {
		boolean result = false;
		
		for (CategoryNode node : getChildren()) {
			boolean isSame = true;
			
			if (node.getName() != null && targetNode.getName() != null) {
				if (!node.getName().equals(targetNode.getName())) {
					isSame = false;
				}
			}
			
			if (node.getValue() != null && targetNode.getValue() != null) {
				if (!node.getValue().equals(targetNode.getValue())) {
					isSame = false;
				}
			}
			
			if (node.getProperty() != null && targetNode.getProperty() != null) {
				if (!node.getProperty().equals(targetNode.getProperty())) {
					isSame = false;
				}
			}
			
			if (node.getRelations() != null && targetNode.getRelations() != null) {
				if (!node.getRelations().equals(targetNode.getRelations())) {
					isSame = false;
				}
			}
			
			if (isSame) {
				result = true;
			}
		}
		
		return result;
	}
}
