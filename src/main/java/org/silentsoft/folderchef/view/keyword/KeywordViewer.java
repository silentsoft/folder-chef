package org.silentsoft.folderchef.view.keyword;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeywordViewer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KeywordViewer.class);
	
	private Parent keywordViewer;
	
	private KeywordViewerController keywordViewerController;
	
	public KeywordViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("KeywordViewer.fxml"));
			keywordViewer = fxmlLoader.load();
			keywordViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize keyword viewer !", e);
		}
	}
	
	public Parent getKeywordViewer() {
		return keywordViewer;
	}
	
	private void initialize() {
		keywordViewerController.settingDataPropertiesToColumns();
		keywordViewerController.displayKeywordToGrid();
	}
}