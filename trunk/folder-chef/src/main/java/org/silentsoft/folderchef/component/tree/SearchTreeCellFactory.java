package org.silentsoft.folderchef.component.tree;

import java.io.FileInputStream;

import org.silentsoft.folderchef.component.model.Category;
import org.silentsoft.folderchef.component.model.Category.Property;
import org.silentsoft.folderchef.core.SharedMemory;
import org.silentsoft.ui.component.tree.TreeIterator;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;

public class SearchTreeCellFactory extends TreeCell<CategoryNode> {
	
	private ImageView image;
	private Label label;
	private HBox pane;
    private ContextMenu addMenu = new ContextMenu();

    private CategoryNode item;
    private TreeView<CategoryNode> parentTree;
    
    public SearchTreeCellFactory() {
    	MenuItem addMenuItem = new MenuItem("Add Employee");
        addMenu.getItems().add(addMenuItem);
        addMenuItem.setOnAction(new EventHandler() {
            public void handle(Event t) {
                TreeItem<CategoryNode> newEmployee = new TreeItem<CategoryNode>(new CategoryNode("New Employee", Property.Keyword));
                getTreeItem().getChildren().add(newEmployee);
            }
        });
    }
    
    public SearchTreeCellFactory(final TreeView<CategoryNode> parentTree) {
    	this.parentTree = parentTree;
    	
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
				
//				SharedMemory.getDataMap().put("SearchTreeDraggedItem", getTreeItem());
				SharedMemory.getDataMap().put("SearchTreeDraggedItem", parentTree.getSelectionModel().getSelectedItems());
				
				event.consume();
			}
		});
    	
//    	// ON TARGET NODE
//    	setOnDragOver(new EventHandler<DragEvent>() {
//			@Override
//			public void handle(DragEvent event) {
//				if (item == null) {
//					return;
//				}
//				
//				if (event.getDragboard().hasString()) {
//					Category valueToMove = (Category)event.getDragboard().getContent(DataFormat.PLAIN_TEXT);
//					if (valueToMove != item) {
//						event.acceptTransferModes(TransferMode.MOVE);
//					}
//				}
//				
//				event.consume();
//			}
//		});
    	
//    	setOnDragDropped(new EventHandler<DragEvent>(){
//			@Override
//			public void handle(DragEvent event) {
//				if (item == null) {
//					return;
//				}
//				
//				Category valueToMove = (Category)event.getDragboard().getContent(DataFormat.PLAIN_TEXT);
////				TreeItem<Category> itemToMove = search(parentTree.getRoot(), valueToMove);
//				TreeItem<Category> itemToMove = (TreeItem<Category>) SharedMemory.getDataMap().get("DraggedItem");
//				TreeItem<Category> newParent = search(parentTree.getRoot(), item);
//				
//				//Remove from former parent.
//				System.out.println(itemToMove.getParent().getValue().getName());
//				System.out.println(itemToMove.getValue().getName());
//				itemToMove.getParent().getChildren().remove(itemToMove);
//				
//				//Add to new parent.
//				newParent.getChildren().add(itemToMove);
//				
//				newParent.setExpanded(true);
//				event.consume();
//			}
//    	});
    }
    
//    private TreeItem<Category> search(final TreeItem<Category> currentNode, final Category valueToSearch) {
//    	TreeItem<Category> result = null;
//    	if (currentNode.getValue().getName() == valueToSearch.getName()) {
//    		result = currentNode;
//    	} else if (!currentNode.isLeaf()) {
//    		for (TreeItem<Category> child : currentNode.getChildren()) {
//    			result = search(child, valueToSearch);
//    			if (result != null) {
//    				break;
//    			}
//    		}
//    	}
//    	
//    	return result;
//    }
    
    private TreeItem<Category> search(TreeItem<Category> currentNode, Category valueToSearch) {
    	TreeItem<Category> result = null;
    	
    	TreeIterator<Category> itr = new TreeIterator<Category>(currentNode);
    	while (itr.hasNext()) {
    		TreeItem<Category> target = itr.next();
    		if (target.getValue() != null && target.getValue().getName().equals(valueToSearch.getName())) {
    			result = target;
    			break;
    		}
    	}
    	
    	return result;
    }
    
    @Override
    public void updateItem(CategoryNode item, boolean empty) {
        super.updateItem(item, empty);
        
        this.item = item;

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
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
    				image = new ImageView(new Image(new FileInputStream("icon/keyword_set.png")));
    				break;
    			case Keyword:
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
        	
        	setGraphic(pane);
        	setContextMenu(addMenu);
        }
    }
}
