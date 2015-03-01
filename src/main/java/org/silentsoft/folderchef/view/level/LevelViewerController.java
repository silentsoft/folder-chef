package org.silentsoft.folderchef.view.level;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.silentsoft.core.CommonConst;
import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.component.text.AutoCompleteTextField;
import org.silentsoft.core.component.tree.TreeIterator;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.event.EventListener;
import org.silentsoft.core.util.MapUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.folderchef.component.model.Category;
import org.silentsoft.folderchef.component.model.Relation;
import org.silentsoft.folderchef.component.model.Category.Property;
import org.silentsoft.folderchef.component.tree.CategoryNode;
import org.silentsoft.folderchef.component.tree.CategoryTreeCellFactory;
import org.silentsoft.folderchef.component.tree.TreeViewWithItems;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;
import org.silentsoft.folderchef.main.FolderChef;
import org.silentsoft.folderchef.view.option.OptionViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelViewerController implements EventListener {
	public enum SortOption {
		ByKeyword,
		ByCount
	};
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LevelViewerController.class);
	
	@FXML
	private MenuItem mntmLoad;
	
	@FXML
	private MenuItem mntmSave;
	
	@FXML
	private MenuItem mntmAbout;
	
	@FXML
	private TextField txtCategory;
	
	@FXML
	private Button btnAdd;
	
	@FXML
	private Button btnMakeKeySet;
	
	@FXML
	private Button btnRename;
	
	@FXML
	private Button btnDelete;
	
	@FXML
	private TreeViewWithItems<CategoryNode> tree;
	
	@FXML
	private ScrollPane scrPaneTree;
	
	@FXML
	private RadioButton rdbtnKeyword;
	
	@FXML
	private RadioButton rdbtnCount;
	
	@FXML
	private AutoCompleteTextField txtKeyword;
	
	@FXML
	private Button btnSearch;
	
	@FXML
	private ListView<String> list;
	
	@FXML
	private ScrollPane scrPaneList;
	
	@FXML
	private CheckBox chkExcludeNoneItem;
	
	@FXML
	private Button btnPrev;
	
	@FXML
	private Button btnNext;
	
	public void initialize() {
		EventHandler.addListener(this);
		
		list.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				list_OnMouseReleased(event);
			}
		});
	}
	
	public void initializeSharedMemory() {
		Object excludeNoneItem = SharedMemory.getDataMap().get(BizConst.KEY_LOAD_EXCLUDE_NONE_ITEM);
		if (excludeNoneItem == null) {
			SharedMemory.getDataMap().put(BizConst.KEY_LOAD_EXCLUDE_NONE_ITEM, true);
		} else {
			chkExcludeNoneItem.setSelected(ObjectUtil.toBoolean(excludeNoneItem));
		}
	}

	@Override
	public void onEvent(String event) {
		if (event.equals(BizConst.EVENT_TRANSFORM_FINISH)) {
			boolean transformResult = ObjectUtil.toBoolean(SharedMemory.getDataMap().get(BizConst.KEY_TRANSFORM_RESULT));
			if (transformResult) {
				EventHandler.removeListener(this);
				EventHandler.callEvent(LevelViewer.class, BizConst.EVENT_VIEW_CATEGORY, false);
//				levelViewer.dispose();
			} else {
				MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_MESSAGE)));
			}
		}
	}
	
	@FXML
	private void mntmLoad_OnAction() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileNameExtensionFilter("Ser file", new String[]{"ser"}));
		
		int returnVal = chooser.showOpenDialog(chooser);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				FileInputStream fis = new FileInputStream(chooser.getSelectedFile());
				ObjectInputStream ois = new ObjectInputStream(fis);

				tree.setItems(FXCollections.observableArrayList((ArrayList<CategoryNode>)ois.readObject()));
				
				fis.close();
			} catch (Exception e) {
				MessageBox.showException(FolderChef.getStage(), e);
			}
		}
	}
	
	@FXML
	private void mntmSave_OnAction() {
		JFileChooser chooser = new JFileChooser();
		chooser.setSelectedFile(new File("DirectoryTree.ser"));
		
		int returnVal = chooser.showSaveDialog(chooser);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile());
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				
				ArrayList<CategoryNode> serializableData = new ArrayList<CategoryNode>(tree.getItems());
				oos.writeObject(serializableData);
				
				oos.flush();
				fos.close();
			} catch (Exception e) {
				MessageBox.showException(FolderChef.getStage(), e);
			}
		}
	}
	
	@FXML
	private void mntmAbout_OnAction() {
		MessageBox.showAbout(FolderChef.getStage(), "Folder Chef 3.0.0", "VOC Contact : silentsoft@naver.com");
	}
	
	@FXML
	private void txtCategory_OnAction() {
		btnAdd_OnMouseReleased();
	}

	@FXML
	private void btnAdd_OnMouseReleased() {
		if (isPossibleCategoryName(txtCategory.getText())) {
			TreeItem<CategoryNode> target = new TreeItem<CategoryNode>(new CategoryNode(txtCategory.getText(), Property.Directory));
			
			TreeItem<CategoryNode> node = tree.getSelectionModel().getSelectedItem();
			if (node == null || node == tree.getRoot()) {
				Iterator<CategoryNode> treeIterator = ((ObservableList<CategoryNode>)tree.getItems()).iterator();
				while (treeIterator.hasNext()) {
					CategoryNode treeCategoryNode = treeIterator.next();
					if (treeCategoryNode.getProperty() == Property.Directory && treeCategoryNode.getName().equals(txtCategory.getText())) {
						MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "There is exists same category !");
						return;
					}
				}
				((ObservableList<CategoryNode>)tree.getItems()).add(target.getValue());
				tree.refresh();
			} else {
				CategoryNode categoryNode = node.getValue();
				if (categoryNode.getProperty() == Property.Directory) {
					Iterator<TreeItem<CategoryNode>> treeIterator = node.getChildren().iterator();
					while (treeIterator.hasNext()) {
						CategoryNode treeCategoryNode = treeIterator.next().getValue();
						if (treeCategoryNode.getProperty() == Property.Directory && treeCategoryNode.getName().equals(txtCategory.getText())) {
							MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "There is exists same category !");
							return;
						}
					}
					
					tree.findNode(categoryNode).getChildren().add(target.getValue());
					tree.refresh();
				} else {
					MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Cannot add category to " + categoryNode.getProperty().toString());
					return;
				}
			}
		}
		
		txtCategory.requestFocus();
		txtCategory.selectAll();
	}
	
	@FXML
	private CategoryNode btnMakeKeySet_OnMouseReleased() {
		CategoryNode target = new CategoryNode("Keyword Set", Property.KeywordSet);
		
		TreeItem<CategoryNode> node = tree.getSelectionModel().getSelectedItem();
		if (node == null || node == tree.getRoot()) {
			((ObservableList<CategoryNode>)tree.getItems()).add(target);
			tree.refresh();
		} else {
			CategoryNode categoryNode = node.getValue();
			if (categoryNode.getProperty() == Property.Directory) {
				tree.findNode(categoryNode).getChildren().add(target);
				tree.refresh();
			} else {
				MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Cannot add KeywordSet to " + categoryNode.getProperty().toString());
				return null;
			}
		}
		
		return target;
	}

	@FXML
	private void btnRename_OnMouseReleased() {
		if (isPossibleCategoryName(txtCategory.getText())) {
			TreeItem<CategoryNode> node = tree.getSelectionModel().getSelectedItem();
			if (node == null) {
				MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Please select category");
				return;
			} else if (node == tree.getRoot()) {
				MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Cannot rename the root");
				return;
			}
			
			CategoryNode categoryNode = node.getValue();
			if (categoryNode.getProperty() == Property.Directory) {
				renameNode(categoryNode, txtCategory.getText());
				tree.refresh();
			} else {
				MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Cannot rename to " + categoryNode.getProperty().toString());
				return;
			}
		}
	}

	@FXML
	private void btnDelete_OnMouseReleased() {
		TreeItem<CategoryNode> node = tree.getSelectionModel().getSelectedItem();
		if (node == null) {
			MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Please select item");
			return;
		} else if (node == tree.getRoot()) {
			MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Cannot delete the root");
			return;
		}
		
		if (node.getParent() == tree.getRoot()) {
			tree.getItems().remove(node.getValue());
			tree.refresh();
		} else {
			tree.findNode(node.getParent().getValue()).getChildren().remove(node.getValue());
			tree.refresh();
		}
	}

	@FXML
	private void rdbtnKeyword_OnAction() {
		displayKeywordToList(SortOption.ByKeyword);
	}

	@FXML
	private void rdbtnCount_OnAction() {
		displayKeywordToList(SortOption.ByCount);
	}

	@FXML
	private void txtKeyword_OnAction() {
		btnSearch_OnMouseReleased();
	}

	@FXML
	private void btnSearch_OnMouseReleased() {
		if (txtKeyword.getText().length() > 0) {
			int searchCount = 0;
			int selectedIndex = list.getSelectionModel().getSelectedIndex();
			int iSize = list.getItems().size();
			for (int i= (selectedIndex == iSize-1) ? 0 : selectedIndex+1; i<iSize; i++) {
				String keyword = list.getItems().get(i).toString().split(CommonConst.VERTICAL_BAR)[BizConst.IDX_KEYSET_KEYWORD];
				if (keyword.toUpperCase().indexOf(txtKeyword.getText().toUpperCase()) != -1) {
					list.getSelectionModel().select(i);
					break;
				}
				
				if (i == iSize-1) {
					// if index is last, back to the first. again and again.
					i = -1;
				}
				
				searchCount++;
				if (searchCount >= iSize) {
					break;
				}
			}
			//
			//-------------------------------------------------------
			//
			ArrayList<String> searchResult = new ArrayList<String>();
			for (int i=0; i<iSize; i++) {
				String keyword = list.getItems().get(i).toString().split(CommonConst.VERTICAL_BAR)[BizConst.IDX_KEYSET_KEYWORD];
				if (keyword.toUpperCase().indexOf(txtKeyword.getText().toUpperCase()) != -1) {
					searchResult.add(list.getItems().get(i).toString());
				}
			}
			SharedMemory.getDataMap().put(BizConst.KEY_SEARCH_RESULT, searchResult);
			EventHandler.callEvent(LevelViewer.class, BizConst.EVENT_VIEW_SEARCH_RESULT, false);
		} else {
			MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Please type the keyword !");
			return;
		}
	}
	
	private void list_OnMouseReleased(MouseEvent event) {
		if (event.getClickCount() >= CommonConst.MOUSE_DOUBLE_CLICK) {
			TreeItem<CategoryNode> node = tree.getSelectionModel().getSelectedItem();
			if (node == null) {
				MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Cannot add keyword to empty space !");
				return;
			} else {
				//Only can add to KeywordSet. Not Directory or Keyword directly.
				String value = ObjectUtil.toString(list.getSelectionModel().getSelectedItem()).split(CommonConst.VERTICAL_BAR)[BizConst.IDX_KEYSET_KEYWORD];
				ArrayList<String> relations = new ArrayList<String>();
				
				Relation relation = (Relation)SharedMemory.getDataMap().get(BizConst.KEY_RELATION);
				Map<String, Integer> relatedMap = relation.getRelation(value);
				if (relatedMap == null) {
					LOGGER.info("Keyword <{}> is not have relation !", new Object[]{value});
				} else {
					Iterator<String> itr = (Iterator<String>) MapUtil.sortByValue(relatedMap).iterator();
					
					while (itr.hasNext()) {
						String key = itr.next();
						int count = ObjectUtil.toInt(relatedMap.get(key));
						relations.add(key + CommonConst.VERTICAL_BAR_CHAR + count);
					}
				}
				
				TreeItem<CategoryNode> target = new TreeItem<CategoryNode>(new CategoryNode(value, value, relations, Property.Keyword));
				
				CategoryNode categoryNode;
				if (node == tree.getRoot()) {
					categoryNode = tree.getRoot().getValue();
				} else {
					categoryNode = tree.findNode(node.getValue());
				}
				
				if (categoryNode.getProperty() == Property.KeywordSet) {
					if (categoryNode.isExistsInsideSameCategoryNode(target.getValue())) {
						MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Selected keyword is already exists !");
						return;
					}
					
					categoryNode.getChildren().add(target.getValue());
					tree.refresh();
				} else if (categoryNode.getProperty() == Property.Directory) {
					if (getNodeCountByProperty(categoryNode, Property.KeywordSet) <= 1) {
						CategoryNode keysetNode = null;
						if (getNodeCountByProperty(categoryNode, Property.KeywordSet) == 0) {
							keysetNode = btnMakeKeySet_OnMouseReleased();
						} else {
							keysetNode = getNodeByProperty(categoryNode, Property.KeywordSet);
							if (keysetNode.isExistsInsideSameCategoryNode(target.getValue())) {
								MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Selected keyword is already exists !");
								return;
							}
						}
						
						//TODO : Why add keyword to root directly ?
//						if (categoryNode == tree.getRoot().getValue()) {
							tree.findNode(keysetNode).getChildren().add(target.getValue());
//						} else {
//							categoryNode.getChildren().get(0).getChildren().add(target.getValue());
//						}
						
						tree.refresh();
					} else {
						MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Select keyword set first !");
						return;
					}
				} else {
					MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Cannot add keyword to " + categoryNode.getProperty().toString());
					return;
				}
			}
		}
	}

	@FXML
	private void chkExcludeNoneItem_OnAction() {
		SharedMemory.getDataMap().put(BizConst.KEY_LOAD_EXCLUDE_NONE_ITEM, chkExcludeNoneItem.isSelected());
	}

	@FXML
	private void btnPrev_OnMouseReleased() {
		SharedMemory.getTransformerMap().clear();
		EventHandler.callEvent(LevelViewer.class, BizConst.EVENT_VIEW_KEYWORD, false);
	}

	@FXML
	private void btnNext_OnMouseReleased() {
		EventHandler.callEvent(LevelViewer.class, BizConst.EVENT_VIEW_SEARCH_RESULT_CLOSE, false);
		EventHandler.callEvent(LevelViewer.class, BizConst.EVENT_VIEW_INFINITY, false);
		
		SharedMemory.getDataMap().put(BizConst.KEY_TRANSFORM_ROOT, tree.getRoot());
		SharedMemory.getDataMap().put(BizConst.KEY_TRANSFORM_ROOT_LIST, ((ObservableList<CategoryNode>)tree.getItems()));
		
		EventHandler.callEvent(LevelViewer.class, BizConst.EVENT_TRANSFORM_EXECUTE);
	}
	
	private int getNodeCountByProperty(CategoryNode node, Property property) {
		int count = 0;
		
		if (node == tree.getRoot().getValue()) {
			for (CategoryNode category : tree.getItems()) {
				if (category.getProperty().equals(property)) {
					count++;
				}
			}
		} else {
			for (CategoryNode category : node.getChildren()) {
				if (category.getProperty().equals(property)) {
					count++;
				}
			}
		}
		
		return count;
	}
	
	private CategoryNode getNodeByProperty(CategoryNode node, Property property) {
		if (node == tree.getRoot().getValue()) {
			for (CategoryNode category : tree.getItems()) {
				if (category.getProperty().equals(property)) {
					return category;
				}
			}
		} else {
			for (CategoryNode category : node.getChildren()) {
				if (category.getProperty().equals(property)) {
					return category;
				}
			}
		}
		
		return null;
	}
	
	public void customizeDirectoryTree() {
		String loadDestination = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_DESTINATION));
		String[] splitName = loadDestination.split(Pattern.quote(System.getProperty("file.separator")));
		String rootName = splitName[splitName.length-1];
		
		ObservableList<CategoryNode> root = (ObservableList<CategoryNode>)SharedMemory.getDataMap().get(BizConst.KEY_TRANSFORM_ROOT_LIST);
		if (root == null) {
			root = FXCollections.observableArrayList();
		}
		
		tree = new TreeViewWithItems<CategoryNode>(new TreeItem<CategoryNode>(new CategoryNode(rootName, Property.Directory)));
		tree.getRoot().setExpanded(true);
		tree.setItems(root);
		tree.setCellFactory(new Callback<TreeView<CategoryNode>, TreeCell<CategoryNode>>() {
			@Override
			public TreeCell<CategoryNode> call(TreeView<CategoryNode> param) {
				return new CategoryTreeCellFactory(tree);
			}
		});
		
		scrPaneTree.setContent(tree);
	}
	
	private void renameNode(CategoryNode sourceNode, String name) {
		CategoryNode node = tree.findNode(sourceNode);
		if (node != null) {
			node.setName(name);
		}
	}
	
	public void displayKeywordToList(SortOption sortOption){
		int upperCount = ObjectUtil.toInt(SharedMemory.getDataMap().get(BizConst.KEY_TRANSFORM_UPPER_COUNT));
		
		ObservableList<String> data = FXCollections.observableArrayList();
		
		ArrayList<String> keywords = new ArrayList<String>();
		
		String key;
		int value;
		Iterator<String> itr = null;
		switch(sortOption){
		case ByKeyword:
			itr = (Iterator<String>) MapUtil.sortByKey(SharedMemory.getExtractorMap()).iterator();
			break;
		case ByCount:
			itr = (Iterator<String>) MapUtil.sortByValue(SharedMemory.getExtractorMap()).iterator();
			break;
		}
		
		while (itr.hasNext()) {
			key = itr.next();
			value = ObjectUtil.toInt(SharedMemory.getExtractorMap().get(key));
			
			if (value >= upperCount) {
				keywords.add(key);
				data.add(key + CommonConst.VERTICAL_BAR_CHAR + value);
			}
		}
		
		list.setItems(data);
		
		txtKeyword.getEntries().addAll(keywords);
	}
	
	private boolean isPossibleCategoryName(String targetName) {
		if ( (targetName.indexOf(File.separator) != -1) || 
			 (targetName.indexOf(CommonConst.COLONE) != -1) ||
			 (targetName.indexOf(CommonConst.STAR) != -1) ||
			 (targetName.indexOf(CommonConst.QUESTION_MARK) != -1) ||
			 (targetName.indexOf(CommonConst.QUOTATION_MARK_DOUBLE) != -1) ||
			 (targetName.indexOf(CommonConst.BRACKET_ANGLE_OPEN) != -1) ||
			 (targetName.indexOf(CommonConst.BRACKET_ANGLE_CLOSE) != -1) ||
			 (targetName.indexOf(CommonConst.VERTICAL_BAR_CHAR) != -1)  ) {
			MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Cannot make category. please check special character !");
			return false;
		}
		
		if (targetName.length() == 0) {
			MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Cannot make category. please check category name !");
			return false;
		}
		
		if (targetName.length() >= CommonConst.MAX_DIRECTORY_LENGTH) {
			MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), "Cannot make category. please check category length !");
			return false;
		}
		
		return true;
	}
}
