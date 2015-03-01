package org.silentsoft.folderchef.component.tree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.silentsoft.folderchef.component.model.Category;

public class SearchTreeCellRenderer implements TreeCellRenderer {
	private JLabel label;
	
	public SearchTreeCellRenderer() {
		label = new JLabel();
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Object object = ((DefaultMutableTreeNode) value).getUserObject();
		
		String text = "";
		String toolTipText = "";
		ImageIcon imageIcon = null;
		
		if (object instanceof Category) {
			Category category = (Category) object;
			
			text = category.getName();
			toolTipText = category.getValue();
			
			switch (category.getProperty()) {
			case Directory:
				imageIcon = new ImageIcon("icon/directory.png");
				break;
			case File:
				imageIcon = new ImageIcon("icon/file.png");
				break;
			case KeywordSet:
				imageIcon = new ImageIcon("icon/keyword_set.png");
				break;
			case Keyword:
				imageIcon = new ImageIcon("icon/keyword.png");
				break;
			default :
				imageIcon = new ImageIcon("icon/none_property.png");
				break;
			}
		} else {
			text += value;
			toolTipText += value;
			imageIcon = new ImageIcon("icon/none_item.png");
		}
		
		tree.setToolTipText(toolTipText);
		
		label.setText(text);
		label.setIcon(imageIcon);
		label.setOpaque(true);
		
		if(selected){
            label.setBackground(Color.lightGray);
            label.setForeground(Color.black);
        } else {
            label.setBackground(Color.white);
            label.setForeground(Color.darkGray);
        }
		
		return label;
	}
}
