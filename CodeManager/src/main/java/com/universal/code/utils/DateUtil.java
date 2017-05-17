package com.universal.code.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.exception.ValidateException;

@Component
public class DateUtil {

	protected static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	public final static String DEF_DAY_FORMAT;
	public final static String DEF_DATE_FORMAT;
	
	static {
		DEF_DAY_FORMAT = "yyyy-MM-dd";
		DEF_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	}
	
	public static String getFastDate(){
		return getFastDate(null);
	}
	
	public static String getFastDate(String dateFormat){
		return getFastDate(null, dateFormat);
	}

    public static String getFastDate(Date date, String dateFormat){
    	
    	String format = dateFormat;
    	Date datetime = (date != null ? date : new Date());
    	String dateformat = (format != null && !format.isEmpty()) ? format:DEF_DATE_FORMAT;
    	return FastDateFormat.getInstance(dateformat, TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA).format(datetime);
    }
    
    public static Date getStringToDate(String dateString){
    	return getStringToDate(dateString, DEF_DAY_FORMAT);
    }
    
    public static Date getStringToDate(String dateString, String dateFormat){
    	Date date = null;
    	String format = dateFormat;
    	String dateStr = dateString;
    	String dateformat = (format != null && !format.isEmpty()) ? format:DEF_DATE_FORMAT;
    	
   		try {
   			DateFormat transFormat = new SimpleDateFormat(dateformat);
			date = transFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	return date;
    }

    
    /**
     * 과거일부터 현재 날자와의 차이를 구함
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static int getPrevDateDiff(int year, int month, int day){

    	/** 현재 날자 */
    	Calendar currentCal = Calendar.getInstance();
    	currentCal.setTime (new Date()); 

    	/** 기준일 */
    	Calendar benchmarkCal = Calendar.getInstance();
    	benchmarkCal.set ( year, (month-1), day); // (과거일자)기준일로 설정. month의 경우 해당월수-1.

    	/** 날자차이 채크 */
    	int difference = 0;
    	while ( benchmarkCal.before(currentCal) ) {
    		difference++;
    		benchmarkCal.add ( Calendar.DATE, 1 ); // 다음날로 바뀜
    	}

    	return difference;
    }
    
    public static boolean isValidDate(String dateString) {
    	return isValidDate(dateString, null);
    }
    
	public static boolean isValidDate(String dateString, String dateFormat) {
		
		if (dateString == null || dateString.isEmpty()) {
			return false;
		}
		String date = dateString;
		String format = dateFormat;
		SimpleDateFormat sdf = new SimpleDateFormat(((format == null || format.isEmpty()) ? DEF_DAY_FORMAT : format));
		String confirmFormat = null;
		try {
			confirmFormat = sdf.format(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.equals(confirmFormat);
	}

	public Integer getStrDateToNumber(String dateString, String dateFormat){
		Integer out = IOperateCode.INTEGER_ZERO_VALUE;
		String date = dateString;
		String format = dateFormat;
		if( isValidDate(date, format) ) {
		
			date = dateString.replace("-", "").replace(".", "").replace("/", "");
			out = Integer.parseInt(date);
		}
		else {
			throw new ValidateException("날자형식이 잘못되었거나 존재하지 않습니다.");
		}
		return out;
	}
	
	
	/**
	 * 지정한 날짜에서 시간을 더할 때 GregorianCalendar 클래스를 사용합니다. add() 함수가 날짜에 대한 사칙연산을 하게
	 * 되는데 첫번째 인수로 연산하고 싶은 날짜 필드를 넘기시면 됩니다. 두번째 필드는 더하고 싶은 값을 넘기면 되는데 마이너스 값이면
	 * 시간을 빼게 됩니다.
	 */
	// 년도, 월, 일 더하기
	public Date addDate(Date standardDate, int addYear, int addMonth, int addWeek, int addDay, int addHour, int addMinute, int addSecond) {
		if(logger.isDebugEnabled()) {
			logger.debug("[START] addDate standardDate : " + standardDate);
		}
		
		if( standardDate == null ) {
			throw new ValidateException("기준 날짜가 존재하지 않습니다.");
		}
		
		Date out = null;
		
		try {
			if( logger.isDebugEnabled() ) {
				logger.debug(new StringBuilder()
				.append(SystemUtil.LINE_SEPARATOR)
				.append("[Increase Date Parameter Infomation]")
				.append(SystemUtil.LINE_SEPARATOR)
				.append("standardDate : ")
				.append(standardDate)
				.append(SystemUtil.LINE_SEPARATOR)
				.append("Year : ")
				.append(addYear)
				.append(SystemUtil.LINE_SEPARATOR)
				.append("Month : ")
				.append(addMonth)
				.append(SystemUtil.LINE_SEPARATOR)
				.append("Week : ")
				.append(addWeek)
				.append(SystemUtil.LINE_SEPARATOR)
				.append("Day : ")
				.append(addDay)
				.append(SystemUtil.LINE_SEPARATOR)
				.append("Hour : ")
				.append(addHour)
				.append(SystemUtil.LINE_SEPARATOR)
				.append("Minute : ")
				.append(addMinute)
				.append(SystemUtil.LINE_SEPARATOR)
				.append("Second : ")
				.append(addSecond)
				.append(SystemUtil.LINE_SEPARATOR)
				.append("[END]")
				.toString());
			}
			
			DateFormat transFormat = new SimpleDateFormat();
			Calendar cal = new GregorianCalendar(/*Locale.KOREA*/);
			cal.setTime(standardDate);
			
	        if( addYear > 0 || addMonth > 0) {
	        	//마지막일에서 증가인지 채크
	        	boolean lastDay = false;
		        if(cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH)){
		        	lastDay = true;
		        }
		        
				if( addYear > 0 ) {
					cal.add(Calendar.YEAR, addYear); // 년단위 증가
				}
				if( addMonth > 0 ) {
					cal.add(Calendar.MONTH, addMonth); // 월단위 증가
				}
				if( lastDay ) {
					cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH) ); //월단위 마지막 일짜 유지
				}
	        }
	        
			if( addWeek > 0 ) {
				cal.add(Calendar.WEEK_OF_YEAR, addWeek); // 주단위 증가
			}
			if( addDay > 0 ) {
				cal.add(Calendar.DAY_OF_YEAR, addDay); // 일단위 증가
			}
			if( addHour > 0 ) {
				cal.add(Calendar.HOUR, addHour); // 시간단위 증가
			}
			if( addMinute > 0 ) {
				cal.add(Calendar.MINUTE, addMinute); // 분단위 증가
			}
			if( addSecond > 0 ) {
				cal.add(Calendar.SECOND, addSecond); // 초단위 증가
			}

			out = transFormat.parse(transFormat.format(cal.getTime()));
			
		} catch (ParseException e) {
			throw new ApplicationException(e);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[END] addDate result : " + out);
		}
		return out;
	}

    /**
     * 시작일과 종료일의 날짜 차이를 구함
     * @param startDate
     * @param lastDate
     * @return
     */
	public long doDiffOfDate(String startDate, String lastDate) {
		
		String start = startDate;
		String end = lastDate;
		long diffDays = 0L; 
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date beginDate = formatter.parse(start);
			Date endDate = formatter.parse(end);

			// 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
			long diff = endDate.getTime() - beginDate.getTime();
			diffDays = diff / (24 * 60 * 60 * 1000);

		} catch (ParseException e) {
			throw new ApplicationException(e);
		}
		
		return diffDays;
	}
	
	/**
	 * 바인드된 date에 해당하는 요일(1~7)을 리턴합니다.
	 * 1:일요일
	 * 2:월요일
	 * 3:화요일
	 * 4:수요일
	 * 5:목요일
	 * 6:금요일
	 * 7:토요일
	 * @param date
	 * @param dateFormat : 문자열 date의 날짜형식을 바인드합니다.
	 * @return
	 */
	public static int getDayOfWeek(String dateParam, String dateFormatParam) {
		
		String date = dateParam; 
		String dateFormat = dateFormatParam;
		return getDayOfWeek(getStringToDate(date, dateFormat));
	}
	
	/**
	 * 바인드된 date에 해당하는 요일(1~7)을 리턴합니다.
	 * 1:일요일
	 * 2:월요일
	 * 3:화요일
	 * 4:수요일
	 * 5:목요일
	 * 6:금요일
	 * 7:토요일
	 * @param date
	 * @return
	 */
	public static int getDayOfWeek(Date dateParam) {
		
		Date date = dateParam;
		Calendar cal = Calendar.getInstance();
		if( date != null ) {
			cal.setTime(date);	
		}
		int nWeek = cal.get(Calendar.DAY_OF_WEEK);
		return nWeek;
	}
	
	
	/**
	 * Date를 long으로 변환한 데에터를 다시 문자형 날짜로 변환하여줍니다.
	 * @param dateTime
	 * @param dateFormat
	 * @return
	 */
	public static String doLongDateToString(long dateTime, String dateFormat) {
		long time = dateTime;
		String format = dateFormat;
		return getFastDate(new Date(time), format);
	}
	
	
}
