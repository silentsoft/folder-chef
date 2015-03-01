package org.silentsoft.folderchef.component.custom;

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;

import org.silentsoft.core.util.ArrayUtil;
import org.silentsoft.folderchef.component.model.Category;
import org.silentsoft.folderchef.component.model.Category.Property;
import org.silentsoft.folderchef.component.tree.DnDJTree;
import org.silentsoft.folderchef.component.tree.DnDNode;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;

public class KeywordPanel extends JPanel {
	private JLabel label = new JLabel();
	private JComboBox<String> combo = new JComboBox<String>();
	
	public KeywordPanel() {
		super();
		setLayout(new GridBagLayout());
		setOpaque(false);
	}
	
	public JLabel getLabel() {
		return label;
	}
	
	public JComboBox<String> getCombo() {
		return combo;
	}
	
	public static Category extractCategory(Object value) {
		if (value instanceof DnDNode) {
			DnDNode node = (DnDNode) value;
			Object userObject = node.getUserObject();
			if (userObject instanceof Category) {
				return (Category) userObject;
			}
		}
		
		return null;
	}
	
	public void setContents(DnDJTree tree, Category category) {
		if (category == null) {
			return;
		}
		
		removeAll();
		
		String text = "";
		String toolTipText = "";
		boolean showCombo = false;
		ArrayList<String> relations = null;
		ImageIcon imageIcon = null;
		
		text = category.getName();
		toolTipText = category.getValue();
		relations = category.getRelations();
		
		switch (category.getProperty()) {
		case Directory:
			imageIcon = new ImageIcon("icon/directory.png");
			break;
		case File:
			imageIcon = new ImageIcon("icon/file.png");
			break;
		case KeywordSet:
			showCombo = true;
			imageIcon = new ImageIcon("icon/keyword_set.png");
			break;
		case Keyword:
			showCombo = true;
			imageIcon = new ImageIcon("icon/keyword.png");
			break;
		default :
			imageIcon = new ImageIcon("icon/none_property.png");
			break;
		}
		
		label = null;
		label = new JLabel();
		label.setToolTipText(toolTipText);
		label.setText(text);
		label.setIcon(imageIcon);
		
		add(label);
		if (showCombo) {
			combo = null;
			combo = new JComboBox<String>();
			
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel) combo.getModel();
			
			if (category.getProperty() == Property.Keyword) {
				if (relations != null) {
					if (relations.size() > 0) {
						Object selectedItem = model.getSelectedItem();
						
						model.removeAllElements();
						for (String relation : relations) {
							model.addElement(relation);
						}
						
						model.setSelectedItem(selectedItem);
					}
				}
			} else if (category.getProperty() == Property.KeywordSet) {
				DnDNode node = (DnDNode)tree.getLastSelectedPathComponent();
				if (node != null) {
					if (((Category)node.getUserObject()).getProperty() == Property.KeywordSet) {
						String[] childArray = new String[node.getChildCount()];
						for (int i=0, j=node.getChildCount(); i<j; i++) {
							DnDNode child = (DnDNode)node.getChildAt(i);
							childArray[i] = ((Category)child.getUserObject()).getName();
						}
						Arrays.sort(childArray);
						
						Object selectedItem = model.getSelectedItem();
						model.removeAllElements();
						
						HashSet<String> keywordSetModel = new HashSet<String>();
						HashSet<String[]> relationSet = (HashSet<String[]>)SharedMemory.getDataMap().get(BizConst.KEY_RELATION_SET);
						for (Iterator<String[]> itr = relationSet.iterator(); itr.hasNext(); ) {
							String[] relationArray = itr.next();
							
							if (ArrayUtil.isInclude(relationArray, childArray)) {
								Object[] differenceSet = ArrayUtil.getDifference(relationArray, childArray);
								for (Object relation : differenceSet) {
									keywordSetModel.add(relation.toString());
								}
								
							}
						}
						
						for (Iterator<String> itr = keywordSetModel.iterator(); itr.hasNext(); ) {
							String str = itr.next();
							if (!ArrayUtil.isInclude(childArray, str)) {
								model.addElement(str);
							}
						}
						
						model.setSelectedItem(selectedItem);
					}
				}
			}
			
			add(combo);
		}
		
		label.setOpaque(true);
		combo.setOpaque(false);
	}
	
}
