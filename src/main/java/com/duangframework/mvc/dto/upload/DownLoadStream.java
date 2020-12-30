package com.duangframework.mvc.dto.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

/**
 * DownLoadStream.
 */
public class DownLoadStream {

	private String fileName;
	private OutputStream outputStream = new ByteArrayOutputStream();

	public DownLoadStream() {
	}

	public DownLoadStream(File file, String fileName, OutputStream outputStream) {
		this.fileName = fileName;
		this.outputStream = outputStream;
	}

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
}






