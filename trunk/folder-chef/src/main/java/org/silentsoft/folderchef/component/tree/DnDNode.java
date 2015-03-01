package org.silentsoft.folderchef.component.tree;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.Serializable;
 



import javax.swing.tree.DefaultMutableTreeNode;

import org.silentsoft.folderchef.component.model.Category;
import org.silentsoft.folderchef.component.model.Category.Property;
 
/**
 * @author helloworld922
 *         <p>
 * @version 1.0
 *          <p>
 *          copyright 2010 <br>
 * 
 *          You are welcome to use/modify this code for any purposes you want so
 *          long as credit is given to me.
 */
public class DnDNode extends DefaultMutableTreeNode implements Transferable, Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4816704492774592665L;
 
	/**
	 * data flavor used to get back a DnDNode from data transfer
	 */
	public static final DataFlavor DnDNode_FLAVOR = new DataFlavor(DnDNode.class,
			"Drag and drop Node");
 
	/**
	 * list of all flavors that this DnDNode can be transfered as
	 */
	protected static DataFlavor[] flavors = { DnDNode.DnDNode_FLAVOR };
 
	public DnDNode()
	{
		super();
	}
 
	/**
	 * 
	 * Constructs
	 * 
	 * @param data
	 */
	public DnDNode(Serializable data)
	{
		super(data);
	}
	
	/**
	 * Determines if we can add a certain node as a child of this node.
	 * 
	 * @param node
	 * @return
	 */
	public boolean canAdd(DnDNode node)
	{
		if (node != null)
		{
			if (!this.equals(node.getParent()))
			{
				if ((!this.equals(node)))
				{
					Category iCategory = (Category)getUserObject();
					if (iCategory.getProperty() == Property.Directory) {
						if (((Category)node.getUserObject()).getProperty() == Property.Keyword) {
							return false;
						}
					} else if (iCategory.getProperty() == Property.KeywordSet) {
						if (((Category)node.getUserObject()).getProperty() == Property.Directory) {
							return false;
						} else if (((Category)node.getUserObject()).getProperty() == Property.KeywordSet) {
							return false;
						}
					} else if (iCategory.getProperty() == Property.Keyword) {
						return false;
					}
					
					return true;
				}
			}
		}
		return false;
	}
 
	/**
	 * Gets the index node should be inserted at to maintain sorted order. Also
	 * performs checking to see if that node can be added to this node. By
	 * default, DnDNode adds children at the end.
	 * 
	 * @param node
	 * @return the index to add at, or -1 if node can not be added
	 */
	public int getAddIndex(DnDNode node)
	{
		if (!this.canAdd(node))
		{
			return -1;
		}
		return this.getChildCount();
	}
 
	/**
	 * Checks this node for equality with another node. To be equal, this node
	 * and all of it's children must be equal. Note that the parent/ancestors do
	 * not need to match at all.
	 * 
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
		{
			return false;
		}
		else if (!(o instanceof DnDNode))
		{
			return false;
		}
		else if (!this.equalsNode((DnDNode) o))
		{
			return false;
		}
		else if (this.getChildCount() != ((DnDNode) o).getChildCount())
		{
			return false;
		}
		{
			// compare all children
			for (int i = 0; i < this.getChildCount(); i++)
			{
				if (!this.getChildAt(i).equals(((DnDNode) o).getChildAt(i)))
				{
					return false;
				}
			}
			// they are equal!
			return true;
		}
	}
 
	/**
	 * Compares if this node is equal to another node. In this method, children
	 * and ancestors are not taken into concideration.
	 * 
	 * @param node
	 * @return
	 */
	public boolean equalsNode(DnDNode node)
	{
		if (node != null)
		{
			if (this.getAllowsChildren() == node.getAllowsChildren())
			{
				if (this.getUserObject() != null)
				{
					if (this.getUserObject().equals(node.getUserObject()))
					{
						return true;
					}
				}
				else
				{
					if (node.getUserObject() == null)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
 
	/**
	 * @param flavor
	 * @return
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 **/
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (this.isDataFlavorSupported(flavor))
		{
			return this;
		}
		else
		{
			throw new UnsupportedFlavorException(flavor);
		}
	}
 
	/**
	 * @return
	 **/
	public DataFlavor[] getTransferDataFlavors()
	{
		return DnDNode.flavors;
	}
 
	/**
	 * @param flavor
	 * @return
	 **/
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		DataFlavor[] flavs = this.getTransferDataFlavors();
		for (int i = 0; i < flavs.length; i++)
		{
			if (flavs[i].equals(flavor))
			{
				return true;
			}
		}
		return false;
	}
 
	/**
	 * @param temp
	 * @return
	 */
	public int indexOfNode(DnDNode node)
	{
		if (node == null)
		{
			throw new NullPointerException();
		}
		else
		{
			for (int i = 0; i < this.getChildCount(); i++)
			{
				//20150118. do not use 'equals' method. do check custom.
//				if (this.getChildAt(i).equals(node))
				Category source = (Category)((DefaultMutableTreeNode)this.getChildAt(i)).getUserObject();
				Category target = (Category)node.getUserObject();
				if(source.getName().equals(target.getName()))
				{
					return i;
				}
			}
			return -1;
		}
	}
}