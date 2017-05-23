package com.universal.code.extend;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.mapping.Collection;

import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.PropertyUtil;
import com.universal.code.utils.SystemUtil;

public abstract class CommonObject {

	private PropertyUtil propertyUtil;
	
	public String toString() {
		propertyUtil = new PropertyUtil();
		StringBuilder out = new StringBuilder();
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

					value = PropertyUtil.getProperty(this, field.getName());
					out.append("	");
					out.append(field.getName());
					out.append(": ");
					
					if(Collection.class.isAssignableFrom(field.getType())) {
						out.append("{").append(SystemUtil.LINE_SEPARATOR);
						Iterator items = ((Collection) value).getColumnIterator();
						while(items.hasNext()) {
							out.append("		");
							out.append(items.next());
						}
						out.append("	}").append(SystemUtil.LINE_SEPARATOR);
					}
					else if(Map.class.isAssignableFrom(field.getType())) {
						out.append("{").append(SystemUtil.LINE_SEPARATOR);
						Map<?, ?> items = (Map) value;
						for(Entry item : items.entrySet()) {
							out.append("		");
							out.append(item.getKey());
							out.append(": ");
							out.append(item.getValue());
							out.append(SystemUtil.LINE_SEPARATOR);
						}
						out.append("	}").append(SystemUtil.LINE_SEPARATOR);
					}
					else {
						out.append(value).append(SystemUtil.LINE_SEPARATOR);
					}

				} catch (IllegalArgumentException e) {
					throw new ApplicationException(e);
				} catch (Exception e) {
					throw new ApplicationException(e);
				}
			}
		}
		out.append("}");
		return out.toString();
	}
}
