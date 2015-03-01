package org.silentsoft.folderchef.transform;

import java.io.File;
import java.util.TreeSet;

import javafx.scene.control.TreeItem;

import org.silentsoft.core.CommonConst;
import org.silentsoft.core.component.tree.TreeIterator;
import org.silentsoft.core.component.tree.TreePath;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.folderchef.component.model.Category;
import org.silentsoft.folderchef.component.model.Category.Property;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelTransformer extends Transformer{
	private static final Logger LOGGER = LoggerFactory.getLogger(LevelTransformer.class);

	public LevelTransformer() {
		super();
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transform() {
		// TODO Auto-generated method stub
		if (isSucceedToLeveling()) {
			SharedMemory.getDataMap().put(BizConst.KEY_TRANSFORM_RESULT, true);
		} else {
			SharedMemory.getDataMap().put(BizConst.KEY_TRANSFORM_RESULT, false);
		}
		
		EventHandler.callEvent(LevelTransformer.class, BizConst.EVENT_TRANSFORM_FINISH);
	}

	private boolean isSucceedToLeveling() {
		SharedMemory.getTransformerMap().clear();
		TreeItem<Category> root = (TreeItem<Category>)SharedMemory.getDataMap().get(BizConst.KEY_TRANSFORM_ROOT);
		
		int childCount = 0;
		
		TreeIterator<Category> itr = new TreeIterator<Category>(root);
		while (itr.hasNext()) {
			TreeItem<Category> node = itr.next();
			Category category = node.getValue();
			
			if (category.getProperty() == Property.KeywordSet) {
				childCount = node.getChildren().size();
				if (childCount > 0) {
					String keywords = "";
					TreeSet<String> keywordSet = new TreeSet<String>();
					
					for (int i=0; i<childCount; i++) {
						keywordSet.add(node.getChildren().get(i).getValue().getName());
					}
					
					for (String keyword : keywordSet) {
						keywords += keyword + CommonConst.VERTICAL_BAR_CHAR;
					}
					
					TreeItem[] paths = TreePath.getPath(node);
					String directory = "";
					for (int i=0; i<paths.length-1; i++) {
						directory = directory.concat(((Category)paths[i].getValue()).getName());
						if (i+1 < paths.length) {
							directory = directory.concat(File.separator);
						}
					}
					
					if (SharedMemory.getTransformerMap().containsKey(keywords)) {
						SharedMemory.getDataMap().put(BizConst.KEY_MESSAGE, String.format("Keywords overlap between <%s> and <%s>", SharedMemory.getTransformerMap().get(keywords).toString(), directory));
						SharedMemory.getTransformerMap().clear();
						return false;
					} else {
						SharedMemory.getTransformerMap().put(keywords, directory);
						LOGGER.info("keywords : <{}>, directory : <{}>", new Object[]{keywords, directory});
					}
				}
			}
		}
		
		TreeItem cloneRoot = deepClone(root);
		SharedMemory.getDataMap().put(BizConst.KEY_LOAD_ROOT, cloneRoot);
		
		return true;
	}
	
	private TreeItem deepClone(TreeItem root) {
		TreeItem cloneItem = new TreeItem((Category)root.getValue());
		
		int childCount = root.getChildren().size();
		for (int i = 0; i<childCount; i++) {
			TreeItem child = (TreeItem) root.getChildren().get(i);
			Category category = (Category)child.getValue();
			//LOGGER.debug("deepCloning : <{}>/<{}>", new Object[]{category.getName(), category.getProperty()});
			if (category.getProperty() != Property.KeywordSet) {
				cloneItem.getChildren().add(deepClone(child));
			}
		}
		
		return cloneItem;
	}
}
