package org.silentsoft.folderchef.view.search;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchResultViewer extends Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchResultViewer.class);
	
	private Stage stage;
	private SearchResultViewerController searchResultViewerController;
	
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		
		stage.setTitle("Search Result");
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SearchResultViewer.fxml"));
			stage.setScene(new Scene(fxmlLoader.load(), 280, 430));
			searchResultViewerController = fxmlLoader.getController();
			
			initialize();
			
			stage.show();
		} catch (Exception e) {
			LOGGER.error("Failed initialize search result viewer !", e);
		}
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		stage.close();
	}
	
	private void initialize() {
		searchResultViewerController.customizeKeywordTree();
	}
}