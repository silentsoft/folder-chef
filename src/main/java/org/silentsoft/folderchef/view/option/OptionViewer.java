package org.silentsoft.folderchef.view.option;
	
import org.silentsoft.folderchef.core.SharedMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class OptionViewer {
	public enum ConfigLoadOption {
		FromINI,
		FromSharedMemory
	};
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OptionViewer.class);
	
	private Parent optionViewer;
	
	private OptionViewerController optionViewerController;
	
	public OptionViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OptionViewer.fxml"));
			optionViewer = fxmlLoader.load();
			optionViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize option viewer !", e);
		}
	}
	
	public Parent getOptionViewer() {
		return optionViewer;
	}
	
	private void initialize() {
		ConfigLoadOption configLoadOption = (ConfigLoadOption) SharedMemory.getDataMap().get("con");
		switch (configLoadOption) {
		case FromINI:
			optionViewerController.loadConfigurationFromINI();
			break;
		case FromSharedMemory:
			optionViewerController.loadConfigurationFromSharedMemory();
			break;
		default:
			LOGGER.error("Cannot load config option <{}>", new Object[]{configLoadOption});
			break;
		}
	}
}
