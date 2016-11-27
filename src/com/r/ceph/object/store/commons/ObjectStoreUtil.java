package com.r.ceph.object.store.commons;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.r.ceph.object.store.constants.ObjectStoreConstants;
import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesException;
import com.rackspacecloud.client.cloudfiles.FilesObject;

/**
 * Ceph Object Store implementation.
 * 
 * This class is uses to perform upload/download/copy/delete file objects in Ceph object store.
 * 
 * @author Ranjith M
 * @since 1.0
 */
public class ObjectStoreUtil implements IObjectStoreUtil
{
	private static Log log = LogFactory.getLog(ObjectStoreUtil.class);

	private static final int numRetries = 5;

	private FilesClient client = null;

	public ObjectStoreUtil() throws Exception {
		try {
			init();
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * method to initialize object store
	 * 
	 * @throws Exception
	 */
	private void init() throws Exception {
		ObjectStorePropertyReader reader = ObjectStorePropertyReader.getInstance();

		int timeout = Integer.parseInt(reader.getProperty(ObjectStoreConstants.CONNECTION_TIMEOUT, ObjectStoreConstants.DEFAULT_CONNECTION_TIMEOUT));
		client = new FilesClient(reader.getProperty(ObjectStoreConstants.USER_NAME), reader.getProperty(ObjectStoreConstants.PASSWORD), reader.getProperty(ObjectStoreConstants.AUTH_URL), reader.getProperty(ObjectStoreConstants.ACCOUNT), (timeout > 0 ? timeout : 5000));

		if (client == null || !client.login()) {
			throw new RuntimeException("Failed to log-in");
		}
	}

	@Override
	public void createContainer(String container) throws Exception {
		try {
			if (!client.containerExists(container)) {
				client.createContainer(container);
			}
		} catch (Exception ex) {
			log.error("Exception occured while creating new container", ex);
			throw ex;
		}
	}

	@Override
	public List<?> listContainers() throws Exception {
		try {
			return client.listContainers();
		} catch (Exception ex) {
			log.error("Exception occured while reteriving containers info", ex);
			throw ex;
		}
	}

	@Override
	public void deleteContainer(String container) throws Exception {
		try {
			if (client.containerExists(container)) {
				client.deleteContainer(container);
			}
		} catch (Exception ex) {
			log.error("Exception occured while deleting container", ex);
			throw ex;
		}
	}

	@Override
	public void uploadFile(String container, String uploadTo, File input) throws Exception {
		int tries = 0;
		boolean sucess = false;
		do {
			tries++;
			try {
				if (!uploadTo.endsWith(input.getName())) {
					uploadTo = uploadTo.endsWith(ObjectStoreConstants.FILE_PATH_SEPERATOR) ? uploadTo : uploadTo.concat(ObjectStoreConstants.FILE_PATH_SEPERATOR);
					uploadTo = uploadTo.concat(input.getName());
				}
				client.storeObjectAs(container, input, "application/x-Stacksync", uploadTo);

				sucess = true;
			} catch (FilesException ex) {
				log.error("Error occured while uploading file to object store, retrying .." + tries);
				if (tries == numRetries) {
					throw ex;
				}
			}
		} while (!sucess && tries <= numRetries);
	}

	@Override
	public void copyFile(String copyFromContainer, String copyFrom, String copyToContainer, String copyTo) throws Exception {
		int tries = 0;
		boolean sucess = false;
		do {
			tries++;
			try {
				if (!copyTo.endsWith(FilenameUtils.getName(copyFrom))) {
					copyTo = copyTo.endsWith(ObjectStoreConstants.FILE_PATH_SEPERATOR) ? copyTo : copyTo.concat(ObjectStoreConstants.FILE_PATH_SEPERATOR);
					copyTo = copyTo.concat(FilenameUtils.getName(copyFrom));
				}
				client.copyObject(copyFromContainer, copyFrom, copyToContainer, copyTo);

				sucess = true;
			} catch (FilesException ex) {
				log.error("Error occured while copying file in object store, retrying .." + tries);
				if (tries == numRetries) {
					throw ex;
				}
			} catch (Exception ex) {
				// suppressed
			}
		} while (!sucess && tries <= numRetries);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void downloadFiles(String container, String downloadFrom, String downloadTo) throws Exception {
		int tries = 0;
		boolean sucess = false;
		do {
			tries++;
			try {
				if (StringUtils.isNotBlank(FilenameUtils.getName(downloadFrom))) {
					// download single file
					byte[] input = client.getObject(container, downloadFrom);
					makeFile(downloadFrom, downloadTo, input);
				} else {
					// download list of files from directory
					List<FilesObject> files = (List<FilesObject>) listFiles(container, downloadFrom);
					downloadTo = downloadTo.endsWith(ObjectStoreConstants.FILE_PATH_SEPERATOR) ? downloadTo : downloadTo.concat(ObjectStoreConstants.FILE_PATH_SEPERATOR);

					for (FilesObject file : files) {
						String downloadDir = downloadTo.concat(FilenameUtils.getPath(file.getName()));
						makeFile(file.getName(), downloadDir, file.getObject());
					}
				}

				sucess = true;
			} catch (Exception ex) {
				log.error("Error occured while downloading files from object store, retrying .." + tries);
				if (tries == numRetries) {
					throw ex;
				}
			}
		} while (!sucess && tries <= numRetries);
	}

	@Override
	public List<?> listFiles(String container, String path) throws Exception {
		int tries = 0;
		boolean sucess = false;
		List<?> files = null;
		do {
			tries++;
			try {
				files = StringUtils.isBlank(path) ? client.listObjects(container) : client.listObjects(container, path);
				sucess = true;
			} catch (FilesException ex) {
				log.error("Error occured while reteriving files info from object store, retrying .." + tries);
				if (tries == numRetries) {
					throw ex;
				}
			}
		} while (!sucess && tries <= numRetries);

		return files;
	}

	@Override
	public void deleteFile(String container, String key) throws Exception {
		int tries = 0;
		boolean sucess = false;
		do {
			tries++;
			try {
				client.deleteObject(container, key);

				sucess = true;
			} catch (FilesException ex) {
				log.error("Error occured while deleting file in object store, retrying .." + tries);
				if (tries == numRetries) {
					throw ex;
				}
			}
		} while (!sucess && tries <= numRetries);
	}

	/**
	 * method to make file object
	 * 
	 * @param downloadFrom
	 * @param downloadTo
	 * @param input
	 * @throws Exception
	 */
	public void makeFile(String downloadFrom, String downloadTo, byte[] input) throws Exception {
		downloadTo = (downloadTo.endsWith(FilenameUtils.getName(downloadFrom))) ? downloadTo.replace(FilenameUtils.getName(downloadFrom), "") : downloadTo;

		if (!new File(downloadTo).exists()) {
			new File(downloadTo).mkdirs();
		}
		downloadTo = downloadTo.endsWith(ObjectStoreConstants.FILE_PATH_SEPERATOR) ? downloadTo.concat(FilenameUtils.getName(downloadFrom)) : downloadTo.concat(ObjectStoreConstants.FILE_PATH_SEPERATOR).concat(FilenameUtils.getName(downloadFrom));

		try (FileOutputStream output = new FileOutputStream(downloadTo)) {
			output.write(input);
		}
	}
}