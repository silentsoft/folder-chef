package org.silentsoft.folderchef.view.infinity;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfinityViewer {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfinityViewer.class);
	
	private Parent infinityViewer;

	public InfinityViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfinityViewer.fxml"));
			infinityViewer = fxmlLoader.load();
			
		} catch (Exception e) {
			LOGGER.error("Failed initialize infinity viewer !", e);
		}
	}

	public Parent getInfinityViewer() {
		return infinityViewer;
	}
}