package util;

public class Constants {
	
//	 * The requestMode has values:
//	 *  initialNewObject -- when a new object is being created (when add doc, create folder, checkin dialogs first appear)
//	 *  initialExistingObject -- when an existing object is being edited (when edit properties first appears)
//	 *  inProgressChanges -- when an object is being modified (for dependent choice lists)
//	 *  finalNewObject -- before the object is persisted (when action is performed on add doc, create folder, checkin)
//	 *  finalExistingObject -- before the existing object is persisted (when save action is performed on edit properties)

	final public static String RESPONSE_MODE_INPROGRESS	=	"inProgressChanges";
	final public static String RESPONSE_MODE_INITIAL_NEW_OBJECT	=	"initialNewObject";
	final public static String RESPONSE_MODE_INITIAL_EXISTING_OBJECT	=	"initialExistingObject";
	final public static String RESPONSE_MODE_FINAL_NEW_OBJECT	=	"finalNewObject";
	final public static String RESPONSE_MODE_FINAL_EXISTING_OBJECT	=	"finalExistingObject";
	final public static String HTTP_REQUEST_MODE	=	"requestMode";
	final public static String HTTP_REQUEST_PROPERTIES	=	"properties";
	final public static String HTTP_REQUEST_CLIENT_CONTEXT	=	"clientContext";
	final public static String HTTP_REQUEST_OBJECT_ID	=	"objectId";
	final public static String HTTP_REQUEST_COOKIE	=	"Cookie";
	final public static String HTTP_REQUEST_CLIENT_CONTEXT_USER_ID	=	"userid";
	final public static String PROPERTY_SYMBOLIC_NAME	=	"symbolicName";
	final public static String PROPERTY_INITIAL_VALUE	=	"initialValue";
	final public static String PROPERTY_VALUE	=	"value";
	final public static String PROPERTY_REQUIRED	=	"Required";
	final public static String PROPERTY_VALIDATE_AS	=	"validateAs";
	final public static String PROPERTY_VALIDATION_AS_NEXT_EXAM_DATE	=	"NextExamDate";
	final public static String PROPERTY_VALIDATION_AS_DATE_INDEXED	=	"DateIndexed";
	final public static String PROPERTY_VALIDATION_AS_ISSERIALNOEXISTS	=	"isSerialNoExists";
	final public static String PROPERTY_VALIDATION_CUSTOM_VALIDATION_ERROR	=	"customValidationError";
	final public static String PROPERTY_DISPLAY_MODE	=	"displayMode";
	final public static String PROPERTY_REQUIRED_SMALL	=	"required";
	
	
	// FileNet property symbolic names
	final public static String PROPERTY_SYM_EXAM_DATE_0001	=	"EXAM_DATE_0001";
	final public static String PROPERTY_SYM_MMSTATUS		=	"MMSTATUS";
	final public static String PROPERTY_SYM_TIMESTAMP		=	"TIMESTAMP";
	final public static String PROPERTY_SYM_DATE_OF_VI_0001	=	"DATE_OF_VI_0001";
	final public static String PROPERTY_SYM_USER_ID			=	"USER_ID";
	final public static String PROPERTY_SYM_LAB_DATE_0001	= "LAB_DATE_0001";
	final public static String PROPERTY_SYM_SRD				= "SRD";
	final public static String PROPERTY_SYM_NEXT_EXAM__0001				= "NEXT_EXAM__0001";
	final public static String PROPERTY_SYM_EXPIRATION_0001				= "EXPIRATION_0001";

	// Error messages to be displayed on ICN UI
	final public static String MESSAGE_NEXT_EXAM_DATE	=	"Next exam date must be greater than exam date";
	final public static String MESSAGE_EXAM_DATE		=	"Exam date must be equal to or less than date indexed";
	final public static String MESSAGE_LAB_DATE			=	"Lab date must be equal to or less than date indexed";
	final public static String MESSAGE_IMM_DATE			=	"Date of immunization must be equal to or less than date indexed";
	final public static String MESSAGE_REL_DATE			=	"Expiration date must be equal to or greater than indexed date";
	final public static String MESSAGE_WC_DATE			=	"Date of Inj-Ill should be equal to or less than Date of Visit";
	final public static String MESSAGE_EMP_MED_FOL			=	"This Employee Medical Folder already exists.";
	
	// FileNet Doc Class symbolic names
	final public static String DOC_CLASS_SYM_WELLNESS_D_0001	=	"ICM_WELLNESS_D_0001";
	final public static String DOC_CLASS_SYM_LAB_REPORT_0001	=	"ICM_LAB_REPORT_0001";
	final public static String DOC_CLASS_SYM_IMMUNIZATI_0001	=	"ICM_IMMUNIZATI_0001";
	final public static String DOC_CLASS_SYM_REL_OF_INF_0001	=	"ICM_REL_OF_INF_0001";
	final public static String DOC_CLASS_SYM_WC_OSHA_DO_0001	=	"ICM_WC_OSHA_DO_0001";
	final public static String DOC_CLASS_SYM_MON_DO_0001		=	"ICM_MED_MON_DO_0001";
	final public static String DOC_CLASS_SYM_EMP_MED_FO_0002		=	"ICM_EMP_MED_FOL_0002";
	
	final public static String DATE_FORMAT_TIMEZONE 			=	"yyyy-MM-dd'T'HH:mm:ss.SSSz";
	final public static String DATE_FORMAT 						=	"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	final public static String COMPARE_DATE_EQUAL 				=	"EQUAL";
	final public static String COMPARE_DATE_LESSER				=	"LESSER";
	final public static String COMPARE_DATE_GREATER				=	"GREATER";
	
	final public static String ICN_DESKTOP_TEST_ID				=	"EMFTest";
	final public static String ICN_DESKTOP_ID					=	"EMF";
	final public static String ICN_ACTION						=	"action";
	final public static String ICN_ACTION_OPENSEARCHTEMPLATE	=	"openSearchTemplate";

	// FileNet constants
	final public static String RESOURCEBUNDLE_NAME = "EMFFileNetConfigurations";
	final public static String CE_URI = "CE_URI";
	final public static String USER_NAME = "USER_NAME";
	final public static String PASSWORD = "PASSWORD";
	final public static String JAAS_MODULE = "JAAS_MODULE";
	final public static String OBJECTSTORE_NAME = "OBJECTSTORE_NAME";
	final public static String DOCUMENTCLASS_NAME = "DOCUMENTCLASS_NAME";
	
	// Search Document Configurations
	final public static String SEARCH_SERIALNO = "SearchSerialNo";
	final public static String SEARCH_SERIALNO_QUERY = "SEARCH_SERIALNO_QUERY";
	final public static String SEARCH_SERIALNO_QUERY_VERSIONSTATUS = "AND VersionStatus=1";
	final public static String ORDERBY = "ORDERBY";
	final public static String PAGE_SIZE = "PAGE_SIZE";	
	
	
	
	
	
	
	
	
	
}
