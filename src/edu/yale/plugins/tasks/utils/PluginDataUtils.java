package edu.yale.plugins.tasks.utils;

import com.thoughtworks.xstream.XStream;
import edu.yale.plugins.tasks.YalePluginTasks;
import edu.yale.plugins.tasks.model.ATContainerCollection;
import edu.yale.plugins.tasks.model.BoxLookupReturnRecordsCollection;
import org.archiviststoolkit.model.ATPluginData;
import org.archiviststoolkit.mydomain.DomainAccessObject;
import org.archiviststoolkit.mydomain.DomainAccessObjectFactory;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: nathan
 * Date: 10/2/12
 * Time: 7:58 PM
 *
 * Class to load and save Box look return records to and from the AT database
 */
public class PluginDataUtils {
    /**
     * Method to load the box lookup record
     */
    public static BoxLookupReturnRecordsCollection getBoxLookupReturnRecord(Long resourceId) {
        try {
            String dataName = YalePluginTasks.BOX_RECORD_DATA_NAME + "_" + resourceId;
            ATPluginData pluginData = getPluginData(dataName);

            if(pluginData != null) {
                BoxLookupReturnRecordsCollection boxRecord = (BoxLookupReturnRecordsCollection)getObjectFromPluginData(pluginData);
                return boxRecord;
            } else {
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to get an AT Container record for doing voyager searches
     *
     * @param resourceId
     * @return
     */
    public static ATContainerCollection getATContainerRecord(Long resourceId) {
        try {
            String dataName = YalePluginTasks.AT_CONTAINER_DATA_NAME + "_" + resourceId;
            ATPluginData pluginData = getPluginData(dataName);

            if(pluginData != null) {
                ATContainerCollection containerRecord = (ATContainerCollection)getObjectFromPluginData(pluginData);
                return containerRecord;
            } else {
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to save Box Look Return Records
     */
    public static void saveBoxLookReturnRecord(BoxLookupReturnRecordsCollection boxRecord) throws Exception {
        String dataName = YalePluginTasks.BOX_RECORD_DATA_NAME + "_" + boxRecord.getResourceId();

        ATPluginData pluginData = getPluginData(dataName);

        // use Xstream to convert the java object to an xml string
        XStream xstream = new XStream();
        String dataString = xstream.toXML(boxRecord);

        // see If there is an configuration data already saved. If any is found then
        // update it.
        if(pluginData == null) {
            pluginData = new ATPluginData(YalePluginTasks.PLUGIN_NAME, true,
                    1, dataName, YalePluginTasks.BOX_RECORD_DATA_NAME, dataString);
        } else {
            pluginData.setDataString(dataString);
        }

        // save using temp session
        try {
            Class clazz = pluginData.getClass();

            DomainAccessObject access =
                    DomainAccessObjectFactory.getInstance().getDomainAccessObject(clazz);
            //access.getLongSession();
            access.update(pluginData);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Method to save an AT container record
     */
    public static void saveATContainerRecord(ATContainerCollection containerRecord) throws Exception {
        String dataName = YalePluginTasks.AT_CONTAINER_DATA_NAME + "_" + containerRecord.getResourceId();

        ATPluginData pluginData = getPluginData(dataName);

        // use Xstream to convert the java object to an xml string
        XStream xstream = new XStream();
        String dataString = xstream.toXML(containerRecord);

        // see If there is an configuration data already saved. If any is found then
        // update it.
        if(pluginData == null) {
            pluginData = new ATPluginData(YalePluginTasks.PLUGIN_NAME, true,
                    1, dataName, YalePluginTasks.AT_CONTAINER_DATA_NAME, dataString);
        } else {
            pluginData.setDataString(dataString);
        }

        // save using temp session
        try {
            Class clazz = pluginData.getClass();

            DomainAccessObject access =
                    DomainAccessObjectFactory.getInstance().getDomainAccessObject(clazz);
            //access.getLongSession();
            access.update(pluginData);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Method to return plugin data from the database
     *
     * @param dataName
     * @return
     * @throws Exception
     */
    public static ATPluginData getPluginData(String dataName) throws Exception {
        try {
            DomainAccessObject access =
                    DomainAccessObjectFactory.getInstance().getDomainAccessObject(ATPluginData.class);

            ATPluginData pluginData = new ATPluginData();
            pluginData.setPluginName(YalePluginTasks.PLUGIN_NAME);
            pluginData.setDataName(dataName);

            Collection collection = access.findByExample(pluginData);

            // get the plugin data object returned from the database only return the first one
            if(collection != null && collection.size() > 0) {
                Object[] dataFound = collection.toArray();
                pluginData = (ATPluginData)dataFound[0];
                return pluginData;
            } else {
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new Exception("Error Getting Plugin Data from Database ...");
        }
    }

    /**
     * Method to delete an abstract maker not object from the database
     *
     * @param resourceId The name of the box lookup return record
     * @throws Exception If anything goes wrong throws an exception
     */
    public static void deleteBoxLookupReturnRecord(Long resourceId) throws Exception {
        String dataName = YalePluginTasks.BOX_RECORD_DATA_NAME + "_" + resourceId;
        deletePluginData(dataName);
    }

    /**
     * Method to delete a container record from the database
     *
     * @param resourceId
     * @throws Exception
     */
    public static void deleteATContainerRecord(Long resourceId) throws Exception {
        String dataName = YalePluginTasks.AT_CONTAINER_DATA_NAME + "_" + resourceId;
        deletePluginData(dataName);
    }

    /**
     * Method to delete a record from the plugin data directory
     *
     * @param dataName
     * @throws Exception
     */
    private static void deletePluginData(String dataName) throws Exception {
        // delete using temp session
        try {
            ATPluginData pluginData = getPluginData(dataName);

            Class clazz = pluginData.getClass();

            DomainAccessObject access =
                    DomainAccessObjectFactory.getInstance().getDomainAccessObject(clazz);
            access.delete(pluginData);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Return
     *
     * @param pluginData
     * @return
     */
    public static Object getObjectFromPluginData(ATPluginData pluginData) {
        XStream xstream = new XStream();
        xstream.setClassLoader(PluginDataUtils.class.getClassLoader());
        return xstream.fromXML(pluginData.getDataString());
    }

}