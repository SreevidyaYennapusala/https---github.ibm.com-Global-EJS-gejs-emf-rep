package util;

import java.util.ResourceBundle;


/**
 * 
 * @author 0009VM744
 *
 */
public class EMFConfiguration {
	
	
	/**
	* Getting the ResourceBundle based on the FileName
	* @param fileName
	* @return
	* @throws Exception
	*/
	public static ResourceBundle getResourceBundleName(String fileName) throws Exception
	{
		ResourceBundle objResourceBundle = null;
		try{
			//get the ResourceBundle object
			objResourceBundle = ResourceBundle.getBundle (fileName);
			return objResourceBundle;
			}catch(Exception e){
				throw new Exception(e.getMessage());
			}
	}
	
	/**
	 * 
	 * @param rb
	 * @param keyName
	 * @return
	 */
	public static String getValue(ResourceBundle rb, String keyName)
	{
		return rb.getString(keyName).trim();
	}
	
}
