package org.silentsoft.folderchef.component.tree;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;

import org.silentsoft.core.CommonConst;
import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.component.tree.TreeIterator;
import org.silentsoft.core.util.ArrayUtil;
import org.silentsoft.core.util.MapUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.folderchef.component.model.Category;
import org.silentsoft.folderchef.component.model.Relation;
import org.silentsoft.folderchef.component.model.Category.Property;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;
import org.silentsoft.folderchef.main.FolderChef;

public class CategoryTreeCellFactory extends TreeCell<CategoryNode> {

	private ImageView image;
	private Label label;
	private HBox pane;
	
	private ContextMenu menu;
	
	private CategoryNode item;
	private TreeViewWithItems<CategoryNode> parentTree;
	
	/**
	 * Drag enabled tree cell factory
	 * @param parentTree
	 */
	public CategoryTreeCellFactory(final TreeViewWithItems<CategoryNode> parentTree) {
		this(parentTree, true);
	}
	
	/**
	 * Can set drag enabled by <code>isDragEnabled</code> parameter
	 * @param parentTree
	 * @param isDragEnabled
	 */
	public CategoryTreeCellFactory(final TreeViewWithItems<CategoryNode> parentTree, boolean isDragEnabled) {
		this.parentTree = parentTree;
		
		if (isDragEnabled) {
			// ON SOURCE NODE
			setOnDragDetected(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (item == null) {
						return;
					}
					
					Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
					ClipboardContent content = new ClipboardContent();
					content.put(DataFormat.PLAIN_TEXT, item);
					dragBoard.setContent(content);
					
					SharedMemory.getDataMap().put("CategoryTreeDraggedItem", getTreeItem());
					
					event.consume();
				}
			});
			
			// ON TARGET NODE
	    	setOnDragOver(new EventHandler<DragEvent>() {
				@Override
				public void handle(DragEvent event) {
					if (item == null) {
						return;
					}
					
					if (event.getDragboard().hasString()) {
						CategoryNode valueToMove = (CategoryNode)event.getDragboard().getContent(DataFormat.PLAIN_TEXT);
						if (valueToMove != item) {
							event.acceptTransferModes(TransferMode.MOVE);
						}
					}
					
					event.consume();
				}
			});
	    	
	    	setOnDragDropped(new EventHandler<DragEvent>(){
				@Override
				public void handle(DragEvent event) {
					if (item == null) {
						return;
					}
					
					ObservableList<TreeItem<CategoryNode>>  searchTreeItemsToMove = (ObservableList<TreeItem<CategoryNode>>) SharedMemory.getDataMap().get("SearchTreeDraggedItem");
					TreeItem<CategoryNode> categoryTreeItemToMove = (TreeItem<CategoryNode>) SharedMemory.getDataMap().get("CategoryTreeDraggedItem");
					CategoryNode newParent = parentTree.findNode(getTreeItem().getValue());
					if (newParent == null) {
						newParent = parentTree.getRoot().getValue();
					}
					
					if (searchTreeItemsToMove != null) {
						Iterator<TreeItem<CategoryNode>> itr = searchTreeItemsToMove.iterator();
						while (itr.hasNext()) {
							TreeItem<CategoryNode> searchTreeItemToMove = itr.next();
							
							if (searchTreeItemToMove != null) {
								if (newParent.getProperty() == Property.KeywordSet) {
									if (newParent.isExistsInsideSameCategoryNode(searchTreeItemToMove.getValue())) {
										MessageBox.showError(FolderChef.getStage(), "target node already exists " + searchTreeItemToMove.getValue().getName());
									} else {
										searchTreeItemToMove.getParent().getChildren().remove(searchTreeItemToMove);
										
										//Add to new parent.
										newParent.getChildren().add(searchTreeItemToMove.getValue());
										parentTree.refresh();
									}
								} else {
									MessageBox.showError(FolderChef.getStage(), "Cannot add keyword to " + newParent.getProperty().toString());
								}
							}
						}
						
						SharedMemory.getDataMap().put("SearchTreeDraggedItem", null);
					} else if (categoryTreeItemToMove != null) {
						CategoryNode moveTargetNode = parentTree.findNode(categoryTreeItemToMove.getValue());
						
						boolean canMove = true;
						
						if (newParent.getProperty() == Property.Directory) {
							if (moveTargetNode.getProperty() == Property.Keyword) {
								canMove = false;
							}
						} else if (newParent.getProperty() == Property.KeywordSet) {
							if (moveTargetNode.getProperty() == Property.Directory) {
								canMove = false;
							} else if (moveTargetNode.getProperty() == Property.KeywordSet) {
								canMove = false;
							}
						} else if (newParent.getProperty() == Property.Keyword) {
							if (moveTargetNode.getProperty() == Property.Keyword) {
								canMove = false;
							} else if (moveTargetNode.getProperty() == Property.KeywordSet) {
								canMove = false;
							}
						}
						
						if (canMove) {
							boolean isExistsInsideSameCategory = isExistsInsideSameCategoryNode(newParent, moveTargetNode);
							
							if (!isExistsInsideSameCategory || 
								(newParent.getProperty() == Property.Directory && moveTargetNode.getProperty() == Property.KeywordSet)) {
								if (categoryTreeItemToMove.getParent().equals(parentTree.getRoot())) {
									((ObservableList<CategoryNode>)parentTree.getItems()).remove(moveTargetNode);
								} else {
									parentTree.findNode(categoryTreeItemToMove.getParent().getValue()).getChildren().remove(moveTargetNode);
								}
								
								//Add to new parent.
								if (newParent.equals(parentTree.getRoot().getValue())) {
									((ObservableList<CategoryNode>)parentTree.getItems()).add(moveTargetNode);
								} else {
									newParent.getChildren().add(moveTargetNode);
								}
								
								parentTree.refresh();
							} else {
								MessageBox.showError(FolderChef.getStage(), "target node already exists " + moveTargetNode.getName());
							}
						} else {
							MessageBox.showError(FolderChef.getStage(),
									"Cannot add " + categoryTreeItemToMove.getValue().getProperty().toString() + " to " + newParent.getProperty().toString());
						}
						
						SharedMemory.getDataMap().put("CategoryTreeDraggedItem", null);
					}
					
					event.consume();
				}
	    	});
		}
	}
	
	@Override
	protected void updateItem(CategoryNode item, boolean empty) {
		super.updateItem(item, empty);
        
        this.item = item;

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
        	boolean showMenu = false;
        	
        	label = new Label();
        	label.setText(item.getName());
        	label.setTooltip(new Tooltip(item.getValue()));
        	
        	try {
    			switch (item.getProperty()) {
    			case Directory:
    				image = new ImageView(new Image(new FileInputStream("icon/directory.png")));
    				break;
    			case File:
    				image = new ImageView(new Image(new FileInputStream("icon/file.png")));
    				break;
    			case KeywordSet:
    				showMenu = true;
    				image = new ImageView(new Image(new FileInputStream("icon/keyword_set.png")));
    				break;
    			case Keyword:
    				showMenu = true;
    				image = new ImageView(new Image(new FileInputStream("icon/keyword.png")));
    				break;
    			default :
    				image = new ImageView(new Image(new FileInputStream("icon/none_property.png")));
    				break;
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        	
        	pane = new HBox();
        	pane.getChildren().add(image);
        	pane.getChildren().add(label);
        	
        	if (showMenu) {
        		menu = null;
        		menu = new ContextMenu();
        		
        		if (item.getProperty() == Property.Keyword) {
        			ArrayList<String> relations = item.getRelations();
        			if (relations != null) {
        				if (relations.size() > 0) {
        					for (String relation : relations) {
        						MenuItem menuItem = new MenuItem(relation);
        						menuItem.setOnAction(new EventHandler() {
									@Override
									public void handle(Event event) {
										insertChildrenFromText(relation);
									}
        						});
        						
        						menu.getItems().add(menuItem);
        					}
        				}
        			}
        		} else if (item.getProperty() == Property.KeywordSet) {
        			String[] childArray = new String[getTreeItem().getChildren().size()];
        			for (int i=0, j=childArray.length; i<j; i++) {
        				TreeItem<CategoryNode> child = getTreeItem().getChildren().get(i);
        				childArray[i] = ((Category)child.getValue()).getName();
        			}
        			Arrays.sort(childArray);
        			
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
        					MenuItem menuItem = new MenuItem(str);
        					menuItem.setOnAction(new EventHandler(){
								@Override
								public void handle(Event event) {
									insertChildrenFromText(str);
								}
        					});
        					
        					menu.getItems().add(menuItem);
        				}
        			}
        		}
        		
        		setContextMenu(menu);
        	}
        	
        	setGraphic(pane);
        }
	}
	
	public boolean isExistsInsideSameCategoryNode(CategoryNode sourceNode, CategoryNode targetNode) {
		boolean result = false;
		
		if (sourceNode == parentTree.getRoot().getValue()) {
			for (TreeItem<CategoryNode> treeItem : parentTree.getRoot().getChildren()) {
				CategoryNode categoryNode = treeItem.getValue();
				
				boolean isSame = true;
				
				if (categoryNode.getName() != null && targetNode.getName() != null) {
					if (!categoryNode.getName().equals(targetNode.getName())) {
						isSame = false;
					}
				}
				
				if (categoryNode.getValue() != null && targetNode.getValue() != null) {
					if (!categoryNode.getValue().equals(targetNode.getValue())) {
						isSame = false;
					}
				}
				
				if (categoryNode.getProperty() != null && targetNode.getProperty() != null) {
					if (!categoryNode.getProperty().equals(targetNode.getProperty())) {
						isSame = false;
					}
				}
				
				if (categoryNode.getRelations() != null && targetNode.getRelations() != null) {
					if (!categoryNode.getRelations().equals(targetNode.getRelations())) {
						isSame = false;
					}
				}
				
				if (isSame) {
					result = true;
				}
			}
		} else {
			result = sourceNode.isExistsInsideSameCategoryNode(targetNode);
		}
		
		return result;
	}
	
	private void insertChildrenFromText(String text) {
		String keyword = "";
		
		if (item.getProperty() == Property.Keyword) {
			keyword = text.split(CommonConst.VERTICAL_BAR)[BizConst.IDX_KEYSET_KEYWORD];
		} else if(item.getProperty() == Property.KeywordSet) {
			keyword = text;
		} else {
			return;
		}
		
		ArrayList<String> relations = new ArrayList<String>();
		
		Relation relation = (Relation)SharedMemory.getDataMap().get(BizConst.KEY_RELATION);
		Map<String, Integer> relatedMap = relation.getRelation(keyword);
		if (relatedMap != null) {
			Iterator<String> itr = (Iterator<String>)MapUtil.sortByValue(relatedMap).iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				int count = ObjectUtil.toInt(relatedMap.get(key));
				relations.add(key + CommonConst.VERTICAL_BAR_CHAR + count);
			}
		}
		
		CategoryNode parentNode = null;
		if (item.getProperty() == Property.Keyword) {
			parentNode = getTreeItem().getParent().getValue();
		} else if (item.getProperty() == Property.KeywordSet) {
			parentNode = getTreeItem().getValue();
		}
		
		CategoryNode target = new CategoryNode(keyword, keyword, relations, Property.Keyword);
		if (parentNode.getChildren().indexOf(target) == -1) {
			parentTree.findNode(parentNode).getChildren().add(target);
			parentTree.refresh();
		}
	}
}
