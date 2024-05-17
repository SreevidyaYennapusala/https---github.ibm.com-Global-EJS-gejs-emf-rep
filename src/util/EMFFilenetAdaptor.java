package util;

import java.util.ResourceBundle;

import javax.security.auth.Subject;


import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;

public class EMFFilenetAdaptor {
	
	//	Declaring the Logger Instance
//	private static Logger objlogger = Logger.getLogger (EMFFilenetAdaptor.class);
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	public Connection getCEConnection() throws Exception
	{
		// Getting Resource bundle file
		ResourceBundle objResourceBundle = EMFConfiguration.getResourceBundleName(Constants.RESOURCEBUNDLE_NAME);
		
		// Getting the Connection		
		Connection conn = Factory.Connection.getConnection(EMFConfiguration.getValue(objResourceBundle, Constants.CE_URI));
		String stanza = EMFConfiguration.getValue(objResourceBundle, Constants.JAAS_MODULE);
		//Getting the subject
		Subject subject = UserContext.createSubject(conn, EMFConfiguration.getValue(objResourceBundle, Constants.USER_NAME), 
				EMFConfiguration.getValue(objResourceBundle, Constants.PASSWORD), stanza);
		UserContext uc = UserContext.get();
		uc.pushSubject(subject);
//		objlogger.info("Got the connection");
	    return conn;
	 }
	
	/**
	 * 
	 * @param conn
	 * @return
	 */
	public  Domain getDomain(Connection conn)
	{
	    String domainName = null;
	    Domain domain = Factory.Domain.fetchInstance(conn, domainName, null);
//	    objlogger.info("Name of the domain: "+ domain.get_Name());
	    return domain;
	}
	
	/**
	 * 
	 * @param domain
	 * @param objectStoreName
	 * @return
	 */
	public ObjectStore getObjectStore(Domain domain, String objectStoreName)
	{
       ObjectStore store = Factory.ObjectStore.fetchInstance(domain, objectStoreName,null);
//       objlogger.info("Name of the object store: "+ store.get_Name());
       return store;
	}
	
	
}
