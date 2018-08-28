package com.xy.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xy.annotation.MyController;
import com.xy.annotation.MyRequestMapping;

public class MyDispatcherServlet extends HttpServlet{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Properties properties = new Properties();
	private List<String> classNames = new ArrayList<String>();
	private Map<String, Object> ioc = new HashMap<String, Object>();
	private Map<String, Method> handlerMapping = new HashMap<String, Method>();
	private Map<String, Object> controllerMap = new HashMap<String, Object>();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		doLoadConfig(config.getInitParameter("contextConfigLocation")); //加载配置
		
		doScanner(properties.getProperty("scanPackage")); //取到扫描包路径下所有类名
		
		doInstance(); //将扫描包路径下加了@MyController注解的类，创建一个实例，加入IOC容器（ioc容器为一个map）
		
		initHandlerMapping(); //取ioc容器中每个类中加入@MyRequestMapping注解的方法，将url与方法的映射关系维护在一个map中，同时也维护url与类实例的关系
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			doDispatcher(req, resp);
		} catch (Exception e) {
			resp.getWriter().write("500!! Server Exception");
		}
	}
	private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		System.out.println("my spring start");
		if (handlerMapping.isEmpty()) {
			return;
		}
		String url = req.getRequestURI();
		String contextPath = req.getContextPath();
		url = url.replace(contextPath, "").replaceAll("/+", "/");
		
		if (!handlerMapping.containsKey(url)) {
			resp.getWriter().write("404 NOT FOUND");
			return;
		}
		
		Method method = handlerMapping.get(url);
		Class<?>[] parameterTypes = method.getParameterTypes();
		Map<String, String[]> parameterMap = req.getParameterMap();
		Object[] paramValues = new Object[parameterTypes.length];
		
		for (int i = 0; i < parameterTypes.length; i ++) {
			String requestParam = parameterTypes[i].getSimpleName();
			
			if (requestParam.equals("HttpServletRequest")) {
				paramValues[i] = req;
				continue;
			}
			if (requestParam.equals("HttpServletResponse")) {
				paramValues[i] = resp;
				continue;
			}
			if (requestParam.equals("String")) {
				//这里不会把每个String的参数值都设置成最后一个吗？？
				for (Entry<String, String[]> param : parameterMap.entrySet()) {
					String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
					paramValues[i] = value;
				}
			}
		}
		try {
			method.invoke(controllerMap.get(url), paramValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("my spring end");
	}

	private void initHandlerMapping() {
		if (ioc.isEmpty()) {
			return;
		}
		try {
			for (Entry<String, Object> entry : ioc.entrySet()) {
				Class<? extends Object> clazz = entry.getValue().getClass();
				if (!clazz.isAnnotationPresent(MyController.class)) {
					continue;
				}
				String baseUrl = "";
				if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
					MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
					baseUrl = annotation.value();
				}
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (!method.isAnnotationPresent(MyRequestMapping.class)) {
						continue;
					}
					MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
					String url = annotation.value();
					url = (baseUrl + "/" + url).replaceAll("/+", "/");
					handlerMapping.put(url, method);
					controllerMap.put(url, clazz.newInstance());
					System.out.println(url + "," + method);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doInstance() {
		if (classNames.isEmpty()) {
			return;
		}
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				if (clazz.isAnnotationPresent(MyController.class)) {
					ioc.put(toLowerFirstWord(clazz.getSimpleName()), clazz.newInstance());
				} else {
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String toLowerFirstWord(String simpleName) {
		char[] charArray = simpleName.toCharArray();
		charArray[0] += 32;
		return String.valueOf(charArray);
	}

	private void doScanner(String property) {
		URL url = getClass().getClassLoader().getResource("/" + property.replaceAll("\\.", "/"));
		File dir = new File(url.getFile());
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				doScanner(property + "." + file.getName());
			} else {
				String className = (property + "." + file.getName()).replace(".class", "");
				classNames.add(className);
			}
			
		}
	}

	private void doLoadConfig(String initParameter) {
		InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(initParameter);
		
		try {
			properties.load(resourceAsStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != resourceAsStream) {
				try {
					resourceAsStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}