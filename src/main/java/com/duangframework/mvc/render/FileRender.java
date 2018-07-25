package com.duangframework.mvc.render;

import com.duangframework.exception.MvcException;
import com.duangframework.exception.ServiceException;
import com.duangframework.mvc.dto.upload.DownLoadStream;
import com.duangframework.mvc.dto.upload.UploadFile;
import com.duangframework.utils.UploadFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;


/**
 * FileRender.
 */
public class FileRender extends Render {
	
	private static final long serialVersionUID = 4293616220202691369L;
	private File file;
	private UploadFile uploadFile;
	private DownLoadStream stream;
	private boolean isDelete;

	public FileRender(UploadFile file) {
		this(file, true);
	}
	public FileRender(UploadFile file, boolean isDelete) {
		this.uploadFile = file;
		this.file = file.getFile();
		this.isDelete = isDelete;
	}


	public FileRender(File file) {
		this(file, true);
	}
	public FileRender(File file, boolean isDelete) {
		this.file = file;
		this.isDelete = isDelete;
	}
	public FileRender(DownLoadStream stream) {
		this(stream, true);
	}
    public FileRender(DownLoadStream stream, boolean isDelete) {
        this.stream = stream;
        this.isDelete = isDelete;
        byte[] bytes = new byte[8192];
        try {
            IOUtils.write(bytes, stream.getOutputStream());
            String serverFileDir = UploadFileUtils.builderServerFileDir("");
            String serverFileName = UploadFileUtils.builderServerFileName(stream.getFileName(), false);
            File dir = new File(serverFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(serverFileDir, serverFileName);
            FileUtils.writeByteArrayToFile(file, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void render() {
		if(null == request || null == response) {
			return;
		}
		try {
			if (file == null || !file.isFile() || file.length() > Integer.MAX_VALUE) {
				throw new ServiceException("下载的文件不正确");
			}
			response.write(file);
			response.setDeleteFile(isDelete);
		} catch (Exception e) {
			throw new MvcException(e.getMessage(), e);
		}
	}
}


