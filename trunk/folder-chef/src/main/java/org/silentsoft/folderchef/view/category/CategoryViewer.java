package org.silentsoft.folderchef.view.category;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.folderchef.main.FolderChef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryViewer {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryViewer.class);
	
	private Parent categoryViewer;
	
	private CategoryViewerController categoryViewerController;
	
	public CategoryViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CategoryViewer.fxml"));
			categoryViewer = fxmlLoader.load();
			categoryViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize category viewer !", e);
		}
	}
	
	public Parent getCategoryViewer() {
		return categoryViewer;
	}
	
	private void initialize() {
		categoryViewerController.initialize();
		categoryViewerController.initializeSharedMemory();
		categoryViewerController.customizeDirectoryTree();
		
		try {
			categoryViewerController.preLoad();
		} catch (Exception e) {
			MessageBox.showException(FolderChef.getStage(), e);
		}
	}
}
