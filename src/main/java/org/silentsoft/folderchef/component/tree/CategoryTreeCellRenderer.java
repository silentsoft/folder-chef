package org.silentsoft.folderchef.component.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.silentsoft.folderchef.component.custom.KeywordPanel;
import org.silentsoft.folderchef.component.model.Category;

public class CategoryTreeCellRenderer implements TreeCellRenderer {
	
	private final KeywordPanel panel;
	
	public CategoryTreeCellRenderer() {
		super();
		panel = new KeywordPanel();
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Category category = panel.extractCategory(value);
		panel.setContents((DnDJTree)tree, category);
		
		if (selected) {
			panel.getLabel().setBackground(Color.lightGray);
			panel.getLabel().setForeground(Color.black);
        } else {
        	panel.getLabel().setBackground(Color.white);
        	panel.getLabel().setForeground(Color.darkGray);
        }
		
		return panel;
	}
}