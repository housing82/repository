package com.universal.code.sql;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.TokenMgrError;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Top;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.CommonUtil;
import com.universal.code.utils.PropertyUtil;
import com.universal.code.utils.RegexUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;
import com.universal.code.utils.TypeUtil;
import com.universal.code.utils.UniqueId4j;

@Component
public class JSQLParser {
	
	private static final Logger logger = LoggerFactory.getLogger(JSQLParser.class);

	@Autowired
	private PropertyUtil propertyUtil;
	
	@Autowired
	private RegexUtil regexUtil;
	
	@Autowired
	private TypeUtil typeUtil;
	
	@Autowired
	private StringUtil stringUtil;
	
	//private TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
	
	//PACKAGE NAME
	private final String PCKG_RELATIONAL = "net.sf.jsqlparser.expression.operators.relational";
	private final String PCKG_ARITHMETIC = "net.sf.jsqlparser.expression.operators.arithmetic";
	private final String PCKG_CONDITIONAL = "net.sf.jsqlparser.expression.operators.conditional";
	private final String PCKG_STMT_SELECT = "net.sf.jsqlparser.statement.select";
	private final String PCKG_OPERATORS = "net.sf.jsqlparser.expression.operators";
	private final String PCKG_STATEMENT = "net.sf.jsqlparser.statement";
	private final String PCKG_SCHEMA = "net.sf.jsqlparser.schema";
	private final String PCKG_EXPRESSION = "net.sf.jsqlparser.expression";
	private final String PCKG_JAVA_UTIL = "java.util";
	
	//CLASS NAME
	private final String CLAS_PLAIN_SELECT = PlainSelect.class.getSimpleName();
	private final String CLAS_LIMIT = Limit.class.getSimpleName();
	private final String CLAS_TOP = Top.class.getSimpleName();
	private final String CLAS_ARRAY_LIST = ArrayList.class.getSimpleName();
	private final String CLAS_JDBC_PARAMETER = JdbcParameter.class.getSimpleName();
	private final String CLAS_ORDERBY_ELEMENT = OrderByElement.class.getSimpleName();
	private final String CLAS_INTEGER = Integer.class.getSimpleName();
	private final String CLAS_STRING = String.class.getSimpleName();
	private final String CLAS_LONG = Long.class.getSimpleName();
	private final String CLAS_NUMBER = Number.class.getSimpleName();
	private final String CLAS_OBJECT = Object.class.getSimpleName();
	
	//FIELD NAME
	private final String FIELD_GROUPBY_REFERENCES = "groupByColumnReferences";
	private final String FIELD_GROUPBY_PARAM = "GroupByParam";
	
	//ATTRIBUTE NAME
	private final String CLASS_NAME = "className"; 
	private final String PACKAGE = "package";
	private final String VALUE_CLASS_NAME = "valueClassName"; 
	private final String VALUE_PACKAGE = "valuePackage";
	private final String LEVEL = "level";
	private final String ORDER = "order";
	private final String PROPERTY_TYPE = "propertyType";
	private final String PROPERTY_NAME = "propertyName";
	private final String PROPERTY_VALUE = "propertyValue";
	private final String GROUP_ID = "gid";
	private final String SUB_GROUP_ID = "subid";
	private final String PASS = "pass";
	
	//data extract type
	private final int EXTRACT_INIT = -1;
	private final int EXTRACT_EQUALS = 0;
	private final int EXTRACT_PATTERN = 1;
	private final int EXTRACT_LOOP_ASC = 2;
	
	private boolean devTest = true;
	
	/** REGEX PATTERN */
	private final String PTN_NAMED_PLUS_PARAMETER = "(([\\s]+)?[+]([\\s]+)?([_a-zA-Z0-9가-힣.]+)([\\s]+)?[+]([\\s]+)?)";
	
	private final String PTN_NAMED_PARAMETER = "(:([_a-zA-Z0-9가-힣]+))|".concat(PTN_NAMED_PLUS_PARAMETER);
	
	//SOURCE FILTER ONLY
	private final String PTN_FILTER_CLASS_NAME = "(?i)(EqualsTo|InExpression|LikeExpression|Function|Between|PlainSelect)";
	//SOURCE DATA EXTRACT
	private final String PTN_EXTRACT_CLASS_NAME = "(?i)(((And|Or|Select)+Expression(Item)?)|Between|PlainSelect)";
	//SOURCE FILTER AND SOURCE DATA EXTRACT  
	private final String PTN_EXTRACT_PROPERTY = "(?i)((((left|right|between|select)(Expression|Item))+(List|Start|End)?)|expression|where|having|groupByColumnReferences|parameters)";
	//"(?i)(((left|right|between)?(Expression|Item)+(List|Start|End)?)|where|having|groupByColumnReferences|parameters)";

	public static final String OUTPUT_KEY = "OUTPUT";
	public static final String INPUT_KEY = "INPUT";
	public static final String TABLE_KEY = "TABLE";
	public static final String STATEMENT = "STATEMENT";
	public static final String WHERE_KEY = "WHERE";
	
	public JSQLParser(){
		
		if(propertyUtil == null || regexUtil == null || typeUtil == null || stringUtil == null) {
			propertyUtil = new PropertyUtil();
			regexUtil = new RegexUtil();
			typeUtil = new TypeUtil();
			stringUtil = new StringUtil();
		}
	}
	
	public Map<String, Object> parseSQL(String sql){
		long startTime = 0L;
		
		if(logger.isDebugEnabled()) {
			logger.debug("[START] parseSQL");
			startTime = SystemUtil.currentTimeMillis();
		}
		
		Map<String, Object> out = null;
		
		try {
			if(StringUtil.isEmpty(sql)) {
				return out;
			}
			else if(sql.trim().startsWith("PBSELECT")) {
				return out;
			}
			/**
			 * ";"으로 구분된 sql여러개 분석시 사용 
			Statements stmt = CCJSqlParserUtil.parseStatements(sql.replaceAll(PTN_NAMED_PARAMETER, "?"));
			for(Statement statement : stmt.getStatements()) {
				parseSQL(statement, sql);
			}
			*/
			
			if(sql.contains("LIMIT")) {
				sql = sql.replaceAll("(?i)AS([\r\n\t 	]+)LIMIT", "AS _LIMIT");
			}
			
			Statement stmt = CCJSqlParserUtil.parse(sql); // .replaceAll(PTN_NAMED_PARAMETER, "?")
			out = parseSQL(stmt, sql);
			
		} catch(TokenMgrError e) {
			e.printStackTrace();
			//throw new ApplicationException(e); // kait pbl sql 분석용 임시 주석
		} catch(JSQLParserException e) {
			e.printStackTrace();
			//throw new ApplicationException(e); // kait pbl sql 분석용 임시 주석
		} catch (Exception e) {
			e.printStackTrace();
			//throw new ApplicationException(e); // kait pbl sql 분석용 임시 주석
		} 
		if(logger.isDebugEnabled()) {
			logger.debug("[END] parseSQL " + SystemUtil.durationMillisecond(startTime));
		}
		
		logger.debug("[Parser Result]\n{}", out);
		return out;
	}
	

	private void parseSubSQL(List<SelectBody> subSelectBodyList, List<Map<String, Object>> readColumns) {
		
		for(SelectBody subSelectBody : subSelectBodyList) {
			
			readColumns.addAll(parseSelect(subSelectBody));
		}
		
	}
	
	private boolean filterKey(List<Map<String, Object>> inputParams, Map<String, Object> inputParam) {
		boolean out = true;
		
		if(inputParams.indexOf(inputParam) > -1) {
			out = false;
		}
		
		return out;
	}
	
	
	private Map<String, Object> parseSQL(Statement stmt, String sql){
		if( logger.isDebugEnabled() ) {
			logger.debug("[START] parseSQL");
		}
		if( sql == null ) {
			throw new ApplicationException("SQL이 존재하지 않습니다.");
		}
		if( stmt == null ) {
			throw new ApplicationException("SQL스테이트먼트가 존재하지 않습니다.");
		}
		
		long startTime = SystemUtil.currentTimeMillis();
		Map<String, Object> inoutColumns = new HashMap<String, Object>();
		List<Map<String, Object>> inputItemList = null;
		
		if( logger.isDebugEnabled() ) {
			logger.debug("#Statement : " + stmt.toString());
		}
		
		/****************** [INPUT START] ******************/
		List<Map<String, Object>> inputParams = new ArrayList<Map<String, Object>>();
		Map<String, Object> inputParam = null;
		for(String param : regexUtil.findPatternToList(sql, PTN_NAMED_PARAMETER)) {
			inputParam = new LinkedHashMap<String, Object>();
			param = param.trim();
			if(regexUtil.testPattern(param, PTN_NAMED_PLUS_PARAMETER)) {
				param = param.substring(param.indexOf("+") + "+".length(), param.lastIndexOf("+")).trim();
			}
			inputParam.put("TYPE", JdbcParameter.class.getSimpleName());
			inputParam.put(param, null);
			if(filterKey(inputParams, inputParam)) {
				inputParams.add(inputParam);
			}
		}
		inoutColumns.put(INPUT_KEY, inputParams);
		
		/****************** [INPUT END] ******************/
		
		//set sql statement type
		inoutColumns.put(STATEMENT, stmt.getClass().getSimpleName());
		
		if( stmt instanceof Select ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#Select Statement ");
			}
			
			List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
			
			List<SelectBody> subSelectBodyList = new ArrayList<SelectBody>(); 
			
			List<Map<String, Object>> readColumns = parseSelect(stmt, subSelectBodyList);
			
			// parseSubSQL 
			parseSubSQL(subSelectBodyList, readColumns);
			
			List<Map<String, Object>> selectTables = new ArrayList<Map<String, Object>>();
			Map<String, Object> selectTable = new LinkedHashMap<String, Object>();
			//[OUTPUT] 추출 완료된 조회 컬럼
			for(Map<String, Object> read : readColumns) {
				
				/*
				if(read.get("ALIAS") != null) {
					columns.add(read.get("ALIAS").toString());
				}
				else {
					if(read.get("COLUMN_NAME") != null) {
						columns.add(read.get("COLUMN_NAME").toString());	
					}
				}
				*/
				if(read.get("TABLE_NAME") != null) {
					
					selectTable = new LinkedHashMap<String, Object>();
					selectTable.put("TABLE_NAME", read.get("TABLE_NAME"));
					selectTable.put("TABLE_ALIAS", read.get("TABLE_ALIAS"));
					selectTable.put("TABLE_ON_EXPR", read.get("TABLE_ON_EXPR"));
					selectTable.put("PARSER_CLASS", read.get("PARSER_CLASS"));
					if(selectTables.indexOf(selectTable) == -1) {
						selectTables.add(selectTable);
					}
				}
				
				columns.add(read);
			}
						
			inoutColumns.put(OUTPUT_KEY, columns);
			inoutColumns.put(TABLE_KEY, selectTables);
		}
		else if( stmt instanceof Insert ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#Insert Statement ");
			}
			Insert insert = (Insert) stmt;
			
			//S. PARSER START
			logger.debug("[INSERT]\n" /*, propertyUtil.out(insert)*/);

			List<Map<String, Object>> insertTables = new ArrayList<Map<String, Object>>();
			Map<String, Object> insertTable = new LinkedHashMap<String, Object>();

			Table table = insert.getTable();
			insertTable.put("TABLE_NAME", table.getName());
			insertTable.put("TABLE_FULLY_QUALIFIED_NAME", table.getFullyQualifiedName());
			if(table.getAlias() != null) {
				insertTable.put("TABLE_ALIAS", table.getAlias().getName());
			}
			logger.debug("[INSERT] insertTable: {}", insertTable);
			insertTables.add(insertTable);
			
			List<Map<String, Object>> insertColumns = new ArrayList<Map<String, Object>>();
			Map<String, Object> insertCol = null;
			
			List<Column> inColumns = insert.getColumns();
			
			for(Column column : inColumns) {
				
				insertCol = new LinkedHashMap<String, Object>();
				insertCol.put("TYPE", column.getClass().getSimpleName());
				insertCol.put("COLUMN_NAME", column.getColumnName());
				insertCol.put("FULLY_QUALIFIED_NAME", column.getFullyQualifiedName()); 
				
				if(column.getTable() != null) {
					insertCol.put("TABLE_SCHEMA_NAME", column.getTable().getSchemaName());
					insertCol.put("TABLE_NAME", column.getTable().getName());
					insertCol.put("TABLE_FULLY_QUALIFIED_NAME", column.getTable().getFullyQualifiedName());
					if(column.getTable().getAlias() != null) {
						insertCol.put("TABLE_ALIAS", column.getTable().getAlias().getName());
					}
					
					if(column.getTable().getName() == null) {
						insertCol.putAll(insertTable);
					}
				}
				else {
					insertCol.putAll(insertTable);
				}
				logger.debug("[INSERT] insertCol: {}", insertCol);
				insertColumns.add(insertCol);
			}
			
			ExpressionList itemsList = (ExpressionList) insert.getItemsList();
			//가 있으면 insertColumns에 담는다. 
			logger.debug("[INSERT] itemsList: {}", itemsList);
			if(itemsList != null && itemsList.getExpressions() != null) {
				
				for(Expression expr : itemsList.getExpressions()) {
					logger.debug("[INSERT] expr: {}, class: {}", expr.toString(), expr.getClass());
					
					insertCol = new LinkedHashMap<String, Object>();
					insertCol.put("TYPE", expr.getClass().getSimpleName());
					insertCol.put("INPUT_NAME", expr.toString());
					
					insertColumns.add(insertCol);
				}
			}
			else {
				if(inoutColumns.get(INPUT_KEY) != null) {

					inputItemList = ((List<Map<String, Object>>) inoutColumns.get(INPUT_KEY));
					insertColumns.addAll(inputItemList);	
				}	
			}
			
			//다른테이블의 데이터를 조회하여 입력할 경우
			Select select = insert.getSelect();
			logger.debug("[INSERT] select: {}", select);
			List<Map<String, Object>> readColumns = new ArrayList<Map<String, Object>>();
			if(select != null) {
				// select가있으면 SelectBody을 추출 하여 분석한다.
				List<Map<String, Object>> readColumnData = new ArrayList<Map<String, Object>>();
				List<SelectBody> subSelectBodyList = (List<SelectBody>) propertyUtil.getObjectList(select, SelectBody.class, new String[]{}, false);
				// parseSubSQL 
				parseSubSQL(subSelectBodyList, readColumnData);
				
				//[OUTPUT] 추출 완료된 조회 컬럼
				for(Map<String, Object> read : readColumnData) {
					readColumns.add(read);
				}
			}

			
			//E. PARSER END
			
			inoutColumns.put(TABLE_KEY, insertTables);
			inoutColumns.put(INPUT_KEY, insertColumns);	
			inoutColumns.put(OUTPUT_KEY, readColumns);
		}
		else if( stmt instanceof Update ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#Update Statement ");
			}
			Update update = (Update) stmt;

			List<Map<String, Object>> updateTables = new ArrayList<Map<String, Object>>();
			Map<String, Object> updateTable = new LinkedHashMap<String, Object>();
			List<Map<String, Object>> updateColumns = new ArrayList<Map<String, Object>>();
			Map<String, Object> updateCol = null;
			List<Map<String, Object>> readColumns = new ArrayList<Map<String, Object>>();
			Map<String, Object> readCol = null;
			String tableAlais = null;
			String columnTableAlais = null;
			
			
			logger.debug("[UPDATE] getTables: {}", update.getTables());
			for(Table table : update.getTables()) {
				updateTable = new LinkedHashMap<String, Object>();
				updateTable.put("TABLE_NAME", table.getName());
				updateTable.put("TABLE_FULLY_QUALIFIED_NAME", table.getFullyQualifiedName());
				if(table.getAlias() != null) {
					updateTable.put("TABLE_ALIAS", table.getAlias().getName());
				}
				
				updateTables.add(updateTable);
			}
			
			logger.debug("[UPDATE] getFromItem: {}", update.getFromItem());
			if(update.getFromItem() != null) {
				
				updateTable = new LinkedHashMap<String, Object>();
				updateTable.put("TABLE_NAME", update.getFromItem().toString());
				updateTable.put("TABLE_FULLY_QUALIFIED_NAME", update.getFromItem().toString());
				if(update.getFromItem().getAlias() != null) {
					updateTable.put("TABLE_ALIAS", update.getFromItem().getAlias().getName());
				}
				
				updateTables.add(updateTable);
			}
			
			logger.debug("[UPDATE] getJoins: {}", update.getJoins());
			if(update.getJoins() != null) {
				
				for(Join join : update.getJoins()) {
					
					updateTable = new LinkedHashMap<String, Object>();
					updateTable.put("TABLE_NAME", join.getRightItem().toString());
					updateTable.put("TABLE_ALIAS", (join.getRightItem().getAlias() != null ? join.getRightItem().getAlias().getName() : ""));
					updateTable.put("TABLE_ON_EXPR", (join.getOnExpression() != null ? join.getOnExpression().toString() : ""));
					updateTable.put("PARSER_CLASS", join.getRightItem().getClass().getCanonicalName());
					updateTables.add(updateTable);

				}
			}
			
			logger.debug("[UPDATE] getColumns: {}", update.getColumns());
			List<Column> updColumns = update.getColumns();
			
			for(Column column : updColumns) {
				
				updateCol = new LinkedHashMap<String, Object>();
				updateCol.put("TYPE", column.getClass().getSimpleName());
				updateCol.put("COLUMN_NAME", column.getColumnName());
				updateCol.put("FULLY_QUALIFIED_NAME", column.getFullyQualifiedName()); 
				
				logger.debug("[UPDATE_COL_INFO]\n{}", propertyUtil.out(column));
				
				// SQL: UPDATE 대상 컬럼에 테이블 정보가 있을경우
				if(column.getTable() != null) {
					updateCol.put("TABLE_SCHEMA_NAME", column.getTable().getSchemaName());
					updateCol.put("TABLE_NAME", column.getTable().getName());
					updateCol.put("TABLE_FULLY_QUALIFIED_NAME", column.getTable().getFullyQualifiedName());
					if(column.getTable().getAlias() != null) {
						updateCol.put("TABLE_ALIAS", column.getTable().getAlias().getName());
					}
					
					if(column.getTable().getName() == null) {
						if(updateTables != null && updateTables.size() == 1) {
							updateCol.putAll(updateTables.get(0));
						}
						else {
							for(Map<String, Object> table : updateTables) {
								tableAlais = StringUtil.NVL((String) table.get("TABLE_ALIAS"));
								columnTableAlais = StringUtil.NVL((String) updateCol.get("TABLE_ALIAS"));
								
								if(tableAlais.equals(columnTableAlais)) {
									updateCol.putAll(table);
									break;
								}
							}
						}
					}
				}
				else {
					// SQL: UPDATE 대상 컬럼에 테이블 정보가 없을경우
					if(updateTables != null && updateTables.size() == 1) {
						updateCol.putAll(updateTables.get(0));
					}
					else {
						for(Map<String, Object> table : updateTables) {
							tableAlais = StringUtil.NVL((String) table.get("TABLE_ALIAS"));
							columnTableAlais = StringUtil.NVL((String) updateCol.get("TABLE_ALIAS"));
							
							if(tableAlais.equals(columnTableAlais)) {
								updateCol.putAll(table);
								break;
							}
						}
					}
				}
				logger.debug("[UPDATE] updateCol: {}", updateCol);
				updateColumns.add(updateCol);
			}
			
			if(inoutColumns.get(INPUT_KEY) != null) {

				inputItemList = ((List<Map<String, Object>>) inoutColumns.get(INPUT_KEY));
				updateColumns.addAll(inputItemList);	
			}	
			
			logger.debug("[UPDATE] getSelect: {}", update.getSelect());
			Select select = update.getSelect();
			if(select != null) {
				// select가있으면 SelectBody을 추출 하여 분석한다.
				List<Map<String, Object>> readColumnData = new ArrayList<Map<String, Object>>();
				List<SelectBody> subSelectBodyList = (List<SelectBody>) propertyUtil.getObjectList(select, SelectBody.class, new String[]{}, false);
				// parseSubSQL 
				parseSubSQL(subSelectBodyList, readColumnData);
				
				//[OUTPUT] 추출 완료된 조회 컬럼
				for(Map<String, Object> read : readColumnData) {
					readColumns.add(read);
				}
			}
			
			logger.debug("[UPDATE] getWhere: {}", update.getWhere());
			Expression where = update.getWhere();
			if(where != null) {
				// select가있으면 SelectBody을 추출 하여 분석한다.
				List<Map<String, Object>> readColumnData = new ArrayList<Map<String, Object>>();
				List<SelectBody> subSelectBodyList = (List<SelectBody>) propertyUtil.getObjectList(where, SelectBody.class, new String[]{}, false);
				// parseSubSQL 
				parseSubSQL(subSelectBodyList, readColumnData);
				
				//[OUTPUT] 추출 완료된 조회 컬럼
				for(Map<String, Object> read : readColumnData) {
					readColumns.add(read);
				}
			}
			
			
			inoutColumns.put(TABLE_KEY, updateTables);
			inoutColumns.put(INPUT_KEY, updateColumns);
			inoutColumns.put(OUTPUT_KEY, readColumns);
			inoutColumns.put(WHERE_KEY, (update.getWhere() != null ? update.getWhere().toString() : ""));
			
		}
		else if( stmt instanceof Delete ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#Delete Statement ");
			}
			Delete delete = (Delete) stmt;

			List<Map<String, Object>> deleteTables = new ArrayList<Map<String, Object>>();
			Map<String, Object> deleteTable = new LinkedHashMap<String, Object>();
			List<Map<String, Object>> deleteParams = new ArrayList<Map<String, Object>>();
			Map<String, Object> deleteParam = null;
			List<Map<String, Object>> readColumns = new ArrayList<Map<String, Object>>();
			Map<String, Object> readCol = null;
			
			Table table = delete.getTable();
			deleteTable.put("TABLE_NAME", table.getName());
			deleteTable.put("TABLE_FULLY_QUALIFIED_NAME", table.getFullyQualifiedName());
			if(table.getAlias() != null) {
				deleteTable.put("TABLE_ALIAS", table.getAlias().getName());
			}
			logger.debug("[DELETE] deleteTable: {}", deleteTable);
			deleteTables.add(deleteTable);
	
			Expression where = delete.getWhere();
			if(where != null) {
				
				// select가있으면 SelectBody을 추출 하여 분석한다.
				List<Map<String, Object>> readColumnData = new ArrayList<Map<String, Object>>();
				List<SelectBody> subSelectBodyList = (List<SelectBody>) propertyUtil.getObjectList(where, SelectBody.class, new String[]{}, false);
				// parseSubSQL 
				parseSubSQL(subSelectBodyList, readColumnData);
				
				//[OUTPUT] 추출 완료된 조회 컬럼
				for(Map<String, Object> read : readColumnData) {
					readColumns.add(read);
				}
			}
			
			inoutColumns.put(TABLE_KEY, deleteTables);
			inoutColumns.put(OUTPUT_KEY, readColumns);
			inoutColumns.put(WHERE_KEY, (delete.getWhere() != null ? delete.getWhere().toString() : ""));
		}
		else if( stmt instanceof Merge ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#Merge Statement ");
			}
			Merge merge = (Merge) stmt;
	
			List<Map<String, Object>> mergeTables = new ArrayList<Map<String, Object>>();
			Map<String, Object> mergeTable = new LinkedHashMap<String, Object>();
			List<Map<String, Object>> mergeColumns = new ArrayList<Map<String, Object>>();
			Map<String, Object> mergeCol = null;
			List<Map<String, Object>> readColumns = new ArrayList<Map<String, Object>>();
			Map<String, Object> readCol = null;
			
			/*
			merge.getTable()
			
			merge.getOnCondition()
			
			merge.getUsingTable()
			merge.getUsingAlias()
			merge.getUsingSelect()
			
			merge.getMergeInsert()
			
			merge.getMergeUpdate()
			*/
			
			
			inoutColumns.put(TABLE_KEY, mergeTables);
			inoutColumns.put(INPUT_KEY, mergeColumns);
			inoutColumns.put(OUTPUT_KEY, readColumns);
		}
		else if( stmt instanceof CreateTable ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#CreateTable Statement ");
			}
			CreateTable createTable = (CreateTable) stmt;

		}
		else if( stmt instanceof CreateView ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#CreateView Statement ");
			}
			CreateView createView = (CreateView) stmt;

		}
		else if( stmt instanceof CreateIndex ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#CreateIndex Statement ");
			}
			CreateIndex createIndex = (CreateIndex) stmt;

		}
		else if( stmt instanceof Drop ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#Drop Statement ");
			}
			Drop drop = (Drop) stmt;

		}
		else if( stmt instanceof Alter ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#Alter Statement ");
			}
			Alter alter = (Alter) stmt;

		}
		else if( stmt instanceof Replace ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#Replace Statement ");
			}
			Replace replace = (Replace) stmt;

		}
		else if( stmt instanceof Execute ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#Execute Statement ");
			}
			Execute execute = (Execute) stmt;

		}
		else if( stmt instanceof Truncate ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("#Truncate Statement ");
			}
			Truncate truncate = (Truncate) stmt;
			
		}
		else {
			throw new ApplicationException("지원하지 않는 SQL 스테이트먼트입니다.["+stmt+"]");
		}
		
		if( logger.isDebugEnabled() ) {
			logger.debug("[END] parseSQL " + SystemUtil.durationMillisecond(startTime));
		}
		
		return inoutColumns;
	}
	
	/*	
	private List<Map<String, Object>> extractSQLExpression(Object plain, int level){
		return extractSQLExpression(plain, level, null, null);
	}
	
	private List<Map<String, Object>> extractSQLExpression(Object plain, int level, String... excludeMethods){
		return extractSQLExpression(plain, level, null, null, excludeMethods);
	}
 	*/
	
	private List<Map<String, Object>> extractSQLExpression(Object plain, int level, String prntGroupId, String prntSubGroupId, String... excludeMethods){
		List<String> excludeNames = propertyUtil.getExcludeNames(excludeMethods);
		return extractSQLExpression(plain, level, prntGroupId, prntSubGroupId, excludeNames);
	}
	/**
	 * Extract Bean To Map
	 * @param whereExpr
	 * @param excludeMethods
	 * @param recallCnt
	 * @return
	 */
	private List<Map<String, Object>> extractSQLExpression(Object plain, int level, String prntGroupId, String prntSubGroupId, List<String> excludeNames){
		List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
		Map<String, Object> linkedMap = null;
		
		String propertyName = "";
		Class<?> propertyType = null;
		Object propertyValue = null;
		PropertyDescriptor property = null;
		String groupId = prntGroupId;
		String subGroupId = null; //prntSubGroupId;
		
		if(plain != null) {
			PropertyDescriptor propertyDescriptors[] = PropertyUtils.getPropertyDescriptors(plain);

        	for(int i = 0; i < propertyDescriptors.length; i++){
            	
            	property = propertyDescriptors[i];

            	//프로퍼티 이름
            	propertyName = property.getName();
            	
            	if( property.getPropertyType() == null ) {
            		if(devTest && logger.isDebugEnabled()) {
						logger.debug(CommonUtil.addString("\n  X [ property name ] " , propertyName , " PropertyType is NULL"));
            		}
            		continue;
            	}
            	
            	if(excludeNames != null && excludeNames.indexOf(propertyName) > -1) {
            		//제외 메소드일경우 컨티뉴
            		continue;
            	}
            	
            	//프로퍼티 타입
            	propertyType = property.getPropertyType();
            	
            	try {
            		if( property.getReadMethod() != null) {
            			propertyValue = PropertyUtils.getProperty(plain, propertyName);
            		}
            		else {
            			propertyValue = " ※ There is no read method. ";
            		}
				} catch (IllegalAccessException e) {
					throw new ApplicationException(e);
				} catch (InvocationTargetException e) {
					throw new ApplicationException(e);
				} catch (NoSuchMethodException e) {
					throw new ApplicationException(e);
				}
            	
            	//save object data start
            	String packagName = plain.getClass().getPackage().getName();
            	String className = plain.getClass().getSimpleName();
            	String valuePackageName = (propertyValue != null ? propertyValue.getClass().getPackage().getName() : "");
            	String valueClassName = (propertyValue != null ? propertyValue.getClass().getSimpleName() : "");
            	//String valuePackageName = propertyType != null ? propertyType.getPackage().toString() : "";
            	//String valueClassName = propertyType != null ? propertyType.getSimpleName() : "";

            	if(i == 0) {
            		subGroupId = UniqueId4j.getRandomUUID();
            	}
            	
            	if((packagName.equals(PCKG_STMT_SELECT) && className.equals(CLAS_PLAIN_SELECT)) || packagName.equals(PCKG_CONDITIONAL)) {
            		
					if (valuePackageName.equals(PCKG_RELATIONAL)
						|| (valuePackageName.equals(PCKG_JAVA_UTIL) && valueClassName.equals(CLAS_ARRAY_LIST))
						|| valuePackageName.equals(PCKG_EXPRESSION)
						|| valuePackageName.equals(PCKG_SCHEMA)
						|| valuePackageName.equals(PCKG_STMT_SELECT)
					) {
        				groupId = className.concat("-").concat(UniqueId4j.getRandomUUID());
        			}
        			else {
        				groupId = null;
        			}
            	}
            	
            	linkedMap = new LinkedHashMap<String, Object>();
            	
            	linkedMap.put(CLASS_NAME, className);
            	linkedMap.put(LEVEL, level);
            	linkedMap.put(ORDER, i);
            	linkedMap.put(PROPERTY_NAME, propertyName);
            	linkedMap.put(PROPERTY_VALUE, propertyValue);
            	linkedMap.put(PROPERTY_TYPE, propertyType.getSimpleName());            	
            	linkedMap.put(PACKAGE, packagName);
            	linkedMap.put(VALUE_PACKAGE, valuePackageName);
            	linkedMap.put(VALUE_CLASS_NAME, valueClassName);
            	linkedMap.put(GROUP_ID, groupId);
            	linkedMap.put(SUB_GROUP_ID, subGroupId);
            	
            	out.add(linkedMap);
            	
            	if(propertyValue != null && Collection.class.isAssignableFrom(property.getPropertyType()) && Collection.class.isAssignableFrom(propertyValue.getClass())) {
    				//if(logger.isDebugEnabled()) {
    				//	logger.debug(CommonUtil.mergeObjectString(new Object[]{" propertyValue is collection ParameterizedType!! "}));
    				//}
    				out.addAll(getDataTransferCollectionObjectContents(propertyValue, level, groupId, subGroupId, excludeNames));
            	}
            	else if(!typeUtil.isGeneralType(property.getPropertyType())) {
            		//if(logger.isDebugEnabled()) {
    				//	logger.debug(CommonUtil.mergeObjectString(new Object[]{" propertyValue is not GeneralType!! "}));
    				//}
            		out.addAll(getDataTransferObjectContents(property, propertyValue, level, groupId, subGroupId, excludeNames));
            	}
            }
		}
		
		return out;
	}
	
	/**
	 * Extract Bean To Map
	 * @param property
	 * @param propertyValue
	 * @param excludeMethods
	 * @param isLogging
	 * @param stacks
	 * @param level
	 * @return
	 */
    private List<Map<String, Object>> getDataTransferObjectContents(PropertyDescriptor property, Object propertyValue, int level, String prntGroupId, String prntSubGroupId, List<String> excludeNames) {
    	List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
    	
    	if( propertyValue != null && typeUtil.isSupportedReferenceType(propertyValue.getClass().getCanonicalName()) ) {
    		//message.append(SystemUtil.LINE_SEPARATOR);
    		out.addAll(extractSQLExpression(propertyValue, level + 1, prntGroupId, prntSubGroupId, excludeNames));
    	}
    	
    	return out;
    }
    
    /**
     * Extract Bean To Map
     * @param propertyValue
     * @param excludeMethods
     * @param isLogging
     * @param stacks
     * @param level
     * @return
     */
    @SuppressWarnings("rawtypes")
	private List<Map<String, Object>> getDataTransferCollectionObjectContents(Object propertyValue, int level, String prntGroupId, String prntSubGroupId, List<String> excludeNames) {
    	
    	List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
    	
		for(Object collectionValues : (Collection) propertyValue) {
			
			if( collectionValues instanceof ParameterizedType ) {
				if(logger.isDebugEnabled()) {
					//logger.debug(CommonUtil.mergeObjectString(new Object[]{" collectionValues is ParameterizedType!! < recall getDataTransferCollectionObjectContents [ ", collectionValues.getClass().getCanonicalName(), " ] >"}));
				}
				out.addAll(getDataTransferCollectionObjectContents(collectionValues, level + 1, prntGroupId, prntSubGroupId, excludeNames));
			}
			else if( /*typeUtil.isSupportedReferenceType(collectionValues.getClass().getCanonicalName()) &&*/
				!(collectionValues instanceof WildcardType) 
				&& !collectionValues.getClass().isPrimitive()
				&& !typeUtil.isPrimitiveWrapType(collectionValues.getClass())
			) {
				if(logger.isDebugEnabled()) {
					//logger.debug(CommonUtil.mergeObjectString(new Object[]{" collectionValues is self object < recall out [ ", collectionValues.getClass().getCanonicalName(), " ] >"}));
				}
				// user self dto 이면 제귀 호출
	    		//message.append(SystemUtil.LINE_SEPARATOR);
	    		out.addAll(extractSQLExpression(collectionValues, level + 1, prntGroupId, prntSubGroupId, excludeNames));
			}
		}
		
		return out;
    }
    
	private void addSetOperationList(List<Map<String, Object>> readColumns, SelectBody selectBody, List<SelectBody> subSelectBodyList) {
		logger.debug("[START] addSetOperationList");
		if(!SetOperationList.class.isAssignableFrom(selectBody.getClass())) {
			throw new ApplicationException("SelectBody가 SetOperationList구현체가 아닙니다.");
		}
		
		PlainSelect plainSelect = null;
		List<Map<String, Object>> readColumnList = null;
		SetOperationList setOperationList = (SetOperationList) selectBody;
		
		for(SelectBody selectBodys : setOperationList.getSelects()) {
			if(SetOperationList.class.isAssignableFrom(selectBodys.getClass())) {
				logger.debug("[RECALL] addSetOperationList");
				addSetOperationList(readColumns, selectBodys, subSelectBodyList);
			}
			else {
				logger.debug("[EXTRACT] addSetOperationList");
				plainSelect = (PlainSelect) selectBodys;
				
				//[OUTPUT] SelectItems Only Read Column
				readColumnList = extractReadColumns(plainSelect.getSelectItems());
				
				extractSqlInfo(plainSelect, readColumnList, subSelectBodyList);
				
				readColumns.addAll(readColumnList);
			}
		}
		logger.debug("[END] addSetOperationList size: {}", readColumns.size());
	}

    private List<Map<String, Object>> parseSelect(Object stmt){
    	return parseSelect(stmt, null);
    }
	
	private List<Map<String, Object>> parseSelect(Object stmt, List<SelectBody> subSelectBodyList){
		
		List<Map<String, Object>> readColumns = null;
		//List<Map<String, Object>> readColumnList = null;
		
		
		try {
			long startTime = SystemUtil.currentTimeMillis();
			
			/****************** [OUTPUT START] ******************/
			SelectBody selectBody = null;
			
			if(Statement.class.isAssignableFrom(stmt.getClass())) {
				selectBody = ((Select) stmt).getSelectBody();
			}
			else if(SelectBody.class.isAssignableFrom(stmt.getClass())) {
				selectBody = (SelectBody) stmt;
			}
			
			logger.debug("#selectBody: {}", selectBody.getClass());
			PlainSelect plainSelect = null;
			//SetOperationList setOperationList = null;
			
			/*
			plainSelect.getOrderByElements();
			plainSelect.getWhere()
			plainSelect.getFromItem()
			plainSelect.getForUpdateTable()
			plainSelect.getIntoTables()
			plainSelect.getJoins()
			plainSelect.getOracleHierarchical()
			*/
			
			if(SetOperationList.class.isAssignableFrom(selectBody.getClass())) {
				readColumns = new ArrayList<Map<String, Object>>();
				addSetOperationList(readColumns, selectBody, subSelectBodyList);
				
//				setOperationList = (SetOperationList) selectBody;
//				readColumns = new ArrayList<Map<String, Object>>();
//				for(SelectBody selectBodys : setOperationList.getSelects()) {
//					if(SetOperationList.class.isAssignableFrom(selectBodys.getClass())) {
//						(SetOperationList) selectBodys;
//					}
//					else {
//						plainSelect = (PlainSelect) selectBodys;
//					}
//					
//					//[OUTPUT] SelectItems Only Read Column
//					readColumnList = extractReadColumns(plainSelect.getSelectItems());
//					
//					extractSqlInfo(plainSelect, readColumnList, subSelectBodyList);
//					
//					readColumns.addAll(readColumnList);
//				}
			}
			else {
				plainSelect = (PlainSelect) selectBody;

				//[OUTPUT] SelectItems Only Read Column
				readColumns = extractReadColumns(plainSelect.getSelectItems());
				
				extractSqlInfo(plainSelect, readColumns, subSelectBodyList);
			}

			if(readColumns == null || readColumns.size() == 0) {
				throw new ApplicationException("조회 컬럼이 존재하지 않습니다.");
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug("extractReadColumns laptime : {}", SystemUtil.durationMillisecond(startTime));
			}
			
			
			/****************** [OUTPUT END] ******************/
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ApplicationException(e);
		}
		
		return readColumns;
	}

	private void extractSqlInfo(PlainSelect plainSelect, List<Map<String, Object>> readColumns, List<SelectBody> subSelectBodyList) {
		
		Map<String, String> tableMap = null;
		List<Map<String, String>> tableList = new ArrayList<Map<String, String>>();
		
		tableMap = new LinkedHashMap<String, String>();
		tableMap.put("TABLE_NAME", plainSelect.getFromItem().toString());
		tableMap.put("TABLE_ALIAS", (plainSelect.getFromItem().getAlias() != null ? plainSelect.getFromItem().getAlias().getName() : ""));
		tableMap.put("TABLE_ON_EXPR", "");
		tableMap.put("PARSER_CLASS", plainSelect.getFromItem().getClass().getCanonicalName());
		
		// PlainSelect의 하위에 존재하는 SelectBody를 추출한다.
		if(subSelectBodyList != null) {
			subSelectBodyList.addAll((List<SelectBody>) propertyUtil.getObjectList(plainSelect, SelectBody.class, new String[]{}, false));
		}

		logger.debug("tableMap: {}", tableMap);
		tableList.add(tableMap);

		if(plainSelect.getJoins() != null) {
			
			for(Join join : plainSelect.getJoins()) {
				
				tableMap = new LinkedHashMap<String, String>();
				tableMap.put("TABLE_NAME", join.getRightItem().toString());
				tableMap.put("TABLE_ALIAS", (join.getRightItem().getAlias() != null ? join.getRightItem().getAlias().getName() : ""));
				tableMap.put("TABLE_ON_EXPR", (join.getOnExpression() != null ? join.getOnExpression().toString() : ""));
				tableMap.put("PARSER_CLASS", join.getRightItem().getClass().getCanonicalName());
				tableList.add(tableMap);

				logger.debug("tableMap: {}", tableMap);
			}
		}
		
		/*
		logger.debug("+getSelectItems StringList => {}", plainSelect.getStringList(plainSelect.getSelectItems(), true, false));
		logger.debug("+getGroupByColumnReferences StringList => {}", plainSelect.getStringList(plainSelect.getGroupByColumnReferences(), true, false));
		logger.debug("+getOrderByElements StringList => {}", plainSelect.getStringList(plainSelect.getOrderByElements(), true, false));
		logger.debug("+getJoins StringList => {}", plainSelect.getStringList(plainSelect.getJoins(), true, false));
		
		logger.debug("getFromItem: {}", propertyUtil.out(plainSelect.getFromItem()));
		if(plainSelect.getJoins() != null) {
			for(Join join : plainSelect.getJoins()) {
				logger.debug("getJoins: {}", propertyUtil.out(join));
			}
		}
		
		logger.debug("getWhere: {}", propertyUtil.out(plainSelect.getWhere()));
		logger.debug("getIntoTables: {}", propertyUtil.out(plainSelect.getIntoTables()));
		logger.debug("getOrderByElements: {}", propertyUtil.out(plainSelect.getOrderByElements()));
		logger.debug("getForUpdateTable: {}", propertyUtil.out(plainSelect.getForUpdateTable()));
		logger.debug("getOracleHierarchical: {}", propertyUtil.out(plainSelect.getOracleHierarchical()));
		*/
		
		Map<String, String> singleTable = null;
		String columnTableName = null;
		for(Map<String, Object> readColumn : readColumns) {
			columnTableName = StringUtil.NVL((String) readColumn.get("TABLE_NAME"));
			// FIND COLUMN'S REAL TABLE
			if(tableList.size() == 1) {
				singleTable = tableList.get(0);
				columnTableName = singleTable.get("TABLE_NAME");
				if(columnTableName.contains(" ")) {
					columnTableName = columnTableName.substring(0, columnTableName.indexOf(" "));
					singleTable.put("TABLE_NAME", columnTableName);
				}
				readColumn.putAll(singleTable);
			}
			else {
				for(Map<String, String> table : tableList) {
					
					if(columnTableName.equalsIgnoreCase(StringUtil.NVL((String) table.get("TABLE_ALIAS")))) {
						columnTableName = table.get("TABLE_NAME");
						if(columnTableName.contains(" ")) {
							columnTableName = columnTableName.substring(0, columnTableName.indexOf(" "));
							table.put("TABLE_NAME", columnTableName);
						}
						readColumn.putAll(table);
					}
				}
			}
			
			logger.debug("readColumn: {}", readColumn);
		}
	}

	
	private List<Map<String, Object>> extractReadColumns(List<SelectItem> selectItems){
		if(logger.isDebugEnabled()) {
			logger.debug("[START] extractReadColumns");
		}
		
		List<Map<String, Object>> readCols = new ArrayList<Map<String, Object>>();
		Map<String, Object> readCol = null;
		//logger StringBuilder
		StringBuilder loggerStrb = null;
				
		SelectExpressionItem selectExpr = null;
		AllTableColumns allTableColumnExpr = null;
		AllColumns allColumnExpr = null;
		Expression expr = null;
		Alias alias = null;
		
		boolean devTest = false;
		if( selectItems != null ) {
			
			for(SelectItem selectItem : selectItems ) {
				
				readCol = new LinkedHashMap<String, Object>();
				
				if(SelectExpressionItem.class.isAssignableFrom(selectItem.getClass())) {
					selectExpr = (SelectExpressionItem) selectItem;
					expr = selectExpr.getExpression();
					alias = selectExpr.getAlias();
					
					if(logger.isDebugEnabled() && devTest) {
						logger.debug("* SelectExpressionItem:\n{} ", propertyUtil.out(selectExpr));
					}
					
					readCol.put("TYPE", expr.getClass().getSimpleName());
					
					if(alias != null) {
						if(logger.isDebugEnabled() && devTest) {
							loggerStrb = new StringBuilder();
							loggerStrb.append("- as getName : " + alias.getName());
							loggerStrb.append(", isUseAs : " + alias.isUseAs());
							logger.debug(loggerStrb.toString());
						}
						
						readCol.put("ALIAS", alias.getName());
						readCol.put("ALIAS_USEAS", alias.isUseAs());
					}
					else {
						
						if( readCol.get("TYPE").equals(SubSelect.class.getSimpleName()) ) {
							
							readCol.put("ALIAS", "subSelectNoAlias");
							readCol.put("ALIAS_USEAS", false);
							
							//clearMap(readCol);
							//clearList(readCols);
							//throw new ApplicationException("조회컬럼의 서브셀렉트(SubSelect)절의 별칭(Alias)은 필수 사항입니다. 별칭(Alias)를 작성하세요");
						}
						else if( readCol.get("TYPE").equals(Function.class.getSimpleName()) ) {
							
							readCol.put("ALIAS", ((Function) expr).toString());
							readCol.put("ALIAS_USEAS", false);
							
							//clearMap(readCol);
							//clearList(readCols);
							//throw new ApplicationException("조회컬럼의 함수(Function)절의 별칭(Alias)은 필수 사항입니다. 별칭(Alias)를 작성하세요");
						}
						else if( readCol.get("TYPE").equals(JdbcParameter.class.getSimpleName()) ) {
							clearMap(readCol);
							clearList(readCols);
							throw new ApplicationException("조회컬럼의 파라메터(JdbcParameter)절의 별칭(Alias)은 필수 사항입니다. 별칭(Alias)를 작성하세요");
						}
					}
					
					if(Function.class.isAssignableFrom(expr.getClass())) {
						if(logger.isDebugEnabled() && devTest) {
							loggerStrb = new StringBuilder();
							loggerStrb.append("- fn isAllColumns : " + ((Function) expr).isAllColumns());
							loggerStrb.append(", getName : " + ((Function) expr).getName());
							loggerStrb.append(", isEscaped : " + ((Function) expr).isEscaped());
							loggerStrb.append(", getKeep : " + ((Function) expr).getKeep());
							loggerStrb.append(", isDistinct : " + ((Function) expr).isDistinct());
							loggerStrb.append(", getParameters : " + ((Function) expr).getParameters());
							logger.debug(loggerStrb.toString());
						}
						
						readCol.put("FUNCTION_NAME", ((Function) expr).getName());
						readCol.put("FUNCTION_PARAM", ((Function) expr).getParameters());
					}
					else if(Column.class.isAssignableFrom(expr.getClass())) {
						if(logger.isDebugEnabled() && devTest) {
							loggerStrb = new StringBuilder();
							loggerStrb.append("- col getColumnName : " + ((Column) expr).getColumnName());
							loggerStrb.append(", getFullyQualifiedName : " + ((Column) expr).getFullyQualifiedName());
							loggerStrb.append(", getTable : " + ((Column) expr).getTable());
							loggerStrb.append(", getASTNode : " + ((Column) expr).getASTNode());
							logger.debug(loggerStrb.toString());
						}
						
						readCol.put("COLUMN_NAME", ((Column) expr).getColumnName());
						readCol.put("FULLY_QUALIFIED_NAME", ((Column) expr).getFullyQualifiedName()); 
						
						if(((Column) expr).getTable() != null) {
							readCol.put("TABLE_SCHEMA_NAME", ((Column) expr).getTable().getSchemaName());
							readCol.put("TABLE_NAME", ((Column) expr).getTable().getName());
							readCol.put("TABLE_FULLY_QUALIFIED_NAME", ((Column) expr).getTable().getFullyQualifiedName());
						}
					}
					else if(JdbcParameter.class.isAssignableFrom(expr.getClass())) {
						readCol.put("JDBC_INDEX", ((JdbcParameter) expr).getIndex());
					}
					else if(SubSelect.class.isAssignableFrom(expr.getClass())) {
						readCol.put("SUB_SELECT", ((SubSelect) expr).getSelectBody().toString());
					}
					else {
						readCol.put("ETC_EXPR", expr.toString());
					}
					
					readCol.put("SOURCE", selectExpr.toString());
				}
				else if(AllTableColumns.class.isAssignableFrom(selectItem.getClass())){
					allTableColumnExpr = (AllTableColumns) selectItem;
					if(logger.isDebugEnabled()) {
						logger.debug(" SelectItem::::AllTableColumns:\n" + propertyUtil.out(selectItem));
					}
					readCol.put("TYPE", allTableColumnExpr.getClass().getSimpleName());
					readCol.put("COLUMN_NAME", allTableColumnExpr.toString());
					
					clearMap(readCol);
					clearList(readCols);
					
					// kait pbl sql 분석용 임시 주석
					/*
					throw new ApplicationException(new StringBuilder()
						.append("오퍼레이션 항목과 컬럼간의 데이터 맵핑을 위하여 조회 컬럼정의는 필수 사항입니다.")
						.append(SystemUtil.LINE_SEPARATOR)
						.append("SQL에 테이블 \"")
						.append(allTableColumnExpr.getTable().getName())
						.append("\"의 조회 컬럼명을 작성하세요.")
						.append(SystemUtil.LINE_SEPARATOR)
						.append("[*]의 사용은 조인된 테이블에 따라 같은 컬럼명이 존재할수 있음으로 허용하지 않습니다.")
						.toString());
					*/
				}
				else if(AllColumns.class.isAssignableFrom(selectItem.getClass())) {
					allColumnExpr = (AllColumns) selectItem;
					if(logger.isDebugEnabled()) {
						logger.debug(" SelectItem::::AllColumns:\n" + propertyUtil.out(selectItem));
					}
					readCol.put("TYPE", allColumnExpr.getClass().getSimpleName());
					readCol.put("COLUMN_NAME", allColumnExpr.toString());
					//throw new ApplicationException("지원하지 않는 조회 아이템 타입 all: {} classType: {}", allColumnExpr.toString(), selectItem.getClass().getCanonicalName());
				}
				else {
					clearMap(readCol);
					clearList(readCols);
					throw new ApplicationException("지원하지 않는 조회 아이템 타입 : {}", selectItem.getClass().getCanonicalName());
				}
				
				readCols.add(readCol);
			}
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[END] extractReadColumns");
		}
		return readCols;
	}
	
	
	
	private void clearList(List<?> list){
		if(list != null) {
			list.clear();
		}
	}
	
	private void clearMap(Map<?,?> map){
		if(map != null) {
			map.clear();
		}
	}
}
