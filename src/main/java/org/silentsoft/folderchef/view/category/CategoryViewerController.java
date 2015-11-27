package org.silentsoft.folderchef.view.category;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import org.apache.commons.lang.StringUtils;
import org.controlsfx.dialog.Dialog;
import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.folderchef.component.model.Category;
import org.silentsoft.folderchef.component.model.Category.Property;
import org.silentsoft.folderchef.component.tree.CategoryNode;
import org.silentsoft.folderchef.component.tree.CategoryTreeCellFactory;
import org.silentsoft.folderchef.component.tree.TreeViewWithItems;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;
import org.silentsoft.folderchef.main.FolderChef;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.ui.component.messagebox.MessageBox;
import org.silentsoft.ui.component.tree.TreeIterator;
import org.silentsoft.ui.component.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryViewerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryViewerController.class);

	private TreeItem<CategoryNode> root;
	
	@FXML
	private TreeViewWithItems<CategoryNode> tree;
	
	@FXML
	private ScrollPane scrPane;
	
	@FXML
	private Button btnPrev;
	
	@FXML
	private Button btnNext;
	
	public void initialize() {
		tree.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				tree_OnMouseReleased(event);
			}
		});
	}
	
	@FXML
	private void btnPrev_OnMouseReleased() {
		SharedMemory.getDataMap().put(BizConst.KEY_LOAD_ROOT, null);
		EventHandler.callEvent(CategoryViewer.class, BizConst.EVENT_VIEW_LEVEL, false);
		//categoryViewer.dispose();
	}
	
	@FXML
	private void btnNext_OnMouseReleased() {
		String option = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_TYPE));
		String extractTarget = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_TARGET));
		String extractExtensions = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_EXTENSIONS));
		String loadDestination = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_DESTINATION));
		int fileCount = ObjectUtil.toInt(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_FILE_COUNT_PRE));
		int excludeCount = ObjectUtil.toInt(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_FILE_COUNT_EXCLUDE));
		
		String masthead = "Are you sure to use [" + option + "] option ?\r\n";
			   masthead+= "\r\n";
			   masthead+= (fileCount-excludeCount) + " files that Folder Chef shows in the previous page will be " + option.toLowerCase() + " to " + loadDestination;
			   
		String message = "There are " + fileCount + " files in target folders, " + "[" + extractTarget + "]";
		if (MessageBox.showConfirm(FolderChef.getStage(), masthead, message) == Dialog.ACTION_YES) {
			EventHandler.callEvent(CategoryViewer.class, BizConst.EVENT_VIEW_LOAD, false);
			//categoryViewer.dispose();
		}
		
//		String message = "Are you sure to use [" + option + "] option ?\r\n";
//			   message+= "\r\n";
//			   message+= "Extract target [" + extractTarget + "] has " + fileCount + " files for [" + extractExtensions + "]\r\n";
//			   message+= "Simulated " + (fileCount-excludeCount) + " files will " + option.toLowerCase() + " to " + loadDestination;
//		
//		if (MessageBox.showConfirm(Sweeper.getStage(), message) == Dialog.ACTION_YES) {
//			EventHandler.callEvent(CategoryViewer.class, BizConst.EVENT_VIEW_LOAD);
//			//categoryViewer.dispose();
//		}
	}
	
	private void tree_OnMouseReleased(MouseEvent event) {
		if (event.getClickCount() >= CommonConst.MOUSE_DOUBLE_CLICK) {
			TreeItem<CategoryNode> node = tree.getSelectionModel().getSelectedItem();
			if (node != null) {
				Category category = node.getValue();
				if (category.getProperty() == Property.File) {
					try {
						if (Files.exists(Paths.get(category.getValue()))) {
							SystemUtil.runCommand(category.getValue());
						} else {
							LOGGER.error("File <{}> not found !", new Object[]{category.getValue()});
							MessageBox.showError(FolderChef.getStage(), String.format("File <{%s}> not found !", category.getValue()));
						}
					} catch (Exception e) {
						LOGGER.error("File execute failure !", e);
						MessageBox.showException(FolderChef.getStage(), e);
					}
				}
			}
		}
	}
	
	public void initializeSharedMemory() {
		SharedMemory.getDataMap().put(BizConst.KEY_LOAD_FILE_COUNT_PRE, 0);
		SharedMemory.getDataMap().put(BizConst.KEY_LOAD_FILE_COUNT_EXCLUDE, 0);
	}
	
	/**
	 * Display directory tree without KeywordSet and Keyword. (Only Directory)
	 */
	public void customizeDirectoryTree() {
		TreeItem<CategoryNode> root = (TreeItem<CategoryNode>)SharedMemory.getDataMap().get(BizConst.KEY_LOAD_ROOT);
		
		root.setExpanded(true);
		this.root = root;
		tree.setRoot(root);
		tree.setCellFactory(new Callback<TreeView<CategoryNode>, TreeCell<CategoryNode>>() {
			@Override
			public TreeCell<CategoryNode> call(TreeView<CategoryNode> param) {
				return new CategoryTreeCellFactory(tree, false);
			}
		});
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		scrPane.setContent(tree);
	}
	
	/**
	 * Display to grid pre simulated directory tree.
	 * @throws Exception
	 */
	public void preLoad() throws Exception {
		String[] extractTargets = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_TARGET)).split(CommonConst.VERTICAL_BAR);
		String[] extractExtensions = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_EXTENSIONS)).split(CommonConst.COMMA);
		
		for (String extractTarget : extractTargets) {
			LOGGER.info("Start to iterate from target <{}> by <{}>", new Object[]{extractTarget, extractExtensions});
			
			Path targetPath = Paths.get(extractTarget);
			Files.walkFileTree(targetPath, new FileVisitor<Path>() {

				public FileVisitResult postVisitDirectory(Path arg0,
						IOException arg1) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult preVisitDirectory(Path arg0,
						BasicFileAttributes arg1) throws IOException {
					if (arg0.toString().length() >= CommonConst.MAX_DIRECTORY_LENGTH) {
						LOGGER.error("The depth is to deep ! it will be skip subtree <{}>", new Object[]{arg0});
						return FileVisitResult.SKIP_SUBTREE;
					}
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFile(Path arg0,
						BasicFileAttributes arg1) throws IOException {
					boolean isTargetToExtract = false;
					for (String ext : extractExtensions) {
						if (arg0.toString().toUpperCase().endsWith(ext.toUpperCase())) {
							isTargetToExtract = true;
							break;
						}
					}
					
					if (isTargetToExtract) {
						int fileCount = ObjectUtil.toInt(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_FILE_COUNT_PRE));
						SharedMemory.getDataMap().put(BizConst.KEY_LOAD_FILE_COUNT_PRE, ++fileCount);
						//LOGGER.debug("Iterating for <{}> time.", new Object[]{fileCount});
						
						String fileName = arg0.getFileName().toString();
						String fullPath = arg0.toAbsolutePath().toString();
						String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf(CommonConst.DOT));
						String[] keywords = getKeywordsFromFileName(fileNameWithoutExt);
						
						addDirectoryTreeByKeywords(fileName, fullPath, keywords);
					}
					
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFileFailed(Path arg0,
						IOException arg1) throws IOException {
					LOGGER.error("Visit file failed ! <{}>", new Object[]{arg0});
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}
	
	private void addDirectoryTreeByKeywords(String fileName, String fullPath, String[] keywords) {
		TreeItem<CategoryNode> directoryNode = findDirectoryNodeByKeywords(keywords); 
		if (directoryNode != null) {
			directoryNode.getChildren().add(new TreeItem<CategoryNode>(new CategoryNode(fileName, fullPath, Property.File)));
		}
	}
	
	private TreeItem<CategoryNode> findDirectoryNodeByKeywords(String[] keywords) {
		String[] keywordsInMap = null;
		
		float maxMatchingRate = 0;
		float currMatchingRate = 0;
		String directory = "";
		
		Set<Entry<String, Object>> entrySet = SharedMemory.getTransformerMap().entrySet();
		for (Map.Entry<String, Object> entry : entrySet) {
			keywordsInMap = entry.getKey().split(CommonConst.VERTICAL_BAR);
			
			currMatchingRate = getMatchingRate(keywords, keywordsInMap);
			
			if (currMatchingRate > maxMatchingRate) {
				maxMatchingRate = currMatchingRate;
				directory = ObjectUtil.toString(entry.getValue());
			}
		}
		
		TreeIterator<CategoryNode> itr = new TreeIterator<CategoryNode>(root);
		while (itr.hasNext()) {
			TreeItem<CategoryNode> node = itr.next();
			CategoryNode category = node.getValue();
			
			if (category.getProperty() == Property.Directory) {
				TreeItem[] paths = TreePath.getPath(node);
				String currentDirectory = "";
				for (int i=0; i<paths.length; i++) {
					currentDirectory = currentDirectory.concat(((Category)paths[i].getValue()).getName());
					if (i < paths.length) {
						currentDirectory = currentDirectory.concat(File.separator);
					}
				}
				
				if (currentDirectory.equals(directory)) {
					return node;
				}
			}
		}
		
		// none-target is not to move anywhere when user set excludeNoneItem
		boolean excludeNoneItem = ObjectUtil.toBoolean(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_EXCLUDE_NONE_ITEM));
		if (excludeNoneItem) {
			int excludeCount = ObjectUtil.toInt(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_FILE_COUNT_EXCLUDE));
			SharedMemory.getDataMap().put(BizConst.KEY_LOAD_FILE_COUNT_EXCLUDE, ++excludeCount);
			return null;
		}
		
		// none-target is go to root
		return root;
	}
	
	private int getMatchingCount(String[] source, String[] target) {
		int matchingCount = 0;
		
		int sourceLength = source.length;
		int targetLength = target.length;
		if (sourceLength > 0 && targetLength > 0) {
			for (int i =0; i<targetLength; i++) {
				for (int j=0; j<sourceLength; j++) {
					if (target[i].equals(source[j])) {
						matchingCount ++;
					}
				}
			}
		}
		
		return matchingCount;
	}
	
	private float getMatchingRate(String[] source, String[] target) {
		float matchingRate = 0;
		int matchingCount = 0;
		
		int sourceLength = source.length; // from file
		int targetLength = target.length; // from keyword set
		if (sourceLength > 0 && targetLength > 0) {
			for (int i =0; i<targetLength; i++) {
				for (int j=0; j<sourceLength; j++) {
					if (target[i].equals(source[j])) {
						matchingCount ++;
					}
				}
			}
		}
		
//		if (matchingCount > 0 && sourceLength > 0){
//			matchingRate = (100/sourceLength) * matchingCount;
//		}
//		if (matchingCount > 0 && targetLength > 0){
			//if file names array are higher, then rate is lower.
			//so, do not get the rate from file names arry. get the rate from keyword set.
//			matchingRate = (100/targetLength) * matchingCount;
			//but.. if keyword set array are lower, then rate is near the 100... ;;
//		}
		if (matchingCount > 0 && sourceLength > 0 && targetLength > 0) {
			//so i ready this. make it from all of information.
			float sourceCoverage = new Float(100.0f / sourceLength);
			float targetCoverage = new Float(matchingCount / targetLength) + matchingCount;
			
			matchingRate = (float)(sourceCoverage * targetCoverage);
		}
		
		return matchingRate;
	}
	
	/**
	 * get keywords from filename.
	 * @param fileNameWithoutExt
	 * @return
	 */
	private String[] getKeywordsFromFileName(String fileNameWithoutExt) {
		String originalPath = fileNameWithoutExt;
		ArrayList<String> removedKeywords = new ArrayList<String>();
	
		fileNameWithoutExt = fileNameWithoutExt.replace(File.separatorChar, CommonConst.VERTICAL_BAR_CHAR);
		fileNameWithoutExt = fileNameWithoutExt.replace(CommonConst.UNDER_BAR_CHAR, CommonConst.VERTICAL_BAR_CHAR);
		fileNameWithoutExt = fileNameWithoutExt.replace(CommonConst.SPACE_BAR_CHAR, CommonConst.VERTICAL_BAR_CHAR);
		
		LinkedList<String> keywords = new LinkedList<String>(Arrays.asList(fileNameWithoutExt.split(CommonConst.VERTICAL_BAR)));
		
		String keyword = "";
		for (int i=0; i<keywords.size(); i++) {
			keyword = keywords.get(i);
			
			if ( (keyword.endsWith(CommonConst.DOT)) ||
				 (keyword.startsWith(CommonConst.BRACKET_OPEN) && keyword.endsWith(CommonConst.BRACKET_CLOSE)) ) {
				keywords.remove(i);
				i--;
				
				removedKeywords.add(keyword);
				continue;
			}
			
			int dotIndex = keyword.indexOf(CommonConst.DOT);
			if (dotIndex != -1) {
				if (StringUtils.isNumeric(keyword.substring(0, dotIndex))) {
					keyword = keyword.substring(dotIndex+1, keyword.length());
					keywords.set(i, keyword);
				}
			}
			
			if (StringUtils.isNumeric(keyword)) {
				keywords.remove(i);
				i--;
				
				removedKeywords.add(keyword);
				continue;
			}
		}
		
		int keywordSize = 0;
		String source = "";
		String target = "";
		
		keywordSize = keywords.size();
		for (int i=keywordSize-1; i>=0; i--) {
			source = keywords.get(i);
			for (int j=i-1; j>=0; j--) {
				target = keywords.get(j);
				
				if (target.length() >= source.length()) {
					if (target.indexOf(source) > 0) {
						keywords.remove(i);
						
						removedKeywords.add(source);
						break;
					}
				}
			}
		}
		
		for (int i=0; i<keywords.size(); i++) {
			source = keywords.get(i);
			for (int j=i+1; j<keywords.size(); j++) {
				target = keywords.get(j);
				
				if (target.length() >= source.length()) {
					if (target.indexOf(source) > 0) {
						keywords.remove(i);
						i--;
						
						removedKeywords.add(source);
						break;
					}
				}
			}
		}
		
		//LOGGER.debug("The keywords <{}> will be remove from <{}>", new Object[]{removedKeywords.toArray(new String[0]), originalPath});
		
		return keywords.toArray(new String[0]);
	}
}
