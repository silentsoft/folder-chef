package org.silentsoft.folderchef.extract;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.folderchef.component.model.Relation;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;
import org.silentsoft.io.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeywordExtractor extends Extractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(KeywordExtractor.class);

	public KeywordExtractor() {
		super();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		SharedMemory.getDataMap().put(BizConst.KEY_EXTRACT_FILE_COUNT, 0);
		SharedMemory.getDataMap().put(BizConst.KEY_RELATION, new Relation());
		SharedMemory.getDataMap().put(BizConst.KEY_RELATION_SET, new HashSet<String[]>());
	}

	@Override
	public void extract() {
		long startTime = System.currentTimeMillis();
		
		try {
			extractKeywordExcludeDirectory();
			makeKeywordSet();
			EventHandler.callEvent(KeywordExtractor.class, BizConst.EVENT_VIEW_KEYWORD, false);
		} catch (Exception e) {
			LOGGER.error("I got catch an Exception !", e);
		}
		
		long endTime = System.currentTimeMillis();
		
		LOGGER.info("Succeed to extract keywords <{}>ms", new Object[]{(endTime-startTime)});
	}
	
	private void extractKeywordExcludeDirectory() throws Exception {
		String[] extractTargets = SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_TARGET).toString().split(CommonConst.VERTICAL_BAR);
		final String[] extractExtensions = SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_EXTENSIONS).toString().split(CommonConst.COMMA);
		
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
						int fileCount = ObjectUtil.toInt(SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_FILE_COUNT));
						SharedMemory.getDataMap().put(BizConst.KEY_EXTRACT_FILE_COUNT, ++fileCount);
						//LOGGER.debug("Iterating for <{}> time.", new Object[]{fileCount});
						
						String fileName = arg0.getFileName().toString();
						String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf(CommonConst.DOT));
						String[] keywords = getKeywordsFromFileName(fileNameWithoutExt);
						Arrays.sort(keywords);
						
						Relation relation = (Relation)SharedMemory.getDataMap().get(BizConst.KEY_RELATION);
						for (String keyword : keywords) {
							int keyCount = ObjectUtil.toInt(SharedMemory.getExtractorMap().get(keyword));
							SharedMemory.getExtractorMap().put(keyword, ++keyCount);
							
							for (String other : keywords) {
								if (!other.equals(keyword)) {
									relation.setRelation(keyword, other);
								}
							}
						}
						SharedMemory.getDataMap().put(BizConst.KEY_RELATION, relation);
						
						((HashSet<String[]>)SharedMemory.getDataMap().get(BizConst.KEY_RELATION_SET)).add(keywords);
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
	
	/**
	 * Make seyword set from SharedMemory to .dat file.
	 * @throws Exception
	 */
	private void makeKeywordSet() throws Exception {
		try {
			List<String> keywordSet = new ArrayList<String>();
			
			Set<Entry<String, Object>> entrySet = SharedMemory.getExtractorMap().entrySet();
			for (Map.Entry<String, Object> entry : entrySet) {
				keywordSet.add(entry.getKey() + CommonConst.VERTICAL_BAR_CHAR + entry.getValue());
			}
			
			if (!Files.exists(Paths.get(System.getProperty("user.dir") + BizConst.PATH_CONF_DIRECTORY))) {
				Files.createDirectory(Paths.get(System.getProperty("user.dir") + BizConst.PATH_CONF_DIRECTORY));
			}
			
			if (!Files.exists(Paths.get(System.getProperty("user.dir") + BizConst.PATH_KEYWORD_SET))) {
				Files.createFile(Paths.get(System.getProperty("user.dir") + BizConst.PATH_KEYWORD_SET));
			}
			
			Files.write(Paths.get(System.getProperty("user.dir") + BizConst.PATH_KEYWORD_SET), keywordSet, StandardCharsets.UTF_8);
		} catch (Exception e) {
			LOGGER.error("I cannot making keywords !", e);
		}
	}
	
	/**
	 * Load keyword set from .dat file to SharedMemory.
	 * @throws Exception
	 */
	private void loadKeywordSet() throws Exception {
		try {
			Path keywordSetPath = Paths.get(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_EXTRACT_KEYSET)));
			
			List<String> listKeywordSet = new ArrayList<String>();
			listKeywordSet = Files.readAllLines(keywordSetPath, StandardCharsets.UTF_8);
			
			String[] keywords = null;
			String keyword = null;
			String count = null;
			for (String keywordSet : listKeywordSet) {
				keywords = keywordSet.split(CommonConst.VERTICAL_BAR);
				keyword = keywords[BizConst.IDX_KEYSET_KEYWORD];
				count = keywords[BizConst.IDX_KEYSET_COUNT];
				
				SharedMemory.getExtractorMap().put(keyword, Integer.parseInt(count));
			}
			
		} catch (Exception e) {
			LOGGER.error("I cannot reading keywords !", e);
		}
	}
	
//	/**
//	 * get keywords from filename.
//	 * @param fullPathWithoutExt
//	 * @return
//	 */
//	private String[] getKeywordsFromFullPath(String fullPathWithoutExt) {
//		String originalPath = fullPathWithoutExt;
//		ArrayList<String> removedKeywords = new ArrayList<String>();
//	
//		fullPathWithoutExt = fullPathWithoutExt.replace(File.separatorChar, CommonConst.VERTICAL_BAR_CHAR);
//		fullPathWithoutExt = fullPathWithoutExt.replace(CommonConst.UNDER_BAR_CHAR, CommonConst.VERTICAL_BAR_CHAR);
//		fullPathWithoutExt = fullPathWithoutExt.replace(CommonConst.SPACE_BAR_CHAR, CommonConst.VERTICAL_BAR_CHAR);
//		
//		LinkedList<String> keywords = new LinkedList<String>(Arrays.asList(fullPathWithoutExt.split(CommonConst.VERTICAL_BAR)));
//		keywords.remove(0);
//		
//		String keyword = "";
//		for (int i=0; i<keywords.size(); i++) {
//			keyword = keywords.get(i);
//			
//			if ( (keyword.endsWith(CommonConst.DOT)) ||
//				 (keyword.startsWith(CommonConst.BRACKET_OPEN) && keyword.endsWith(CommonConst.BRACKET_CLOSE)) ) {
//				keywords.remove(i);
//				i--;
//				
//				removedKeywords.add(keyword);
//				continue;
//			}
//			
//			int dotIndex = keyword.indexOf(CommonConst.DOT);
//			if (dotIndex != -1) {
//				if (StringUtils.isNumeric(keyword.substring(0, dotIndex))) {
//					keyword = keyword.substring(dotIndex+1, keyword.length());
//					keywords.set(i, keyword);
//				}
//			}
//			
//			if (StringUtils.isNumeric(keyword)) {
//				keywords.remove(i);
//				i--;
//				
//				removedKeywords.add(keyword);
//				continue;
//			}
//		}
//		
//		int keywordSize = 0;
//		String source = "";
//		String target = "";
//		
//		keywordSize = keywords.size();
//		for (int i=keywordSize-1; i>=0; i--) {
//			source = keywords.get(i);
//			for (int j=i-1; j>=0; j--) {
//				target = keywords.get(j);
//				
//				if (target.length() >= source.length()) {
//					if (target.indexOf(source) > 0) {
//						keywords.remove(i);
//						
//						removedKeywords.add(source);
//						break;
//					}
//				}
//			}
//		}
//		
//		for (int i=0; i<keywords.size(); i++) {
//			source = keywords.get(i);
//			for (int j=i+1; j<keywords.size(); j++) {
//				target = keywords.get(j);
//				
//				if (target.length() >= source.length()) {
//					if (target.indexOf(source) > 0) {
//						keywords.remove(i);
//						i--;
//						
//						removedKeywords.add(source);
//						break;
//					}
//				}
//			}
//		}
//		
//		//LOGGER.debug("The keywords <{}> will be remove from <{}>", new Object[]{removedKeywords.toArray(new String[0]), originalPath});
//		
//		return keywords.toArray(new String[0]);
//	}
	
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
