package org.silentsoft.folderchef.view.option;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import javax.swing.JFileChooser;

import org.apache.commons.lang.StringUtils;
import org.controlsfx.dialog.Dialogs;
import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.INIUtil;
import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;
import org.silentsoft.folderchef.main.FolderChef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptionViewerController implements Initializable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OptionViewerController.class);
	
	private ObservableList<String> oListLoadType = 
		    FXCollections.observableArrayList(
		        BizConst.TYPE_LOAD_COPY,
		        BizConst.TYPE_LOAD_MOVE
		    );
	
	@FXML
	private TextField txtExtractTarget;
	
	@FXML
	private TextField txtExtractExtensions;
	
	@FXML
	private ComboBox<String> cmbLoadType;
	
	@FXML
	private TextField txtLoadDestination;
	
	@FXML
	private Button btnStart;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cmbLoadType.setItems(oListLoadType);
		cmbLoadType.setValue(BizConst.TYPE_LOAD_COPY);
	}
	
	@FXML
	private void txtExtractTarget_OnMouseReleased() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int returnValue = chooser.showOpenDialog(chooser);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			String target = "";
			
			File[] selectedFiles = chooser.getSelectedFiles();
			for (File file : selectedFiles) {
				if (target.length() > 0) {
					target += CommonConst.VERTICAL_BAR_CHAR + file.getAbsolutePath();
				} else {
					target = file.getAbsolutePath();
				}
			}
			
			txtExtractTarget.setText(target);
		}
	}
	
	@FXML
	private void txtExtractTarget_OnMouseEntered() {
		txtExtractTarget.setTooltip(new Tooltip(txtExtractTarget.getText()));
	}
	
	@FXML
	private void txtLoadDestination_OnMouseReleased() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int returnValue = chooser.showOpenDialog(chooser);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			String target = chooser.getSelectedFile().getAbsolutePath();
			txtLoadDestination.setText(target);
		}
	}
	
	@FXML
	private void txtLoadDestination_OnMouseEntered() {
		txtLoadDestination.setTooltip(new Tooltip(txtLoadDestination.getText()));
	}
	
	@FXML
	private void btnStart_OnMouseReleased() {
		if (isValidated()) {
			EventHandler.callEvent(OptionViewer.class, BizConst.EVENT_VIEW_INFINITY, false);
			
			saveOptionToINI();
			saveOptionToSharedMemory();
			
			EventHandler.callEvent(OptionViewer.class, BizConst.EVENT_EXTRACT_EXECUTE);
			//optionViewer.dispose();
		} else {
			MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_MESSAGE)));
		}
	}
	
	private boolean isValidated() {
		
		if (StringUtils.isEmpty(txtExtractTarget.getText())) {
			SharedMemory.getDataMap().put(BizConst.KEY_MESSAGE, "Extract target cannot be null !");
			return false;
		}
		
		if (StringUtils.isEmpty(txtExtractExtensions.getText())) {
			SharedMemory.getDataMap().put(BizConst.KEY_MESSAGE, "Extract extensions cannot be null !");
			return false;
		}
		
		if (cmbLoadType.getValue().equals(BizConst.TYPE_LOAD_MOVE)) {
			SharedMemory.getDataMap().put(BizConst.KEY_MESSAGE, "Beta version cannot use MOVE option !");
			return false;
		}
		
		if (StringUtils.isEmpty(txtLoadDestination.getText())) {
			SharedMemory.getDataMap().put(BizConst.KEY_MESSAGE, "Load destination cannot be null !");
			return false;
		}
		
		if (txtExtractTarget.getText().equals(txtLoadDestination.getText())) {
			SharedMemory.getDataMap().put(BizConst.KEY_MESSAGE, "Target and Destination cannot be same !");
			return false;
		}
		
		return true;
	}
	
	public void loadConfigurationFromINI() {
		INIUtil configINI = new INIUtil(System.getProperty("user.dir") + BizConst.PATH_CONFIG);
		if (configINI.isExists()) {
			try {
				String extractTarget = configINI.getData(BizConst.INI_SECTION_EXTRACT, BizConst.INI_EXTRACT_TARGET);
				if (StringUtils.isNotEmpty(extractTarget)) {
					SharedMemory.getDataMap().put(BizConst.KEY_EXTRACT_TARGET, extractTarget);
					txtExtractTarget.setText(extractTarget);
				}
				
				String extractExtensions = configINI.getData(BizConst.INI_SECTION_EXTRACT, BizConst.INI_EXTRACT_EXTENSIONS);
				if (StringUtils.isNotEmpty(extractExtensions)) {
					SharedMemory.getDataMap().put(BizConst.KEY_EXTRACT_EXTENSIONS, extractExtensions);
					txtExtractExtensions.setText(extractExtensions);
				}
				
				String loadType = configINI.getData(BizConst.INI_SECTION_LOAD, BizConst.INI_LOAD_TYPE);
				if (StringUtils.isNotEmpty(loadType)) {
					SharedMemory.getDataMap().put(BizConst.KEY_LOAD_TYPE, loadType);
					cmbLoadType.setValue(loadType);
				}
				
				String loadDestination = configINI.getData(BizConst.INI_SECTION_LOAD, BizConst.INI_LOAD_DESTINATION);
				if (StringUtils.isNotEmpty(loadDestination)) {
					SharedMemory.getDataMap().put(BizConst.KEY_LOAD_DESTINATION, loadDestination);
					txtLoadDestination.setText(loadDestination);
				}
			} catch (Exception e) {
				LOGGER.error("I got catch an exception !", e);
			}
		} else {
			LOGGER.error("Configuration file is not exists ! <{}>", new Object[]{System.getProperty("user.dir") + BizConst.PATH_CONFIG});
			
			try {
				LOGGER.info("try to create configuration file");
				
				if (!Files.exists(Paths.get(System.getProperty("user.dir") + BizConst.PATH_CONF_DIRECTORY))) {
					Files.createDirectory(Paths.get(System.getProperty("user.dir") + BizConst.PATH_CONF_DIRECTORY));
				}
				
				if (!Files.exists(Paths.get(System.getProperty("user.dir") + BizConst.PATH_CONFIG))) {
					Files.createFile(Paths.get(System.getProperty("user.dir") + BizConst.PATH_CONFIG));
				}
			} catch (Exception e) {
				LOGGER.error("I cannot making config !", e);
			}
		}
	}
	
	public void loadConfigurationFromSharedMemory() {
		String extractTarget = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_TARGET));
		String extractExtensions = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_EXTENSIONS));
		String loadDestination = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_DESTINATION));
		String loadType = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_TYPE));
		
		txtExtractTarget.setText(extractTarget);
		txtExtractExtensions.setText(extractExtensions);
		txtLoadDestination.setText(loadDestination);
		cmbLoadType.setValue(loadType);
	}
	
	private void saveOptionToINI() {
		INIUtil configINI = new INIUtil(System.getProperty("user.dir") + BizConst.PATH_CONFIG);
		if (!configINI.isExists()) {
			LOGGER.error("Configuration file is not exists ! <{}>", new Object[]{System.getProperty("user.dir") + BizConst.PATH_CONFIG});
			
			try {
				LOGGER.info("try to create configuration file");
				
				if (!Files.exists(Paths.get(System.getProperty("user.dir") + BizConst.PATH_CONF_DIRECTORY))) {
					Files.createDirectory(Paths.get(System.getProperty("user.dir") + BizConst.PATH_CONF_DIRECTORY));
				}
				
				if (!Files.exists(Paths.get(System.getProperty("user.dir") + BizConst.PATH_CONFIG))) {
					Files.createFile(Paths.get(System.getProperty("user.dir") + BizConst.PATH_CONFIG));
				}
			} catch (Exception e) {
				LOGGER.error("I cannot making config !", e);
			}
		}
		
		try {
			configINI.setData(BizConst.INI_SECTION_EXTRACT, BizConst.INI_EXTRACT_TARGET, txtExtractTarget.getText());
			configINI.setData(BizConst.INI_SECTION_EXTRACT, BizConst.INI_EXTRACT_EXTENSIONS, txtExtractExtensions.getText());
			configINI.setData(BizConst.INI_SECTION_LOAD, BizConst.INI_LOAD_TYPE, cmbLoadType.getValue());
			configINI.setData(BizConst.INI_SECTION_LOAD, BizConst.INI_LOAD_DESTINATION, txtLoadDestination.getText());
		} catch (Exception e) {
			LOGGER.error("I got catch an exception !", e);
		}
	}
	
	private void saveOptionToSharedMemory() {
		String extractTarget = txtExtractTarget.getText();
		String extractExtensions = txtExtractExtensions.getText();
		String loadDestination = txtLoadDestination.getText();
		String loadType = cmbLoadType.getValue();
		
		SharedMemory.getDataMap().put(BizConst.KEY_EXTRACT_TARGET, extractTarget);
		SharedMemory.getDataMap().put(BizConst.KEY_EXTRACT_EXTENSIONS, extractExtensions);
		SharedMemory.getDataMap().put(BizConst.KEY_LOAD_DESTINATION, loadDestination);
		SharedMemory.getDataMap().put(BizConst.KEY_LOAD_TYPE, loadType);
	}
}
