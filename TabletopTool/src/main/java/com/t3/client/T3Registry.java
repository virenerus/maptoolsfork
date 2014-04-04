/*
 *  This software copyright by various authors including the RPTools.net
 *  development team, and licensed under the LGPL Version 3 or, at your
 *  option, any later version.
 *
 *  Portions of this software were originally covered under the Apache
 *  Software License, Version 1.1 or Version 2.0.
 *
 *  See the file LICENSE elsewhere in this distribution for license details.
 */

package com.t3.client;

import java.net.MalformedURLException;
import java.util.List;

import com.caucho.hessian.client.HessianProxyFactory;

//FIXMESOON --> this is used to register you game online for others to easily find it
//this is still running on rptool and should be replaced with our own server
//here we could also implement hole punching
public class T3Registry {
	private static final String SERVICE_URL = "http://services.rptools.net/tabletoptool_registry-1_3.php";
	private static T3RegistryService service;

	static {
		HessianProxyFactory factory = new HessianProxyFactory();
		factory.setChunkedPost(false);
		try {
			service = (T3RegistryService) factory.create(T3RegistryService.class, SERVICE_URL);
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		}
	}

	public static String findInstance(String id) {
		checkService();
		return service.findInstance(id);
	}

	public static List<String> findAllInstances() {
		checkService();
		return service.findAllInstances();
	}

	public static String getAddress() {
		checkService();
		return service.getAddress();
	}

	public static int registerInstance(String id, int port) {
		checkService();
		return service.registerInstance(id, port, TabletopTool.getVersion());
	}

	public static void unregisterInstance(int port) {
		checkService();
		service.unregisterInstance(port);
	}

	public static boolean testConnection(int port) {
		checkService();
		return service.testConnection(port);
	}

	public static void heartBeat(int port) {
		checkService();
		service.heartBeat(port);
	}

	private static void checkService() {
		if (service == null) {
			throw new RuntimeException("Service is not available");
		}
	}

	public static void main(String[] args) throws Exception {
//		long delay = 0;
//
//		Thread.sleep(delay);
//		System.out.println("Register");
//		registerInstance("my test", 4444);
//
//		Thread.sleep(delay);
//		System.out.println("Heartbeat");
//
//		heartBeat(4444);
//
//		Thread.sleep(delay);
//		System.out.println("Find: " + findInstance("my test"));
//
//		Thread.sleep(delay);
//		System.out.println("RERegister");
//		registerInstance("my test", 4444);
//
//		Thread.sleep(delay);
//		System.out.println("Find: " + findInstance("my test"));
//
//		Thread.sleep(delay);
//		System.out.println("Find: " + findInstance("my test"));
//
//		Thread.sleep(delay);
//		System.out.println("UnRegister");
//		unregisterInstance(4444);

		System.out.println("Test: " + testConnection(51234));
		System.out.println("All instances: " + findAllInstances());
	}
}
