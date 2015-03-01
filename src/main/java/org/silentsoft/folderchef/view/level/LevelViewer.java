package org.silentsoft.folderchef.view.level;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.silentsoft.folderchef.view.level.LevelViewerController.SortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelViewer {

	private static final Logger LOGGER = LoggerFactory.getLogger(LevelViewer.class);
	
	private Parent levelViewer;
	
	private LevelViewerController levelViewerController;
	
	public LevelViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LevelViewer.fxml"));
			levelViewer = fxmlLoader.load();
			levelViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize level viewer !", e);
		}
	}
	
	public Parent getLevelViewer() {
		return levelViewer;
	}
	
	private void initialize() {
		levelViewerController.initialize();
		levelViewerController.initializeSharedMemory();
		levelViewerController.customizeDirectoryTree();
		levelViewerController.displayKeywordToList(SortOption.ByCount);
	}
}