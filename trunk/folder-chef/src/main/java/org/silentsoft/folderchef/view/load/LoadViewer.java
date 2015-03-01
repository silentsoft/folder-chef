package org.silentsoft.folderchef.view.load;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadViewer {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoadViewer.class);
	
	private Parent loadViewer;
	
	private LoadViewerController loadViewerController;
	
	public LoadViewer(){
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoadViewer.fxml"));
			loadViewer = fxmlLoader.load();
			loadViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize load viewer !", e);
		}
	}
	
	public Parent getLoadViewer() {
		return loadViewer;
	}
	
	private void initialize() {
		loadViewerController.initialize();
		loadViewerController.initializeSharedMemory();
		
		loadViewerController.loadExecute();
	}
	
	
}
