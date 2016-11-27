package com.r.ceph.object.store.driver;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.r.ceph.object.store.commons.IObjectStoreUtil;
import com.r.ceph.object.store.commons.ObjectStoreUtil;
import com.rackspacecloud.client.cloudfiles.FilesContainer;
import com.rackspacecloud.client.cloudfiles.FilesObject;

/**
 * Ceph Object Store implementation.
 * 
 * main-class
 *
 * @author Ranjith M
 * @since 1.0
 */
public class Driver
{
	private Log log = LogFactory.getLog(Driver.class);

	public static void main(String[] args) {
		Driver driver = new Driver();
		driver.start();
	}

	/**
	 * method to start process
	 */
	public void start() {

		IObjectStoreUtil util = null;
		try {
			// 1. create container
			// 2. list all containers
			// 3. upload file
			// 4. copy file
			// 5. download files
			// 6. list files
			// 7. delete file
			// 8. delete container
			// 9. delete all containers

			int input = 6;
			String container = "myContainer";

			util = new ObjectStoreUtil();
			switch (input) {
				case 1:
					// -- create bucket
					util.createContainer(container);
					log.info("Container: " + container + " created successfully..");
					break;

				case 2:
					// list all containers
					List<?> containers = util.listContainers();

					if (containers != null && containers.size() > 0L) {
						log.info("Containers list:");
						log.info("----------------");

						for (Object c : containers) {
							if (c instanceof FilesContainer) {
								log.info("Name: " + ((FilesContainer) c).getName());
								log.info("Files Count: " + ((FilesContainer) c).getInfo().getObjectCount());
								log.info("Total Size: " + ((FilesContainer) c).getInfo().getTotalSize());
							}
						}
					} else {
						log.info("No containers available..");
					}
					break;

				case 3:
					// -- upload file to object store
					String inputFile = "";
					String uploadTo = "";

					util.uploadFile(container, uploadTo, new File(inputFile));
					log.info("File uploaded to container: " + container + " :: location: " + uploadTo);
					break;

				case 4:
					// -- copy file in object store
					String copyFrom = "";
					String copyTo = "";

					util.copyFile(container, copyFrom, container, copyTo);
					log.info("File copied from container: " + container + " :: location: " + copyFrom + " :: to container: " + container + " :: location: " + copyTo);
					break;

				case 5:
					// -- download file from object store
					String downloadFrom = "";;
					String downloadTo = "";

					util.downloadFiles(container, downloadFrom, downloadTo);
					log.info("File downloaded from container: " + container + " :: location: " + downloadFrom + " :: to local location: " + downloadTo);
					break;

				case 6:
					// -- list files in object store
					String path = "";
					List<?> files = util.listFiles(container, path);

					if (files != null && files.size() > 0L) {
						log.info("Files list:");
						log.info("-----------");

						for (Object file : files) {
							if (file instanceof FilesObject) {
								log.info("Name: " + ((FilesObject) file).getName());
								log.info("Size: " + ((FilesObject) file).getSize());
							}
						}
					} else {
						log.info("No files available..");
					}
					break;

				case 7:
					// -- delete file from object store
					String deleteFile = "";

					util.deleteFile(container, deleteFile);
					log.info("File deleted in container: " + container + " :: location: " + deleteFile);
					break;

				case 8:
					// -- delete container
					util.deleteContainer(container);
					log.info("Container: " + container + " deleted successfully..");
					break;

				case 9:
					// -- delete all containers
					List<?> containersList = util.listContainers();
					for (Object c : containersList) {
						if (c instanceof FilesContainer) {
							util.deleteContainer(((FilesContainer) c).getName());
							log.info("Container: " + ((FilesContainer) c).getName() + " deleted successfully..");
						}
					}
					break;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}