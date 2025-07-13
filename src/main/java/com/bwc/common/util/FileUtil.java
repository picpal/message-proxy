package com.bwc.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 프로젝트 전반에 사용되는 File Util Class
 *
 * @ClassName FileUtil.java
 * @Description FileUtil Class
 * @Modification-Information
 * <pre>
 *    수정일       수정자              수정내용
 *  ----------   ----------   -------------------------------
 *  2020. 7. 1.   lucifer      최초생성
 * </pre>
 * @author lucifer
 * @since 2020. 7. 1.
 * @version 1.0
 * @see
 * <pre>
 *  Copyright (C) 2020 by Taihoinst CO.,LTD. All right reserved.
 * </pre>
 */
@Slf4j
public final class FileUtil {

	private FileUtil() {

	}

	/**
	 * <pre>
	 * List에 담겨있는 내용을 파일로 저장
	 * </pre>
	 *
	 * @param saveFileName
	 * @param data
	 * @throws IOException
	 */
	public static void writeFile(
		String saveFileName,
		List<String> data) throws IOException {
		File file = new File(saveFileName);

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");

		BufferedWriter bufferWriter = new BufferedWriter(outputStreamWriter);
		try {
			for (int i = 0; i < data.size(); i++) {
				bufferWriter.write(data.get(i));
				bufferWriter.newLine();
			}
			bufferWriter.flush(); // bufferedWriter할때는 꼭 flush해야함..
		} catch (IOException e) {
			log.error("IOException !!");
		} finally {
			if (fileOutputStream != null)
				fileOutputStream.close();
			if (outputStreamWriter != null)
				outputStreamWriter.close();
			if (bufferWriter != null)
				bufferWriter.close();
		}
	}

	/**
	 * <pre>
	 * 문자열을 파일로 저장
	 * </pre>
	 *
	 * @param saveFileName
	 * @param data
	 * @throws IOException
	 */
	public static void writeFile(
		String saveFileName,
		String data) throws IOException {
		File file = new File(saveFileName);

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");

		BufferedWriter bufferWriter = new BufferedWriter(outputStreamWriter);
		try {
			bufferWriter.write(data);
			bufferWriter.newLine();
			bufferWriter.flush(); // bufferedWriter할때는 꼭 flush해야함..
		} catch (IOException e) {
			log.error("IOException !!!");
		} finally {
			if (fileOutputStream != null)
				fileOutputStream.close();
			if (outputStreamWriter != null)
				outputStreamWriter.close();
			if (bufferWriter != null)
				bufferWriter.close();
		}
	}

	/**
	 * 폴더가 없으면 폴더 생성
	 *
	 * @param filePath
	 */
	public static void makeFolder(
		String filePath) {
		File dirPath = new File(filePath);
		if (!dirPath.exists()) {
			dirPath.mkdir();
		}
	}

	/**
	 * 폴더가 있으면 폴더 삭제
	 *
	 * @param filePath
	 */
	public static void deleteFoler(
		String filePath) {
		File dirPath = new File(filePath);
		if (dirPath.exists()) {
			dirPath.delete();
		}
	}

	/**
	 * 파일이 있는지 여부 체크
	 *
	 * @param filePath
	 */
	public static boolean isExistsFile(
		String filePath) {
		boolean exists = false;
		File dirPath = new File(filePath);
		if (dirPath.exists()) {
			exists = true;
		}
		return exists;
	}

	/**
	 * 파일이 있으면 파일 삭제
	 *
	 * @param filePath
	 */
	public static void deleteFile(
		String filePath) {
		File dirPath = new File(filePath);
		if (dirPath.exists()) {
			dirPath.delete();
		}
	}

	/**
	 * 폴더가 없으면 순차적 폴더 생성
	 *
	 * @param filePath
	 */
	public static void makeFolders(
		String filePath) {
		File dirPath = new File(filePath);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
	}

	/**
	 * 파일크기 구하기
	 *
	 * @param fullFilePath
	 * @return
	 */
	public static long getFileSize(String fullFilePath) {
		File file = new File(fullFilePath);
		long fileSize = 0;
		if (file.exists()) {
			fileSize = file.length();
		} else {
			log.error("File not exists!!");
		}
		return fileSize;
	}

}
