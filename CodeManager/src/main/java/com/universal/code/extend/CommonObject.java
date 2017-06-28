package com.universal.code.extend;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.mapping.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.SystemUtil;

public abstract class CommonObject {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String toString() {
		
		StringBuilder out = new StringBuilder();
		
		try {
			
			out.append(SystemUtil.LINE_SEPARATOR);
			out.append("[");
			out.append(this.getClass().getName());
			out.append("] {");
			out.append(SystemUtil.LINE_SEPARATOR);
			
			Field[] fields = this.getClass().getDeclaredFields();
			if(fields != null && fields.length > 0) {
				Object value = null;
				for(Field field : fields) {
					try {

						value = PropertyUtils.getProperty(this, field.getName());
						out.append("	");
						out.append(field.getName());
						out.append(": ");
						if(value == null) {
							//logger.debug("＃Value of null {}: {}", field.getName(), value);
							out.append(value)
								.append(SystemUtil.LINE_SEPARATOR);
						}
						else if(List.class.isAssignableFrom(value.getClass())) {
							//logger.debug("＃From List {}: {}", field.getName(), value.getClass());
							out.append("{").append(SystemUtil.LINE_SEPARATOR);
							List items = (List) value;
							for(Object item : items) {
								//logger.debug(" item: {}", item);
								out.append("		")
									.append(item)
									.append(SystemUtil.LINE_SEPARATOR);
							}
							out.append("	}")
								.append(SystemUtil.LINE_SEPARATOR);
						}
						else if(Collection.class.isAssignableFrom(value.getClass())) {
							//logger.debug("＃From Collection {}: {}", field.getName(), value.getClass());
							out.append("{").append(SystemUtil.LINE_SEPARATOR);
							Iterator<?> items = ((Collection) value).getColumnIterator();
							while(items.hasNext()) {
								out.append("		")
									.append(items.next())
									.append(SystemUtil.LINE_SEPARATOR);
							}
							out.append("	}")
								.append(SystemUtil.LINE_SEPARATOR);
						}
						else if(Set.class.isAssignableFrom(value.getClass())) {
							//logger.debug("＃From Set {}: {}", field.getName(), value.getClass());
							out.append("{").append(SystemUtil.LINE_SEPARATOR);
							Iterator<?> items = ((Set) value).iterator();
							while(items.hasNext()) {
								out.append("		")
									.append(items.next())
									.append(SystemUtil.LINE_SEPARATOR);
							}
							out.append("	}")
								.append(SystemUtil.LINE_SEPARATOR);
						}
						else if(Map.class.isAssignableFrom(value.getClass())) {
							//logger.debug("＃From Map {}: {}", field.getName(), value.getClass());
							out.append("{").append(SystemUtil.LINE_SEPARATOR);
							Map<?, ?> items = (Map) value;
							for(Entry item : items.entrySet()) {
								out.append("		")
									.append(item.getKey())
									.append(": ")
									.append(item.getValue())
									.append(SystemUtil.LINE_SEPARATOR);
							}
							out.append("	}").append(SystemUtil.LINE_SEPARATOR);
						}
						else {
							//logger.debug("＃General {}: {}", field.getName(), value.getClass());
							out.append(value)
								.append(SystemUtil.LINE_SEPARATOR);
						}

					} catch (IllegalArgumentException e) {
						throw new ApplicationException(e);
					} catch (Exception e) {
						throw new ApplicationException(e);
					}
				}
			}
			out.append("}");
		}
		catch(Exception e) {
			throw new ApplicationException("toString 장애발생", e);
		}
		
		return out.toString();
	}
	
}
