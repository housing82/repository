package com.universal.code.constants;

import java.util.Map;

import org.apache.commons.collections.FastHashMap;


public class JavaReservedWordConstants {

	private final static Map<String, String> words;
	
	static {
		words = new FastHashMap();
		((FastHashMap) words).setFast(true);
		words.put("abstract", "abstractJrw"); // 추상클래스 또는 추상 메소드로 선언
		words.put("default", "defaultJrw"); // switch문에서 적절한 case가 없을경우 실행되는 구문  
		words.put("package", "packageJrw"); // 프로그램을 패키지로 묶음, 클래스 컬렉션(라이브버리)
		words.put("synchronized", "synchronizedJrw"); // 메소드 동기화 또는 동기화 블럭 생성, 클래스나 코드이 한 섹션을 잠궈 쓰레드에 의한 동시적인 수정을 예방
		words.put("boolean", "booleanJrw"); // 기본 데이터타입. true와 false로 이루어져있슴(1비트형)
		words.put("do", "doJrw"); // do-while 루프에 쓰임
		words.put("if", "ifJrw"); // 조건문에 사용
		words.put("private", "privateJrw"); // 메소드, 또는 변수를 클래스 내부에서만 사용가능, 이 클래스의 멤버만이 객체에 Access할 수 있음을 지정하는 수정자
		words.put("this", "thisJrw"); // 클래스 자기자신이 만들어낼 객체의 주소값을 의미, 현재 객체의 인스턴스를 참조한다
		words.put("break", "breakJrw"); // 가장 가까운 루프문 탈출, switch - case 문 종료
		words.put("double", "doubleJrw"); // 기본 데이터타입. 8byte 실수형
		words.put("implements", "implementsJrw"); // 클래스가 명령된 인터페이스 구현(인터페이스 메소드 정의) 특수한 클래스(interface)를 사용하여 복수 상속 받을시 사용
		words.put("protected", "protectedJrw"); // 메소드, 또는 변수를 페키지 내에서 또는 상속받은 클래스에만 허용, 이 클래스, 서브 클래스, 그외에 패키지내에서만 객체에 access할수 있음을 지정하는 수정자
		words.put("throw", "throwJrw"); // 임의로 exception 발생,  예외발생을 알려준다
		words.put("byte", "byteJrw"); // 기본 데이터타입, 바이트형, 8bit(1byte)정수형 자료형
		words.put("else", "elseJrw"); // if명령문의 대안절
		words.put("import", "importJrw"); // 프로그램이 사용하는 패키지(클래스 라이브러리)를 식별한다
		words.put("public", "publicJrw"); // 클래스, 메소드, 또는 변수를 외부에서 접근 가능케 함, 모든 클래스의 메소드가 객체에 access할수 있음을 지시하는 수정자
		words.put("throws", "throwsJrw"); // 발생한 exception 메소드 밖으로 넘김, 메소드가 던질수 있는 예외 상황을 나열한다
		words.put("switch", "switchJrw"); // 하나 이상의 case명령문과 사용되어 추가명령문을 작성
		words.put("enum", "enumJrw"); // Enumeration 으로 정의된 클래스의 타입을 나타낼때
		words.put("instanceof", "instanceofJrw"); // 두 객체의 타입을 동일성 검사, 지정된 객체가 클래스와 인스턴스일 경우 true를 리턴한다(연산자 취급)
		words.put("return", "returnJrw"); // 메소드 리턴, 함수로 부터 제어를 호출기로 리턴한다
		words.put("try", "tryJrw"); // 예외상황을 트랩하는 코드 블럭을 지정
		words.put("catch", "catchJrw"); // 예외를 트랩하는 구문
		words.put("extends", "extendsJrw"); // 클래스 상속시 쓰임, 코드가 정의하고 있는 모 클래스를 정의한다
		words.put("int", "intJrw"); // 기본 데이터타입. 4byte 정수 타입
		words.put("short", "shortJrw"); // 기본 데이터타입, 2byte 정수 타입
		words.put("char", "charJrw"); // 기본 데이터타입, 2byte character 타입
		words.put("final", "finalJrw"); // 메소드, 변수가 상수이며, 클래스가 서브 클래스가 될 수 없으며, 메소드가 바뀔수 없음을 지정.
		words.put("interface", "interfaceJrw"); // 인터페이스로 선언,클래스가 구현될 수 있는 메소드로 추상적인 유형을 정의
		words.put("static", "staticJrw"); // 변수 또는 메소드를 static 메모리 영역에 선언 (공용의 의미), 클래스의 객체가 아닌 클래스에게객체의 고유성을지시하는수정자
		words.put("void", "voidJrw"); // 리턴값이 없음,  메소드가 값을 리턴하지않음을 지정
		words.put("class", "classJrw"); // 클래스. 
		words.put("finally", "finallyJrw"); // try catch문에서 예외발생 여부와 상관없이 무조건 실행되는 구문, java가 항상 실행하는 try블록의 일부를 지정
		words.put("long", "longJrw"); // 기본 데이터타입 8byte정수 타입
		words.put("strictfp", "strictfpJrw"); // 부동소수타입 계산
		words.put("volatile", "volatileJrw"); // 스레드 이용시 변수의 동기화 문제해결 5.0이상에서 정상작동, 변수가 비동기적으로 변하므로 컴파일러가 변수 사용시 최적화를 시도해선 않됨을 지정한다
		words.put("float", "floatJrw"); // 기본 데이터타입 4바이트 실수
		words.put("native", "nativeJrw"); // 자바이외의 프로그래밍 언어로 작성된 부프로그램을 호출 할 때 사용,메소드가 C내의 어디선가 또는 다른플랫폼에 의존하는 다른 언어로 구현됬음을 지정
		words.put("super", "superJrw"); // 부모클래스의 객체의 주소값을 나타냄, 슈퍼클래스 객체나 구성자를 참조한다
		words.put("while", "whileJrw"); // 반복문, 루프의 시작을 지정
		words.put("continue", "continueJrw"); // 루프문의 조건절로 이동하여 다음 반복
		words.put("for", "forJrw"); // for 루프,연속적인 루프를 실행
		words.put("new", "newJrw"); // 인스턴스를 새로 만들때 쓰임,새 객체나 배열을 생성
		words.put("case", "caseJrw"); // Switch 명령문 내에서 선택사항을 지시한다.
		words.put("null", "nullJrw"); // 임의 객체를 참조하지 않는 변수(C에서 0인것처럼 간주하지않음을 뜻함)를 지정
		words.put("transient", "transientJrw"); // 객체 직렬화대상에서 제외시킴 
		
	}
	
	public static String get(String key) {
		String input = key;
		String checnged = words.get(input);
		return (checnged != null ? checnged : input);
	}
}
