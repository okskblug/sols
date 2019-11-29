package kr.co.atis.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SchemaProperties {

	private Properties properties;

	public SchemaProperties() {
		properties = new Properties();
	}

	public Properties getProperties() {
		return properties;
	}

	/**
	 * 이 메소드는 프로퍼티 파일을 스트림으로 읽어 들여 멤버 변수의 프로퍼티에 적재합니다.
	 *
	 * @param path
	 * @throws IOException
	 */
	public void loadProp(String path) throws IOException {

		final FileInputStream inputStream = new FileInputStream( path );
//		InputStream inputStream = getClass().getResourceAsStream(path);
		properties.load(inputStream);
		inputStream.close();
	}

	/**
	 * 이 메소드는 static으로 선언해서 프로퍼티 파일을 읽는 것을 보여줍니다.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Properties loadPropForStatic(String path) throws IOException {
		Properties properties 	= new Properties();
		InputStream inputStream = SchemaProperties.class.getClassLoader().getResourceAsStream(path);
		properties.load(inputStream);
		inputStream.close();
		return properties;
	}

	public static Properties getSchemaProperties() throws IOException {
		SchemaProperties SchemaProperties = new SchemaProperties();

		// 프로퍼티 파일을 읽어들이고 해당 값을 출력해봅니다.
		SchemaProperties.loadProp("./properties/schemaProperties.properties");
		Properties properties = SchemaProperties.getProperties();
//		properties.list(System.out);

		// 아래 코드는 새로운 프로퍼티 파일에 같은 키를 준 경우 오버라이드 하는 것을 확인합니다.
//		Properties staticProp = SchemaProperties.loadPropForStatic("application-prod.properties");
//		properties.putAll(staticProp);
//		properties = SchemaProperties.getProperties();
//		properties.list(System.out);

		// 아래 코드는 프로퍼티간의 결합을 확인합니다.
//		Properties dummy = new Properties();
		// dummy.put("demo.type", "dummy"); // 기존 키를 오버라이드 합니다.
		// dummy.put("demo.temp", "temp"); // 새로운 키를 추가합니다.
//		properties.putAll(dummy); // 기존 프로퍼티에 더미 프로퍼티를 결합합니다.
//		properties.list(System.out);

		// 아래 코드는 개별 키를 주어 값을 반환받습니다.
//		Object type = properties.get("demo.type");
//
//		// 아래코드는 프로퍼티를 키들을 순회하는 구문입니다.
//		// .stringPropertyNames 대신 .keySet 을 사용할수도 있습니다.
//		for (String key : properties.stringPropertyNames()) {
//			Object value = properties.getProperty(key);
//		}

		// 해당 키가 있는지 여부를 판별합니다.
//		properties.containsKey("demo.type");

		// 해당 값이 있는지 여부를 판별합니다.
//		properties.containsValue("dummy");

		// 보너스 코드
		// 키값을 주고 해당 값이 있으면 해당 값을 반환하지만
		// 해당 값이 없으면 null을 반환합니다.
//		Object result = properties.computeIfAbsent("undefined", value -> returnNull(value));

		return properties;
	}

	public static Object returnNull(Object key) {
		System.out.println(key + " value is null.");
		return null;
	}
}