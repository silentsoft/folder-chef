package org.silentsoft.folderchef.view.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.MapUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.folderchef.component.custom.SearchPanel;
import org.silentsoft.folderchef.component.model.Category;
import org.silentsoft.folderchef.component.model.Relation;
import org.silentsoft.folderchef.component.model.Category.Property;
import org.silentsoft.folderchef.component.tree.CategoryNode;
import org.silentsoft.folderchef.component.tree.SearchTreeCellFactory;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.Pair;

public class SearchResultViewerController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchResultViewerController.class);

	@FXML
	private TreeView<CategoryNode> tree;
	
	@FXML
	private ScrollPane scrPane;
	
	public void customizeKeywordTree() {
//		root = new DnDNode(new Category("result", Property.KeywordSet));
		TreeItem<CategoryNode> root = new TreeItem<CategoryNode>(new CategoryNode("result", Property.KeywordSet));		
		root.setExpanded(true);
		
//		tree = new DnDJTree(root);
//		tree.setCellRenderer(new SearchTreeCellRenderer());
		
		Relation relation = (Relation)SharedMemory.getDataMap().get(BizConst.KEY_RELATION);
		ArrayList<String> searchResult = (ArrayList<String>)SharedMemory.getDataMap().get(BizConst.KEY_SEARCH_RESULT);
		for (int i=0, j=searchResult.size(); i<j; i++) {
			String result = searchResult.get(i);
			String keyword = result.split(CommonConst.VERTICAL_BAR)[BizConst.IDX_KEYSET_KEYWORD];
			ArrayList<String> relations = new ArrayList<String>();
			
			Map<String, Integer> relatedMap = relation.getRelation(keyword);
			if (relatedMap == null) {
				LOGGER.info("Keyword <{}> is not have relation !", new Object[]{keyword});
			} else {
				String key;
				int count;
				Iterator<String> itr = (Iterator<String>) MapUtil.sortByValue(relatedMap).iterator();
				
				while (itr.hasNext()) {
					key = itr.next();
					count = ObjectUtil.toInt(relatedMap.get(key));
					relations.add(key + CommonConst.VERTICAL_BAR_CHAR + count);
				}
			}
			
//			DnDNode target = new DnDNode(new Category(keyword, keyword, relations, Property.Keyword));
//			TreeItem<Category> target = new TreeItem<>(new Category(keyword, keyword, relations, Property.Keyword), new SearchPanel(Property.Keyword));
			TreeItem<CategoryNode> target = new TreeItem<CategoryNode>(new CategoryNode(keyword, keyword, relations, Property.Keyword));
//			if (root.indexOfNode(target) == -1) {
			if (root.getChildren().indexOf(target) == -1) {
//				((DefaultTreeModel)tree.getModel()).insertNodeInto(target, root, root.getChildCount());
				root.getChildren().add(target);
			}
		}
		
		tree = new TreeView<CategoryNode>(root);
		tree.setCellFactory(new Callback<TreeView<CategoryNode>, TreeCell<CategoryNode>>() {
			@Override
			public TreeCell<CategoryNode> call(TreeView<CategoryNode> param) {
				return new SearchTreeCellFactory(param);
			}
		});
		tree.setShowRoot(false);
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
//		for (int i=0, j=tree.getRowCount(); i<j; i++) {
//			tree.expandRow(i);
//		}
		
//		scrollPane.setViewportView(tree);
		scrPane.setContent(tree);
	}
}
