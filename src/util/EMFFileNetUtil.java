package util;

import java.util.ResourceBundle;

import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;



/**
 *  This class is for all plugin operations.
 * @author 
 *
 */
public class EMFFileNetUtil {
	
	//	Declaring the Logger Instance
//	private static Logger objlogger = Logger.getLogger (EMFFileNetUtil.class);
	
	/**
	 * This method is for get demo graphic details
	 * @param strSerialNo
	 * @return
	 * @throws Exception
	 */
//	public JSONObject getDemoGraphicDetails(String strSerialNo) throws Exception
//	{
//		// Getting the Demo Graphic Details
////		DBUtil objDBUtil = new DBUtil();
//		JSONObject objJSONObject =  new JSONObject();
//		
////		JSONObject objJSONObject = objDBUtil.getDemoGraphicDetails(strSerialNo);
//		return objJSONObject;
//	}
	
	/**
	 *  This method is for Search the History of Serial No
	 * @param strSerialNo
	 * @return
	 * @throws Exception
	 */
//	public ArrayList SearchHistory(String strSerialNo) throws Exception
//	{
		// Searching for documents
//		ArrayList objArrayList = searchForDocuments(strSerialNo);
//		JSONArray objResultsArray = null;
//		
//		try
//		{
//		
//			// Check for the documents size of history
//			if(objArrayList.size() == 0)
//			{
//				objResultsArray = new JSONArray();
//				objResultsArray.add(Constants.NO_RECORDS_FOUND);
//			}
//			else
//			{	
//				// Search for comments based VSID
//				objResultsArray = searchForComments(objArrayList);
//				
//				// Check for the documents size of history
//				if(objResultsArray.size() == 0)
//				{
//					objResultsArray = new JSONArray();
//					objResultsArray.add(Constants.NO_RECORDS_FOUND);
//				}
//				else
//				{
//					JSONObject objJSONObject = getDemoGraphicDetails(strSerialNo);
//					
//					if(objJSONObject.size() == 0)
//					{
//						objResultsArray.add(Constants.NO_RECORDS_FOUND);
//					}
//					else
//					{
//						// Getting Resource bundle file
//						ResourceBundle objResourceBundle = EMFConfiguration.getResourceBundleName
//								(Constants.RESOURCEBUNDLE_NAME);
//						String strName = objJSONObject.get(EMFConfiguration.getValue(objResourceBundle, Constants.FIRST_NAME))
//								+" "+objJSONObject.get(EMFConfiguration.getValue(objResourceBundle, Constants.LAST_NAME));
//						objResultsArray.add(strName);
//					}
//										
//				}
//				
//			}
//		}
//		catch (Exception ex)
//		{
//			throw new Exception(ex.getMessage());
//		}
//		System.out.println(objArrayList);
//		return objArrayList;
//	}
	
	/**
	 * 
	 * @param objArrayList
	 * @return
	 * @throws Exception
	 */
//	private JSONArray searchForComments(ArrayList objArrayList) throws Exception {
//		
//		// Defining JSON Array
//		JSONArray objResultsArray = new JSONArray();
//		try
//		{
//			// Getting Resource bundle file
//			ResourceBundle objResourceBundle = EMFConfiguration.getResourceBundleName
//					(Constants.RESOURCEBUNDLE_NAME);
//			
//			// Defining object instance
//			EMFFilenetAdaptor objFilenet = new EMFFilenetAdaptor();
//			
//			// Getting FileNet Connection
//			Connection objConn = objFilenet.getCEConnection();
//			
//			// Getting FileNet Domain
//			Domain objDomain = objFilenet.getDomain(objConn);
//			
//			// Getting FileNet objectStore
//			ObjectStore objStore = objFilenet.getObjectStore(objDomain, EMFConfiguration.
//					getValue(objResourceBundle, Constants.OBJECTSTORE_NAME));
//			
//			
//			for (int i = 0; i < objArrayList.size(); i++) {
//				
//				String strVSID = objArrayList.get(i).toString();
//				// Create a SearchSQL instance and specify the SQL statement (using the helper methods).
//				SearchSQL sqlObject = new SearchSQL();
//				sqlObject.setSelectList(Constants.COMMENTS_SELECTLIST);
//				sqlObject.setFromClauseInitialValue(Constants.COMMENTS_DOCUMENTCLASS_NAME,null, false); 
//				sqlObject.setWhereClause(Constants.COMMENTS_VERSIONSERIES+strVSID);
//				sqlObject.setOrderByClause(Constants.COMMENTS_ORDERBY);
//				        
//				// Create a SearchScope instance. (Assumes you have the object store object.)
//				SearchScope search = new SearchScope(objStore);
//		
//				// Execute the fetchRows method using the specified parameters.
//				Boolean continuable = new Boolean(true);
//				RepositoryRowSet myRows = search.fetchRows(sqlObject, null, null, continuable);
//				        
//				// Iterate the collection of rows to access the properties.
//				Iterator iter = myRows.iterator();
//				JSONObject jsonObject = new JSONObject();
//				
//				while (iter.hasNext()) 
//				{
//				    RepositoryRow row = (RepositoryRow) iter.next();
//				    // Print properties from the result set.
//				    String strComment = row.getProperties().get(Constants.COMMENTS_TEXT).getStringValue();
//				    String strLastModifier = row.getProperties().get (Constants.LAST_MODIFIER).getStringValue();
//				    Date date = row.getProperties().get (Constants.DATE_CREATED).getDateTimeValue();
//					Calendar cal = Calendar.getInstance();
//					cal.setTime(date);
//					String strDate= getDate(cal);
//					objResultsArray.add(strComment+"~"+strLastModifier+"~"+strDate);
//				}
//				
//			}
//			
//		}
//		catch (Exception ex)
//		{
//			throw new Exception(ex.getMessage());
//		}
//		return objResultsArray;
//		
//	}


	/**
	 * This method is for Search the documents based on Serial No
	 * @param strSerialNo
	 * @return
	 * @throws Exception
	 */
	public boolean searchForDocuments(String strSerialNo) throws Exception
	{
		// Defining Array List
//		ArrayList obArrayList = new ArrayList();
		boolean exists	=	false;
		try
		{
			// Getting Resource bundle file
			ResourceBundle objResourceBundle = EMFConfiguration.getResourceBundleName
					(Constants.RESOURCEBUNDLE_NAME);
			
			// Defining object instance
			EMFFilenetAdaptor objFilenet = new EMFFilenetAdaptor();
			
			// Getting FileNet Connection
			Connection objConn = objFilenet.getCEConnection();
			
			// Getting FileNet Domain
			Domain objDomain = objFilenet.getDomain(objConn);
			
			// Getting FileNet objectStore
			ObjectStore objStore = objFilenet.getObjectStore(objDomain, EMFConfiguration.
					getValue(objResourceBundle, Constants.OBJECTSTORE_NAME));
			
			// Framing the whereclause
			String strQuery = EMFConfiguration.getValue(objResourceBundle,
					Constants.SEARCH_SERIALNO_QUERY);
			
			strQuery = strQuery + "'" + strSerialNo + "' " +Constants.SEARCH_SERIALNO_QUERY_VERSIONSTATUS;
			
			//Defining the search sql Object
			SearchSQL searchSQL = new SearchSQL();
			String select = "*";
			searchSQL.setSelectList(select);
			// Set where clause
			searchSQL.setWhereClause(strQuery);
			
			// setting the document class to search SQL
			String strDocClass = EMFConfiguration.getValue(objResourceBundle,Constants.
					DOCUMENTCLASS_NAME);
			searchSQL.setFromClauseInitialValue(strDocClass, null, false);
			
			//Setting the order by clause
			searchSQL.setOrderByClause(EMFConfiguration.getValue(objResourceBundle, 
					Constants.ORDERBY));
//			objlogger.info("searchSQL : "+searchSQL.toString());
			
			// Defining search scope object
			SearchScope searchScope = new SearchScope(objStore);
			
			//setting the Page Size
			Integer pageSize = new Integer(EMFConfiguration.getValue(objResourceBundle, 
					Constants.PAGE_SIZE));
			PropertyFilter propertyFilter = null;
			Boolean continuable = Boolean.FALSE;
			
			//Retrieving the search results
			RepositoryRowSet results = searchScope.fetchRows(searchSQL, pageSize,
					propertyFilter, continuable);
			
//			Iterator iter = results.iterator();
			System.out.println("SQL: "+searchSQL);
			exists	=	results.isEmpty();
			System.out.println("Result Empty: " +exists);
			
			// Iterating the results
//			while (iter.hasNext())
//			{
//				  RepositoryRow repositoryRow = (RepositoryRow) iter.next();
//				  // Getting the ID
//				  String strGuid = repositoryRow.getProperties().getIdValue(PropertyNames.ID).
//						  toString();
//				  
//				  // Get the document object from doc id
//				  Document objDocument  =  Factory.Document.fetchInstance(objStore,strGuid,null);
//				  
//				  // Getting the VSID
//				  String strVsId = objDocument.get_VersionSeries().get_Id().toString();
//				  obArrayList.add(strVsId);
//			}
		}
		catch (Exception ex)
		{
			throw new Exception(ex.getMessage());
		}
		return exists;
	}
	
	/**
	 * This is convert the filenet date
	 * @param calendar
	 * @return
	 */
//	public static String getDate(Calendar calendar) {
//		Date date = calendar.getTime();
//		SimpleDateFormat sdfOut = new SimpleDateFormat(Constants.DATE_FORMAT);
//		String strSQLDate = sdfOut.format(date);
//		return strSQLDate;		
//	}
	public static void main(String[] args) throws Exception {
		EMFFileNetUtil objEMFPluginUtil = new EMFFileNetUtil();
		objEMFPluginUtil.searchForDocuments("1234567");
		//objEMFPluginUtil.SearchHistory("191919");
	}
}
