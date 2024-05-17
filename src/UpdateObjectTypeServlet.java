/*
 * Licensed Materials - Property of IBM (c) Copyright IBM Corp. 2012, 2020  All Rights Reserved.
 * 
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 * 
 * DISCLAIMER OF WARRANTIES :
 * 
 * Permission is granted to copy and modify this Sample code, and to distribute modified versions provided that both the
 * copyright notice, and this permission notice and warranty disclaimer appear in all copies and modified versions.
 * 
 * THIS SAMPLE CODE IS LICENSED TO YOU AS-IS. IBM AND ITS SUPPLIERS AND LICENSORS DISCLAIM ALL WARRANTIES, EITHER
 * EXPRESS OR IMPLIED, IN SUCH SAMPLE CODE, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL IBM OR ITS LICENSORS OR SUPPLIERS BE LIABLE FOR
 * ANY DAMAGES ARISING OUT OF THE USE OF OR INABILITY TO USE THE SAMPLE CODE, DISTRIBUTION OF THE SAMPLE CODE, OR
 * COMBINATION OF THE SAMPLE CODE WITH ANY OTHER CODE. IN NO EVENT SHALL IBM OR ITS LICENSORS AND SUPPLIERS BE LIABLE
 * FOR ANY LOST REVENUE, LOST PROFITS OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, EVEN IF IBM OR ITS LICENSORS OR SUPPLIERS HAVE
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Constants;
import util.EMFFileNetUtil;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * This sample  servlet implements the Update Object Type EDS service.  It looks for json files
 * of the same name as the object type, with an extension of _PropertyData.json.  It then uses the information
 * in that JSON file, along with the values of properties passed into the servlet, to construct a response JSON
 * that defines or overrides property values, choice lists, and metadata.
 */
public class UpdateObjectTypeServlet extends HttpServlet {
	
	/*
	 * This is how the JSON coming in is structured. 

		POST /type/<object type name>
		
		{
			"repositoryId":"<target repository>",
			"objectId" : "<if an existing instance, the GUID, PID, etc>",
			"requestMode" : "<indicates context that info is being requested>",
			"externalDataIdentifier" : "<opaque identifier meaningful to service">,
			"properties":
			[
				{
					"symbolicName" : "<symbolic_name>", 
					"value" : <The current value>,
				}
				// More properties ...
			],
			"clientContext":
			{
				"userid":"<user id>",
				"locale":"<browser locale>",
				"desktop": "<desktop id>"
			}
		}
	 *
	 * The requestMode has values:
	 *  initialNewObject -- when a new object is being created (when add doc, create folder, checkin dialogs first appear)
	 *  initialExistingObject -- when an existing object is being edited (when edit properties first appears)
	 *  inProgressChanges -- when an object is being modified (for dependent choice lists)
	 *  finalNewObject -- before the object is persisted (when action is performed on add doc, create folder, checkin)
	 *  finalExistingObject -- before the existing object is persisted (when save action is performed on edit properties)
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 6157276136535276650L;


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		System.out.println("#### EMFCustomEDSService.UpdateObjectTypeServlet Start Time ####  "+getCurrentDateTime());
		
		String objectType = request.getPathInfo().substring(1);
		
		System.out.println("EMFCustomEDSService.UpdateObjectTypeServlet objectType:"+objectType);
		
		// Get the request json
		InputStream requestInputStream = request.getInputStream();
		JSONObject jsonRequest = JSONObject.parse(requestInputStream);
		String requestMode = jsonRequest.get(Constants.HTTP_REQUEST_MODE).toString();
		JSONArray requestProperties = (JSONArray)jsonRequest.get(Constants.HTTP_REQUEST_PROPERTIES);
		JSONArray responseProperties = new JSONArray();
		JSONArray propertyData = getPropertyData(objectType, request.getLocale());
		JSONObject clientContext = (JSONObject)jsonRequest.get(Constants.HTTP_REQUEST_CLIENT_CONTEXT);
		String icnDesktop = (String)clientContext.get("desktop");
		String icnAction = (String)clientContext.get(Constants.ICN_ACTION);
		String objectId = (String)jsonRequest.get(Constants.HTTP_REQUEST_OBJECT_ID);
		
		System.out.println("EMFCustomEDSService.UpdateObjectTypeServlet: clientContext="+clientContext);
		System.out.println("EMFCustomEDSService.UpdateObjectTypeServlet: DESKTOP ID: "+icnDesktop);
		System.out.println("EMFCustomEDSService.UpdateObjectTypeServlet: User Action: "+icnAction);
		System.out.println("EMFCustomEDSService.UpdateObjectTypeServlet: objectType="+objectType+" objectId=" + objectId + " requestMode="+requestMode);
		System.out.println("EMFCustomEDSService.UpdateObjectTypeServlet: requestProperties= "+requestProperties);
		
		if (icnDesktop != null && (icnDesktop.equals(Constants.ICN_DESKTOP_ID) || icnDesktop.equals(Constants.ICN_DESKTOP_TEST_ID)))
		{
			// "action" - "addItem", "checkin", "editProperties", "multiEditProperties", "viewEditProperties", "workflow", or custom action name
			// "build" - "icn203.555" etc.
			// "clientIdentity" - "navigatorWeb", "navigatorMobile", "navigatorOffice", or custom client name
			// "desktop" - Desktop id string (not the display name)
			// "entryTemplateId" - Entry template document Id
			// "entryTemplateItemId" - Entry template ITEMID (CM)
			// "entryTemplateName" - Entry template name
			// "entryTemplateVsId" - Entry template version series id (P8)
			// "locale" - client locale
			// "objectStoreId" - P8 object store Id associated with the action (P8)
			// "userid" - Id of user executing the action
			
			System.out.println("EMFCustomEDSService.UpdateObjectTypeServlet: Cookie="+request.getHeader(Constants.HTTP_REQUEST_COOKIE));

			try {
			
	
				// First, for initial object calls, fill in overrides of initial values. icnAction added to avoid search template execution
				if (requestMode.equals(Constants.RESPONSE_MODE_INITIAL_NEW_OBJECT) && icnAction == null) {
					for (int i = 0; i < propertyData.size(); i++) {
						JSONObject overrideProperty = (JSONObject)propertyData.get(i);
						String overridePropertyName = overrideProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
						if (overrideProperty.containsKey(Constants.PROPERTY_INITIAL_VALUE)) {
							for (int j = 0; j < requestProperties.size(); j++) {
								JSONObject requestProperty = (JSONObject)requestProperties.get(j);
								String requestPropertyName = requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
								if (overridePropertyName.equals(requestPropertyName)) {
									Object initialValue = overrideProperty.get(Constants.PROPERTY_INITIAL_VALUE);
									// setting initial values
									initialValue	=	getInitialValue(requestPropertyName, clientContext);
									requestProperty.put(Constants.PROPERTY_VALUE, initialValue);
								}
							}
						}
					}
				}
				
				// For both initial and in-progress calls, process the property data to add in choice lists and modified metadata
				for (int i = 0; i < propertyData.size(); i++) {
					JSONObject overrideProperty = (JSONObject)propertyData.get(i);
					if (requestMode
							.equals(Constants.RESPONSE_MODE_INITIAL_NEW_OBJECT)
							|| requestMode
									.equals(Constants.RESPONSE_MODE_INITIAL_EXISTING_OBJECT)
							|| requestMode
									.equals(Constants.RESPONSE_MODE_INPROGRESS)) 
					{ 
						// dependOn is not getting used in any doc class JSON
						if (overrideProperty.containsKey("dependentOn")) 
						{
							// perform dependent overrides (such as dependent choice lists) for inProgressChanges calls only
							// although they can be processed for initial calls, it will influence searches (narrowing the search choices)
							
							if (requestMode.equals(Constants.RESPONSE_MODE_INPROGRESS)) 
							{
								String error = null;
								// Treat null and "" as the same - no value.
								// The old common properties pane passes "" for no value, the new ET property layout passes null,
								// and initial empty values from the class definition will be null. 
								String dependentOn = overrideProperty.get("dependentOn").toString();
								
								System.out.println("InProgress: dependentOn: "+dependentOn);
								Object dateObject	= null;
								Object timestampObjectValue	=	 null;
								for (int j = 0; j < requestProperties.size(); j++) 
								{
									JSONObject requestProperty = (JSONObject)requestProperties.get(j);
									String requestPropertyName = requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
									
									if(requestPropertyName.equals(Constants.PROPERTY_SYM_TIMESTAMP))	timestampObjectValue	= requestProperty.get(Constants.PROPERTY_VALUE);
									if(requestPropertyName.equals(Constants.PROPERTY_SYM_LAB_DATE_0001))	dateObject	= requestProperty.get(Constants.PROPERTY_VALUE);
								}
									// dependOn property value
								System.out.println("InProgress: dependentOn: Value: "+dateObject);
								System.out.println("InProgress: timestampObjectValue: Value: "+timestampObjectValue);
								if(timestampObjectValue != null && !(timestampObjectValue.toString().equals("")) && dateObject != null && !(dateObject.toString().equals("")))
								{
									//compare dates
									String status	=	compareDates(timestampObjectValue,dateObject);
									System.out.println("compare date status: "+status);
									if(!status.equals("") && status.equals(Constants.COMPARE_DATE_GREATER) && objectType.equals(Constants.DOC_CLASS_SYM_LAB_REPORT_0001))
									{
										error = Constants.MESSAGE_LAB_DATE;
									}
								}
								if (error != null) 
								{
									JSONObject returnProperty = (JSONObject)overrideProperty.clone();
									returnProperty.put(Constants.PROPERTY_VALIDATION_CUSTOM_VALIDATION_ERROR, error);
									for (int jj = 0; jj < requestProperties.size(); jj++) 
									{
										JSONObject requestProperty1 = (JSONObject)requestProperties.get(jj);
	//									returnProperty.put(dependentOn, requestProperty1.get(Constants.PROPERTY_SYMBOLIC_NAME));
										if(requestProperty1.get(Constants.PROPERTY_SYMBOLIC_NAME).equals(Constants.PROPERTY_SYMBOLIC_NAME))
										{
											
											returnProperty.put(Constants.PROPERTY_SYMBOLIC_NAME, requestProperty1.get(Constants.PROPERTY_SYMBOLIC_NAME));
										}
									}
									responseProperties.add(returnProperty);
								}
							}
						} 
						// icnAction added to make sure during search template execution doc class properties are not readonly/pre-populated
						else if(requestMode.equals(Constants.RESPONSE_MODE_INITIAL_EXISTING_OBJECT) && icnAction != null && icnAction.equals(Constants.ICN_ACTION_OPENSEARCHTEMPLATE))
						{
							overrideProperty.put(Constants.PROPERTY_DISPLAY_MODE, "readwrite");
							overrideProperty.put(Constants.PROPERTY_REQUIRED_SMALL, false);
							// Add the property override
							responseProperties.add(overrideProperty);
						}
						else {
							// Apply the initial value, if any, during an initial object call only
							if (requestMode.equals(Constants.RESPONSE_MODE_INITIAL_NEW_OBJECT) && overrideProperty.containsKey(Constants.PROPERTY_INITIAL_VALUE)  && icnAction == null)
							{
								Object initalValue	=	overrideProperty.get(Constants.PROPERTY_INITIAL_VALUE);
								initalValue	=	getInitialValue(overrideProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString(), clientContext);
								overrideProperty.put(Constants.PROPERTY_VALUE, initalValue);
							}
							// Add the property override
							responseProperties.add(overrideProperty);
						}
					}
					
					// EMF custom validations - start
					if (requestMode.equals(Constants.RESPONSE_MODE_FINAL_NEW_OBJECT) || requestMode.equals(Constants.RESPONSE_MODE_FINAL_EXISTING_OBJECT)) {
						if (overrideProperty.containsKey(Constants.PROPERTY_VALIDATE_AS)) {
							Object timestampObjectValue	=	 null;
							Object propObjectValue	=	null;
							String validationType = overrideProperty.get(Constants.PROPERTY_VALIDATE_AS).toString();
							String symbolicName = overrideProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
							String error = null;
							if (validationType.equals(Constants.PROPERTY_VALIDATION_AS_NEXT_EXAM_DATE)) {
								Object mmstatusObjectValue	=	null;
								Object examDateObjectValue	=	null;
								Object propDateObjectValue	=	 null;
								Object	dateOfVisitObjectValue	=	null;
								
								for (int j = 0; j < requestProperties.size(); j++) {
									JSONObject requestProperty = (JSONObject)requestProperties.get(j);
									String requestPropertySymbolicName = requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
									
									if(requestPropertySymbolicName.equals(Constants.PROPERTY_SYM_EXAM_DATE_0001))	examDateObjectValue	= requestProperty.get(Constants.PROPERTY_VALUE);
									if(requestPropertySymbolicName.equals(Constants.PROPERTY_SYM_MMSTATUS))	mmstatusObjectValue	= requestProperty.get(Constants.PROPERTY_VALUE);
									if(requestPropertySymbolicName.equals(symbolicName))	propDateObjectValue	= requestProperty.get(Constants.PROPERTY_VALUE);
									if(requestPropertySymbolicName.equals(Constants.PROPERTY_SYM_DATE_OF_VI_0001))	dateOfVisitObjectValue	= requestProperty.get(Constants.PROPERTY_VALUE);
								}
								
								System.out.println("validateAs:: mmstatusObjectValue: "+mmstatusObjectValue);
								System.out.println("validateAs:: examDateObjectValue: "+examDateObjectValue);
								System.out.println("validateAs:: propDateObjectValue: "+propDateObjectValue);
								System.out.println("validateAs:: dateOfVisitObjectValue: "+dateOfVisitObjectValue);
								
								if (mmstatusObjectValue != null
										&& examDateObjectValue != null
										&& propDateObjectValue != null
										&& (mmstatusObjectValue.toString())
												.equalsIgnoreCase("C")
										&& objectType
												.equals(Constants.DOC_CLASS_SYM_MON_DO_0001))
								{
									String status	=	compareDates(examDateObjectValue, propDateObjectValue);
									System.out.println("compare date status: "+status);
									if(!status.equals("") && (status.equals(Constants.COMPARE_DATE_EQUAL) || status.equals(Constants.COMPARE_DATE_LESSER)))
									{
										error = Constants.MESSAGE_NEXT_EXAM_DATE;
									}
								}
								if(propDateObjectValue != null && dateOfVisitObjectValue!= null && objectType.equals(Constants.DOC_CLASS_SYM_WC_OSHA_DO_0001))
								{
									String status	=	compareDates(dateOfVisitObjectValue,propDateObjectValue);
		//							System.out.println("compare date status: "+status);
									if(!status.equals("") && (status.equals(Constants.COMPARE_DATE_GREATER)))
									{
										error = Constants.MESSAGE_WC_DATE;
									}
								}							
								if (error != null) {
									JSONObject returnProperty = (JSONObject)overrideProperty.clone();
									returnProperty.put(Constants.PROPERTY_VALIDATION_CUSTOM_VALIDATION_ERROR, error);
									for (int j = 0; j < requestProperties.size(); j++) 
									{
										JSONObject requestProperty = (JSONObject)requestProperties.get(j);
										if(requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).equals(symbolicName))
										returnProperty.put(Constants.PROPERTY_SYMBOLIC_NAME, requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME));
									}
									responseProperties.add(returnProperty);
								}
							}
							if (validationType.equals(Constants.PROPERTY_VALIDATION_AS_DATE_INDEXED)) {
								
								error = null;
								
								for (int j = 0; j < requestProperties.size(); j++) {
									JSONObject requestProperty = (JSONObject)requestProperties.get(j);
									String requestPropertySymbolicName = requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
									
									if(requestPropertySymbolicName.equals(Constants.PROPERTY_SYM_TIMESTAMP))	timestampObjectValue	= requestProperty.get(Constants.PROPERTY_VALUE);
									if(requestPropertySymbolicName.equals(symbolicName))	propObjectValue	= requestProperty.get(Constants.PROPERTY_VALUE);
									if(timestampObjectValue == null) timestampObjectValue	=	getCurrentDateTime();
								}
								
								System.out.println("validateAs::timestampObjectValue: "+timestampObjectValue);
								System.out.println("validateAs:: propObjectValue: "+propObjectValue);
								
								if(propObjectValue != null && timestampObjectValue != null && !timestampObjectValue.equals("") && !propObjectValue.equals("")) {
									String status	=	compareDates(timestampObjectValue,propObjectValue);
									System.out.println("compare date status: "+status);
									if(!status.equals("") && status.equals(Constants.COMPARE_DATE_GREATER) && objectType.equals(Constants.DOC_CLASS_SYM_WELLNESS_D_0001))
									{
										error = Constants.MESSAGE_EXAM_DATE;
									}
									if(!status.equals("") && status.equals(Constants.COMPARE_DATE_GREATER) && objectType.equals(Constants.DOC_CLASS_SYM_LAB_REPORT_0001))
									{
										error = Constants.MESSAGE_LAB_DATE;
									}
									if(!status.equals("") && status.equals(Constants.COMPARE_DATE_GREATER) && objectType.equals(Constants.DOC_CLASS_SYM_IMMUNIZATI_0001))
									{
										error = Constants.MESSAGE_IMM_DATE;
									}
									if(!status.equals("") && status.equals(Constants.COMPARE_DATE_LESSER) && objectType.equals(Constants.DOC_CLASS_SYM_REL_OF_INF_0001))
									{
										error = Constants.MESSAGE_REL_DATE;
									}
								}
								if (error != null) {
									JSONObject returnProperty = (JSONObject)overrideProperty.clone();
									returnProperty.put(Constants.PROPERTY_VALIDATION_CUSTOM_VALIDATION_ERROR, error);
									for (int j = 0; j < requestProperties.size(); j++) 
									{
										JSONObject requestProperty = (JSONObject)requestProperties.get(j);
										if(requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).equals(symbolicName))
										returnProperty.put(Constants.PROPERTY_SYMBOLIC_NAME, requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME));
									}
									responseProperties.add(returnProperty);
								}
							}
							
							
						
							
							if (validationType.equals(Constants.PROPERTY_VALIDATION_AS_ISSERIALNOEXISTS) && objectType.equals(Constants.DOC_CLASS_SYM_EMP_MED_FO_0002)) {
								error = null;
								
								for (int j = 0; j < requestProperties.size(); j++) {
									JSONObject requestProperty = (JSONObject)requestProperties.get(j);
									String requestPropertySymbolicName = requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
									if(requestPropertySymbolicName.equals(symbolicName))	propObjectValue	= requestProperty.get(Constants.PROPERTY_VALUE);
								}
								boolean isSearialNoExist	=	true;
								System.out.println("validateAs:: serial no exists: propObjectValue: "+propObjectValue);
								if(propObjectValue != null && !(propObjectValue.toString().equals(""))) {
									// call FileNet Search
									EMFFileNetUtil objEMFPluginUtil = new EMFFileNetUtil();
									isSearialNoExist	=	objEMFPluginUtil.searchForDocuments(propObjectValue.toString());
									
									if(!isSearialNoExist)	error = propObjectValue.toString() + " " + Constants.MESSAGE_EMP_MED_FOL;
									//Write Med Folder Details should be added 
//									if(isSearialNoExist)
//									{
//										
//									}
								}
								// Set DB Values flag base
								
								if (error != null) {
									JSONObject returnProperty = (JSONObject)overrideProperty.clone();
									returnProperty.put(Constants.PROPERTY_VALIDATION_CUSTOM_VALIDATION_ERROR, error);
									for (int j = 0; j < requestProperties.size(); j++) 
									{
										JSONObject requestProperty = (JSONObject)requestProperties.get(j);
										if(requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).equals(symbolicName))
										returnProperty.put(Constants.PROPERTY_SYMBOLIC_NAME, requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME));
									}
									responseProperties.add(returnProperty);
								}
							}
						}
//						//Setting empty date values like timestamp, etc. but commented as due to this code ICN is not able to save document
//						if(overrideProperty.containsKey(Constants.PROPERTY_INITIAL_VALUE))
//						{
//							System.out.println("Setting empty date values");
//							String overridePropertyName = overrideProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
//							System.out.println("Setting empty date values: overridePropertyName:  "+overridePropertyName);
//							
//							Object propObjectValue	=	null;//overrideProperty.get(Constants.PROPERTY_VALUE);
//							
//							for (int j = 0; j < requestProperties.size(); j++) 
//							{
//								JSONObject requestProperty = (JSONObject)requestProperties.get(j);
//								String requestPropertySymbolicName = requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
//								if(requestPropertySymbolicName.equals(overridePropertyName))	propObjectValue	= requestProperty.get(Constants.PROPERTY_VALUE);
//							}
//
//							System.out.println("Setting empty date values: propObjectValue:  "+propObjectValue);							
//							if(propObjectValue == null || (propObjectValue.toString().equals("")))
//							{
//								overrideProperty.put(Constants.PROPERTY_VALUE, getInitialValue(overridePropertyName, clientContext));
//
////								JSONObject returnProperty = (JSONObject)overrideProperty.clone();
////								returnProperty.put(Constants.PROPERTY_VALUE, getInitialValue(overridePropertyName, clientContext));
//									responseProperties.add(overrideProperty);
//							}
//						}
					}
					// EMF custom validations - End
					
					// For final calls, perform custom validations and property overrides
					if (requestMode.equals(Constants.RESPONSE_MODE_FINAL_NEW_OBJECT) || requestMode.equals(Constants.RESPONSE_MODE_FINAL_EXISTING_OBJECT)) {
						if (overrideProperty.containsKey(Constants.PROPERTY_VALIDATE_AS)) {
							
							// perform custom validation
							String validationType = overrideProperty.get(Constants.PROPERTY_VALIDATE_AS).toString();
	
	//						String error = null;
							String symbolicName = overrideProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
	//						// out of box commented
	//						if (validationType.equals("NoThrees")) {
	//							// a sample validation that simply restricts the field from having a 3 anywhere
	////							String symbolicName = overrideProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
	//							for (int j = 0; j < requestProperties.size(); j++) {
	//								JSONObject requestProperty = (JSONObject)requestProperties.get(j);
	//								String requestPropertySymbolicName = requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
	//								if (requestPropertySymbolicName.contains("[")) { // child component index.. ignore
	//									requestPropertySymbolicName = requestPropertySymbolicName.substring(0,requestPropertySymbolicName.indexOf("["));
	//								}
	//								if (symbolicName.equals(requestPropertySymbolicName)) {
	////									String error = null;
	//									Object propertyValue = requestProperty.get(Constants.PROPERTY_VALUE);
	//									if (propertyValue instanceof String) {
	//										String requestValue = (String) propertyValue;
	//										if (requestValue.contains("3") || requestValue.toLowerCase().contains("three") || requestValue.toLowerCase().contains("third")) {
	//											error = "This string field cannot contain any threes";
	//										}
	//									} else if (propertyValue instanceof Long) {
	//										Long requestValue = (Long) propertyValue;
	//										if (requestValue == 3 || requestValue == 33 || requestValue == 333) {
	//											error = "This integer field cannot contain only threes";
	//										}
	//									} else if (propertyValue instanceof Double) {
	//										Double requestValue = (Double) propertyValue;
	//										if (requestValue == 3.3 || requestValue == 33.33 || requestValue == 333.333) {
	//											error = "This float field cannot contain only threes";
	//										}
	//									}
	//									if (error != null) {
	//										JSONObject returnProperty = (JSONObject)overrideProperty.clone();
	//										returnProperty.put(Constants.PROPERTY_VALIDATION_CUSTOM_VALIDATION_ERROR, error);
	//										returnProperty.put(Constants.PROPERTY_SYMBOLIC_NAME, requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME));
	//										responseProperties.add(returnProperty);
	//									}
	//								}
	//							}
	//						} 
	//						else
								if (validationType.equals(Constants.PROPERTY_REQUIRED)) {
								// a sample validation that simply requires a value
	//							String symbolicName = overrideProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
								String error = null;
								for (int j = 0; j < requestProperties.size(); j++) {
									JSONObject requestProperty = (JSONObject)requestProperties.get(j);
									String requestPropertySymbolicName = requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME).toString();
									if (requestPropertySymbolicName.contains("[")) { // child component index.. ignore
										requestPropertySymbolicName = requestPropertySymbolicName.substring(0,requestPropertySymbolicName.indexOf("["));
									}
									if (symbolicName.equals(requestPropertySymbolicName)) {
	//									String error = null;
										Object propertyValue = requestProperty.get(Constants.PROPERTY_VALUE);
										if (propertyValue == null) {
											error = "This field requires a value";
										} else if (propertyValue instanceof String && ((String) propertyValue).isEmpty()) {
											error = "This field requires a value";
										} else if (propertyValue instanceof JSONArray) {
											JSONArray jsonArray = (JSONArray) propertyValue;
											// Treat these as empty: [ ], [ null ], [ "" ]
											if (jsonArray.isEmpty()) {
												error = "This field requires a value";
											} else if (jsonArray.size() == 1) {
												Object value = jsonArray.get(0);
												if (value == null || value.toString().isEmpty()) {
													error = "This field requires a value";
												}
											}
										}
										if (error != null) {
											JSONObject returnProperty = (JSONObject)overrideProperty.clone();
											returnProperty.put(Constants.PROPERTY_VALIDATION_CUSTOM_VALIDATION_ERROR, error);
											returnProperty.put(Constants.PROPERTY_SYMBOLIC_NAME, requestProperty.get(Constants.PROPERTY_SYMBOLIC_NAME));
											responseProperties.add(returnProperty);
										}
									}
								}
							}
						} 
						// out of box commented
	//					else if (overrideProperty.containsKey("newObjectValueOverride") && requestMode.equals("finalNewObject")) {
	//						// This is an example of an override for a property when creating a new object.
	//						// The user will see this value after the object is created instead of any value entered for the property.
	//						JSONObject returnProperty = new JSONObject();
	//						returnProperty.put(Constants.PROPERTY_SYMBOLIC_NAME, overrideProperty.get(Constants.PROPERTY_SYMBOLIC_NAME));
	//						returnProperty.put(Constants.PROPERTY_VALUE, overrideProperty.get("newObjectValueOverride"));
	//						responseProperties.add(returnProperty);
	//					} else if (overrideProperty.containsKey("existingObjectValueOverride") && requestMode.equals("finalExistingObject")) {
	//						// This is an example of an override for a property when editing an existing object.
	//						// The user will see this value but it will be saved instead of any value entered for the property.
	//						JSONObject returnProperty = new JSONObject();
	//						returnProperty.put(Constants.PROPERTY_SYMBOLIC_NAME, overrideProperty.get(Constants.PROPERTY_SYMBOLIC_NAME));
	//						returnProperty.put(Constants.PROPERTY_VALUE, overrideProperty.get("existingObjectValueOverride"));
	//						responseProperties.add(returnProperty);
	//					}
//						else if (overrideProperty.containsKey("timestamp")) {
//							String timestampVal = overrideProperty.get("timestamp").toString();
//							if (timestampVal != null && timestampVal.equalsIgnoreCase("true")) {
//								// This is another example of a custom property override.   This
//								// custom property override will fill in a property with the current time.
//								JSONObject returnProperty = new JSONObject();
//								returnProperty.put(Constants.PROPERTY_SYMBOLIC_NAME, overrideProperty.get(Constants.PROPERTY_SYMBOLIC_NAME));
//								returnProperty.put(Constants.PROPERTY_VALUE, (new SimpleDateFormat(Constants.DATE_FORMAT_TIMEZONE)).format(new Date(System.currentTimeMillis())));
//								responseProperties.add(returnProperty);
//							}
//						}
					}
				}
				
				// Send the response json
				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("properties", responseProperties);
				
				// Comment out for better performance when there are large choice lists.
				System.out.println("Response Properties:  "+jsonResponse.serialize());
				
				PrintWriter writer = response.getWriter();
				jsonResponse.serialize(writer);
		
			} catch (Exception e) {
				sendErrorResponse(response, e.getMessage());
			}
		}
		System.out.println("#### EMFCustomEDSService.UpdateObjectTypeServlet END Time #### "+getCurrentDateTime());
	}
	
	/**
	 * Sends a JSON error response.
	 * 
	 * @param response HTTP servlet response
	 * @param errorMessage error message to be logged by the EDS plug-in (not displayed to the user)
	 * @param userMessage error message to be displayed to the user
	 * @throws IOException if an error occurs
	 */
	private void sendErrorResponse(HttpServletResponse response, String errorMessage, String userMessage) throws IOException {
		response.setStatus(500);
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("errorMessage", errorMessage);
		jsonResponse.put("userMessage", userMessage);
		System.out.println("  " + jsonResponse.serialize());
		PrintWriter writer = response.getWriter();
		jsonResponse.serialize(writer);
	}
	
	/**
	 * Sends a JSON error response.
	 * 
	 * @param response HTTP servlet response
	 * @param errorMessage error message to be logged by the EDS plug-in (not displayed to the user)
	 * @throws IOException if an error occurs
	 */
	private void sendErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
		sendErrorResponse(response, errorMessage, null);
	}
	
	private JSONArray  getPropertyData(String objectType, Locale locale) throws IOException {
		// Load the file resource containing the property data for the given object type; first look for a locale specific version
		// (files with names containing special characters like 'ü' may fail to be retrieved, in such cases, replace the characters
		// with '_' or other safe character in both the file resource and the objectType value used to construct the file name bellow)
		// Note for Box users: The objectType of a Box metadata template is its ID, which is composed of the template key and template scope (aka enterprise ID),
		// e.g., "myTemplate,enterise_123456". If you wish to use the same property data for a template that is replicated in multiple enterprises,
		// simply parse the ID on the comma and use the first part (i.e., myTemplate) to retrieve the property data.
		InputStream propertyDataStream = this.getClass().getResourceAsStream(objectType.replace(' ', '_')+"_PropertyData_"+locale.toString()+".json");
		if (propertyDataStream == null) {
			// Look for a locale independent version of the property data
//			System.out.println("getPropertyData objectType=="+ objectType);
			propertyDataStream = this.getClass().getResourceAsStream(objectType.replace(' ', '_')+"_PropertyData.json");
//			System.out.println("getPropertyData propertyDataStream=="+ propertyDataStream);
		}
		JSONArray jsonPropertyData = (propertyDataStream != null)	? JSONArray.parse(propertyDataStream) : new JSONArray();
		return jsonPropertyData;
	}
	
	private static String getCurrentDateTime()
	{
		long yourmilliseconds = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_TIMEZONE);    
		Date resultdate = new Date(yourmilliseconds);
		return sdf.format(resultdate);
		
	}
	
	private static String getTimeStampTime()
	{
		SimpleDateFormat sdf =	new SimpleDateFormat(Constants.DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String  maxDatevalue = sdf.format(new Date()); 
		return maxDatevalue;
	}

	private static String getUerIdFromClientContext(JSONObject  clientContext)
	{
		String userid = "";
		if(clientContext != null && !clientContext.isEmpty())
		{
//			System.out.println("getClientContextProperties size: "+clientContext.size());
			if(clientContext.containsKey(Constants.HTTP_REQUEST_CLIENT_CONTEXT_USER_ID))
			{
				System.out.println("getClientContextProperties userid: "+clientContext.get(Constants.HTTP_REQUEST_CLIENT_CONTEXT_USER_ID));
				userid	=	((String) clientContext.get(Constants.HTTP_REQUEST_CLIENT_CONTEXT_USER_ID)).toUpperCase();
			}
			else System.out.println("getClientContextProperties userid does not exists");
		}
		
		return userid;
	}
	
	private static Object getInitialValue(String requestPropertyName, JSONObject clientContext)
	{
//		System.out.println("Setting initial properties values");
		Object initialValue = "";
		if(requestPropertyName != null && clientContext != null)
		{
			if((requestPropertyName.equalsIgnoreCase(Constants.PROPERTY_SYM_USER_ID)))	initialValue =  getUerIdFromClientContext(clientContext);
			
			if((requestPropertyName.equalsIgnoreCase(Constants.PROPERTY_SYM_TIMESTAMP))) initialValue =  getTimeStampTime();
			// Fill in all date fields with current date & time on load of page
			if((requestPropertyName.contains("DATE"))) initialValue =  getTimeStampTime();
			if((requestPropertyName.equalsIgnoreCase(Constants.PROPERTY_SYM_SRD))) initialValue =  getTimeStampTime();
			if((requestPropertyName.equalsIgnoreCase(Constants.PROPERTY_SYM_NEXT_EXAM__0001))) initialValue =  getTimeStampTime();
			if((requestPropertyName.equalsIgnoreCase(Constants.PROPERTY_SYM_EXPIRATION_0001))) initialValue =  getTimeStampTime();
		}
		return initialValue;
	}
	
	private static String compareDates(Object date1, Object date2)
	{
		String status = "";

		Date newDate1 = parserStringValueToDate(date1.toString());
		Date newDate2 = parserStringValueToDate(date2.toString());
		
		int compareResult 	= newDate2.compareTo(newDate1);
		
		switch (compareResult) {
		case 0:
			status = Constants.COMPARE_DATE_EQUAL;
			break;
		case 1:
			status = Constants.COMPARE_DATE_GREATER;
			break;
		case -1:
			status = Constants.COMPARE_DATE_LESSER;
			break;
		}
		
		System.out.println("dateComparison compareResult:: "+date2 +" "+ status + " than " + date1);
		return status;
	}
	
	
	private static Date parserStringValueToDate (String value) 
	{
		Date date = null;
		String dateFormat	= Constants.DATE_FORMAT;
		
//		if(value != null && value.contains("Z"))
//		{
//			value = value.substring(0, value.length()-1);
//			dateFormat	=	Constants.DATE_FORMAT;
////			System.out.println("new date value: "+value);
//		}
     	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
     	simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//		simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		simpleDateFormat.setLenient(false);
		
		try
		{
			date = simpleDateFormat.parse(value);
		}
		catch (ParseException e)
		{
			System.out.println(e);
		}
		
		return date;
	}
	

}

/*
 * Here is how the JSON being returned is structured:
{
	"externalDataIdentifier" : "<opaque identifier meaningful to service>",
	"properties":
	[
		{
			"symbolicName" : "<symbolic_name>",
			"value" : <potential new value>,
			"customValidationError" : "Description of an invalid reason",
			"customInvalidItems" : [0,3,4,8], // invalid multi-value items
			"displayMode" : "<readonly/readwrite>",
			"required" : <true or false>,
			"hidden" : <true or false>,
			"maxValue" : <overridden max value>,
			"minValue" : <overridden min value>,
			"maxLength" : <underlying max>,
			"format": <regular expression validating the format>,
			"formatDescription": <human readable description of the format>,
			"choiceList" :
			{
				"displayName" : "<display_name>",
				"choices" :
				[
					{
						"displayName" : "<name>"
						"active": <true or false>
						"value" : <value>
					},
					// More choices ...
				]
			}  // Or the special values:
			   //
			   //	Value			Description
			   //	---------		-----------------------------
			   //	"default"		Use class defined choice list (if any). 
			   //	null			Removes the currently assigned choice list.
			   //
			   // When no choice list is used, Navigator will create a choice list from the list of valid values, if valid values are defined.
			"hasDependentProperties" : <true or false>,
		},
		// More properties ...
	]
}
*/





