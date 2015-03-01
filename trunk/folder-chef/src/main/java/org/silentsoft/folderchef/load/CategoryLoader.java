package org.silentsoft.folderchef.load;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.silentsoft.core.CommonConst;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.util.DateUtil;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryLoader extends Loader {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CategoryLoader.class);

	public CategoryLoader() {
		super();
	}

	@Override
	public void init() {
		initializeSharedMemory();
	}

	private void initializeSharedMemory() {
	}

	@Override
	public void load() {
		try {
			realLoad();
		} catch (Exception e) {
			LOGGER.error("I got catch an error !", new Object[] { e });
		}
	}

	private void realLoad() throws Exception {
		String[] extractTargets = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_TARGET)).split(CommonConst.VERTICAL_BAR);
		final String[] extractExtensions = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_EXTENSIONS)).split(CommonConst.COMMA);
		String loadDestination = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_DESTINATION));
		String loadType = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_TYPE));

		for (String extractTarget : extractTargets) {
			LOGGER.info("Start to iterate from target <{}> by <{}> to load <{}>", new Object[] { extractTarget, extractExtensions, loadDestination });

			Path targetPath = Paths.get(extractTarget);
			Files.walkFileTree(targetPath, new FileVisitor<Path>() {

				public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) throws IOException {
					if (arg0.toString().length() >= CommonConst.MAX_DIRECTORY_LENGTH) {
						LOGGER.error("The depth is to deep ! it will be skip subtree <{}>", new Object[] { arg0 });
						return FileVisitResult.SKIP_SUBTREE;
					}
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
					boolean isTargetToExtract = false;
					for (String ext : extractExtensions) {
						if (arg0.toString().toUpperCase().endsWith(ext.toUpperCase())) {
							isTargetToExtract = true;
							break;
						}
					}

					if (isTargetToExtract) {
						String fileName = arg0.getFileName().toString();
						String fullPath = arg0.toAbsolutePath().toString();
						String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf(CommonConst.DOT));
						String[] keywords = getKeywordsFromFileName(fileNameWithoutExt);

						loadingByKeywords(fullPath, fileName, keywords, loadDestination, loadType);
						
						EventHandler.callEvent(CategoryLoader.class, BizConst.EVENT_UPDATE_LOAD_PROGRESS, false);
					}

					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
					LOGGER.error("Visit file failed ! <{}>", new Object[] { arg0 });
					return FileVisitResult.CONTINUE;
				}
			});
		}
		
		SharedMemory.getDataMap().put(BizConst.KEY_LOAD_FILE_COUNT_FINISH, true);
	}

	private void loadingByKeywords(String fullPath, String fileName, String[] keywords, String loadDestination, String loadType) {
		String directory = findDirectoryByKeywords(keywords);

		if ("".equals(directory)) {
			boolean excludeNoneItem = ObjectUtil.toBoolean(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_EXCLUDE_NONE_ITEM));
			if (excludeNoneItem) {
				// if exclude option, not move to root
			} else {
				directory = loadDestination;
			}
		} else {
			String lowPath = directory.substring(directory.indexOf(File.separator));
			directory = loadDestination + lowPath;
		}

		try {
			if (!"".equals(directory)) {
				File scrFile = new File(fullPath);
				String target = directory + File.separator + fileName;
				boolean isTargetFileOverlapped = FileUtil.isExists(target);
				
				if (isTargetFileOverlapped) {
					// Rename to original source file
					File orgFile = new File (target);
					
					Path orgPath = orgFile.toPath();
					BasicFileAttributes orgAttr = Files.readAttributes(orgPath, BasicFileAttributes.class);

					String orgCreationTime = DateUtil.getDateAsStr(new Date(orgAttr.creationTime().toMillis()), DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);
					String orgLastModifiedTime = DateUtil.getDateAsStr(new Date(orgAttr.lastModifiedTime().toMillis()), DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);
					String orgLastAccessTime = DateUtil.getDateAsStr(new Date(orgAttr.lastAccessTime().toMillis()), DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);
					
					String orgSequentialPath = directory + File.separator + FileUtil.getName(fileName)
											 + CommonConst.BRACE_OPEN
											 + "C".concat(orgCreationTime)
											 + CommonConst.UNDER_BAR + "L".concat(orgLastModifiedTime)
//											 + CommonConst.UNDER_BAR + "A".concat(orgLastAccessTime)
											 + CommonConst.BRACE_CLOSE
											 + CommonConst.DOT + FileUtil.getExt(fileName);
					if (!orgFile.renameTo(new File(orgSequentialPath))) {
						LOGGER.error("Cannot rename to sequentialPath ! <{}> to <{}>", new Object[]{target, orgSequentialPath});
					}
					
					// Rename to target file
					Path tarPath = scrFile.toPath();
					BasicFileAttributes tarAttr = Files.readAttributes(tarPath, BasicFileAttributes.class);
					
					String tarCreationTime = DateUtil.getDateAsStr(new Date(tarAttr.creationTime().toMillis()), DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);
					String tarLastModifiedTime = DateUtil.getDateAsStr(new Date(tarAttr.lastModifiedTime().toMillis()), DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);
					String tarLastAccessTime = DateUtil.getDateAsStr(new Date(tarAttr.lastAccessTime().toMillis()), DateUtil.DATEFORMAT_YYYYMMDDHHMMSS);
					
					target = directory + File.separator + FileUtil.getName(fileName)
						   + CommonConst.BRACE_OPEN
						   + "C".concat(tarCreationTime)
						   + CommonConst.UNDER_BAR + "L".concat(tarLastModifiedTime)
//						   + CommonConst.UNDER_BAR + "A".concat(tarLastAccessTime)
						   + CommonConst.BRACE_CLOSE
						   + CommonConst.DOT + FileUtil.getExt(fileName);
				}
				
				// Copy or Move the file
				switch (loadType) {
					case BizConst.TYPE_LOAD_COPY: {
						FileUtils.copyFile(scrFile, new File(target));
						break;
					}
					case BizConst.TYPE_LOAD_MOVE: {
						FileUtils.moveFile(scrFile, new File(target));
						break;
					}
				}
				
				int fileCount = ObjectUtil.toInt(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_FILE_COUNT_REAL));
				SharedMemory.getDataMap().put(BizConst.KEY_LOAD_FILE_COUNT_REAL, ++fileCount);
			}
		} catch (Exception e) {
			LOGGER.error("I got catch an error !", e);
		}
	}

	private String findDirectoryByKeywords(String[] keywords) {
		String[] keywordsInMap = null;

		// int matchingCount = 0;
		// int maxMatchingCount = 0;
		float maxMatchingRate = 0;
		float currMatchingRate = 0;
		String directory = "";

		Set<Entry<String, Object>> entrySet = SharedMemory.getTransformerMap().entrySet();
		for (Map.Entry<String, Object> entry : entrySet) {
			keywordsInMap = entry.getKey().split(CommonConst.VERTICAL_BAR);

			// matchingCount = getMatchingCount(keywords, keywordsInMap);
			// if (maxMatchingCount < matchingCount) {
			// directory = ObjectUtil.toString(entry.getValue());
			// }
			currMatchingRate = getMatchingRate(keywords, keywordsInMap);
			// LOGGER.debug("Keywords : <{}>, Map : <{}>, curr/max : <{}>/<{}>",
			// new Object[]{keywords, keywordsInMap, currMatchingRate,
			// maxMatchingRate});
			if (currMatchingRate > maxMatchingRate) {
				maxMatchingRate = currMatchingRate;
				directory = ObjectUtil.toString(entry.getValue());
			}
		}

		return directory;
	}

	private float getMatchingRate(String[] source, String[] target) {
		float matchingRate = 0;
		int matchingCount = 0;

		int sourceLength = source.length; // from file
		int targetLength = target.length; // from keyword set
		if (sourceLength > 0 && targetLength > 0) {
			for (int i = 0; i < targetLength; i++) {
				for (int j = 0; j < sourceLength; j++) {
					if (target[i].equals(source[j])) {
						matchingCount++;
					}
				}
			}
		}

		// if (matchingCount > 0 && sourceLength > 0){
		// matchingRate = (100/sourceLength) * matchingCount;
		// }
		// if (matchingCount > 0 && targetLength > 0){
		// if file names array are higher, then rate is lower.
		// so, do not get the rate from file names arry. get the rate from
		// keyword set.
		// matchingRate = (100/targetLength) * matchingCount;
		// but.. if keyword set array are lower, then rate is near the 100... ;;
		// }
		if (matchingCount > 0 && sourceLength > 0 && targetLength > 0) {
			// so i ready this. make it from all of information.
			float sourceCoverage = new Float(100.0f / sourceLength);
			float targetCoverage = new Float(matchingCount / targetLength)
					+ matchingCount;

			matchingRate = (float) (sourceCoverage * targetCoverage);
		}

		return matchingRate;
	}

	/**
	 * get keywords from filename.
	 * 
	 * @param fileNameWithoutExt
	 * @return
	 */
	private String[] getKeywordsFromFileName(String fileNameWithoutExt) {
		String originalPath = fileNameWithoutExt;
		ArrayList<String> removedKeywords = new ArrayList<String>();
		
		fileNameWithoutExt = fileNameWithoutExt.replace(File.separatorChar, CommonConst.VERTICAL_BAR_CHAR);
		fileNameWithoutExt = fileNameWithoutExt.replace(CommonConst.UNDER_BAR_CHAR, CommonConst.VERTICAL_BAR_CHAR);
		fileNameWithoutExt = fileNameWithoutExt.replace(CommonConst.SPACE_BAR_CHAR, CommonConst.VERTICAL_BAR_CHAR);
		fileNameWithoutExt = fileNameWithoutExt.replace(CommonConst.COMMA_CHAR, CommonConst.VERTICAL_BAR_CHAR);
		
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
			
			int bracketOpenIndex = keyword.indexOf(CommonConst.BRACKET_OPEN);
			int bracketCloseIndex = keyword.indexOf(CommonConst.BRACKET_CLOSE);
			
			if (bracketOpenIndex != -1 && bracketCloseIndex == -1) {
				keyword = keyword.replace(CommonConst.BRACKET_OPEN_CHAR, CommonConst.VERTICAL_BAR_CHAR);
				String[] temp = keyword.split(CommonConst.VERTICAL_BAR);
				
				if (temp.length > 0) {
					keywords.set(i, temp[0]);
					
					if (temp.length > 1) {
						keywords.add(i+1, temp[1]);
					}
					
					i--;
					continue;
				}
			}
			
			
			if (bracketOpenIndex == -1 && bracketCloseIndex != -1) {
				keyword = keyword.replace(CommonConst.BRACKET_CLOSE_CHAR, CommonConst.VERTICAL_BAR_CHAR);
				String[] temp = keyword.split(CommonConst.VERTICAL_BAR);
				
				if (temp.length > 0) {
					keywords.set(i, temp[0]);
					
					if (temp.length > 1) {
						keywords.add(i+1, temp[1]);
					}
	
					i--;
					continue;
				}
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
