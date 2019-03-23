package com.duangframework.mvc.dto.upload;


import com.duangframework.kit.PathKit;

import java.io.File;


/**
 * UploadFile.
 */
public class UploadFile {
	
	private String parameterName;				//表单名称
	private String serverDirectory;				//服务器保存路径，相对于/uploadfiles文件下
	private String fileName;					//上传保存后的文件名
	private String originalFileName;			//未上传前的文件名
	private String oiriginalName;				// 上传文件时的文件控件名称
	private String contentType;					//文件类型
	private long fileSize;						//文件大小
	private String extName;						//扩展名
	private String zipfilePath;					//缩略图文件路径

	
	public UploadFile(String parameterName, String serverDirectory, String filesystemName, String oiriginalName, String originalFileName, String contentType, long fileSize) {
		this.parameterName = parameterName;
		this.serverDirectory = serverDirectory;
		this.fileName = filesystemName;
		this.oiriginalName = oiriginalName;
		this.originalFileName = originalFileName;
		this.contentType = contentType;
		this.fileSize = fileSize;
		this.extName = fileName.substring(fileName.lastIndexOf(".") + 1);
	}
	
	public UploadFile(String parameterName, String serverDirectory, String filesystemName, String oiriginalName, String originalFileName, String contentType, long fileSize, String zipfilePath) {
		this.parameterName = parameterName;
		this.serverDirectory = serverDirectory;
		this.fileName = filesystemName;
		this.oiriginalName = oiriginalName;
		this.originalFileName = originalFileName;
		this.contentType = contentType;
		this.fileSize = fileSize;
		this.extName = fileName.substring(fileName.lastIndexOf(".") + 1);
		this.zipfilePath = zipfilePath;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getOriginalFileName() {
		return originalFileName;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public String setServerDirectory(String serverDirectory) {
		return this.serverDirectory = serverDirectory;
	}
	
	public String getServerDirectory() {
		return serverDirectory;
	}
	
	public File getFile() {
		if (serverDirectory == null || fileName == null) {
			return null;
		} else {
			String path = serverDirectory + "/" + fileName;
			return new File(path);
		}
	}
	
	public long getFileSize() {
		return fileSize;
	}

	public String getExtName() {
		return extName;
	}

	public String getServerFilePath() {
		return serverDirectory + "/" + fileName;
	}

	public String getZipfilePath() {
		return zipfilePath;
	}

	public void setZipfilePath(String zipfilePath) {
		this.zipfilePath = zipfilePath;
	}

	public String getOiriginalName() {
		return oiriginalName;
	}
}






