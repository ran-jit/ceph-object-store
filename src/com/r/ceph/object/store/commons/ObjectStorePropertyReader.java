package com.r.ceph.object.store.commons;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.r.ceph.object.store.constants.ObjectStoreConstants;

/**
 * Ceph Object Store implementation.
 * 
 * This class is uses to read the object store configuration properties.
 *
 * @author Ranjith M
 * @since 1.0
 */
public class ObjectStorePropertyReader
{
	private Log log = LogFactory.getLog(ObjectStorePropertyReader.class);

	private Properties properties;

	private static class ObjectStorePropertyReaderHelper
	{
		private static final ObjectStorePropertyReader INSTANCE = new ObjectStorePropertyReader();
	}

	private ObjectStorePropertyReader() {
		loadProperties();
	}

	public static ObjectStorePropertyReader getInstance() {
		return ObjectStorePropertyReaderHelper.INSTANCE;
	}

	/**
	 * method to load properties
	 */
	private void loadProperties() {
		properties = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		try (InputStream resourceStream = loader.getResourceAsStream(ObjectStoreConstants.CONFIGURATION_PROPERTIES_FILE)) {
			properties.load(resourceStream);
		} catch (Exception ex) {
			log.error("Error occured while loading object store properties", ex);
		}
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public String getProperty(String key, String defVaule) {
		return properties.getProperty(key, defVaule);
	}
}