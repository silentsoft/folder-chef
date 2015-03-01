package org.silentsoft.folderchef.component.tree;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.MapUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.folderchef.component.custom.KeywordPanel;
import org.silentsoft.folderchef.component.model.Category;
import org.silentsoft.folderchef.component.model.Relation;
import org.silentsoft.folderchef.component.model.Category.Property;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;

public class CategoryTreeCellEditor extends DefaultCellEditor {
	private DnDJTree tree;
	private final KeywordPanel panel;
	
	public CategoryTreeCellEditor() {
		super(new JComboBox<String>());
		panel = new KeywordPanel();
	}
	
	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		this.tree = (DnDJTree) tree;
		
		Category category = panel.extractCategory(value);
		panel.setContents(this.tree, category);
		
		return panel;
	}
	
	@Override
	public Object getCellEditorValue() {
		DnDNode node = (DnDNode)tree.getLastSelectedPathComponent();
		if (node != null) {
			Category category = (Category) node.getUserObject();
			
			if (category.getProperty() == Property.Keyword || category.getProperty() == Property.KeywordSet) {
				Object selectedItem = ((JComboBox)panel.getComponent(1)).getSelectedItem();
				if (selectedItem == null) {
					return null;
				}
				
				String keyword = "";
				if (category.getProperty() == Property.Keyword) {
					keyword = ObjectUtil.toString(selectedItem).split(CommonConst.VERTICAL_BAR)[BizConst.IDX_KEYSET_KEYWORD];
				} else if (category.getProperty() == Property.KeywordSet) {
					keyword = ObjectUtil.toString(selectedItem);
				}
				
				ArrayList<String> relations = new ArrayList<String>();
				
				Relation relation = (Relation)SharedMemory.getDataMap().get(BizConst.KEY_RELATION);
				Map<String, Integer> relatedMap = relation.getRelation(keyword);
				if (relatedMap != null) {
					String key;
					int count;
					Iterator<String> itr = (Iterator<String>) MapUtil.sortByValue(relatedMap).iterator();
					
					while (itr.hasNext()) {
						key = itr.next();
						count = ObjectUtil.toInt(relatedMap.get(key));
						relations.add(key + CommonConst.VERTICAL_BAR_CHAR + count);
					}
				}
				
				DnDNode target = new DnDNode(new Category(keyword, keyword, relations, Property.Keyword));
				
				DnDNode parentNode = null; 
				if (category.getProperty() == Property.Keyword) {
					parentNode = (DnDNode)node.getParent();
				} else if (category.getProperty() == Property.KeywordSet) {
					parentNode = node;
				}
				
				if (parentNode.indexOfNode(target) == -1) {
					((DefaultTreeModel)tree.getModel()).insertNodeInto(target, parentNode, parentNode.getChildCount());
				}
			}
		}
		
		return null;
	}
	
	@Override
	public boolean isCellEditable(EventObject event) {
		boolean returnValue = false;
		
		Object source = event.getSource();
		if (source instanceof DnDJTree && event instanceof MouseEvent) {
			DnDJTree tree = (DnDJTree) source;
			MouseEvent mouseEvent = (MouseEvent) event;
			
			if (mouseEvent.getClickCount() >= CommonConst.MOUSE_SINGLE_CLICK) {
				DnDNode node = (DnDNode)tree.getLastSelectedPathComponent();
				if (node != null) {
					Category category = (Category) node.getUserObject();
					
					switch (category.getProperty()) {
					case Keyword:
					case KeywordSet:
						returnValue = true;
						break;
					default :
						returnValue = false;
						break;
					}
				}
			}
		}
		
		return returnValue;
	}
}
