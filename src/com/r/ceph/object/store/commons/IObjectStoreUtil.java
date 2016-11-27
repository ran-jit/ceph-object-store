package com.r.ceph.object.store.commons;

import java.io.File;
import java.util.List;

/**
 * Ceph Object Store implementation.
 * 
 * Interface for object actions.
 *
 * @author Ranjith M
 * @since 1.0
 */
public interface IObjectStoreUtil
{
	/**
	 * method to create new object container
	 * 
	 * @param container
	 * @throws Exception
	 */
	public void createContainer(String container) throws Exception;

	/**
	 * method to list all containers
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<?> listContainers() throws Exception;

	/**
	 * method to delete object container
	 * 
	 * @param container
	 * @throws Exception
	 */
	public void deleteContainer(String container) throws Exception;

	/**
	 * method to upload file to object store
	 * 
	 * @param container
	 * @param uploadTo
	 * @param input
	 * @throws Exception
	 */
	public void uploadFile(String container, String uploadTo, File input) throws Exception;

	/**
	 * method to copy file in object store
	 * 
	 * @param copyFromContainer
	 * @param copyFrom
	 * @param copyToContainer
	 * @param copyTo
	 * @throws Exception
	 */
	public void copyFile(String copyFromContainer, String copyFrom, String copyToContainer, String copyTo) throws Exception;

	/**
	 * method to download files from object store
	 * 
	 * @param container
	 * @param downloadFrom
	 * @param downloadTo
	 * @throws Exception
	 */
	public void downloadFiles(String container, String downloadFrom, String downloadTo) throws Exception;

	/**
	 * method to list files in container from object store
	 * 
	 * @param container
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public List<?> listFiles(String container, String path) throws Exception;

	/**
	 * method to delete file from object store
	 * 
	 * @param container
	 * @param key
	 * @throws Exception
	 */
	public void deleteFile(String container, String key) throws Exception;

}