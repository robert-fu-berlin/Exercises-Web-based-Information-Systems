package org.fuberlin.nbi.wbi.test;

import java.rmi.RemoteException;

import org.fuberlin.nbi.wbi.HelloWorldServiceStub;
import org.fuberlin.nbi.wbi.HelloWorldServiceStub.SayHello;
import org.fuberlin.nbi.wbi.HelloWorldServiceStub.SayHelloResponse;

public class HelloWorldServiceTest {
	public static void main(String[] args) throws RemoteException {
		HelloWorldServiceStub service = new HelloWorldServiceStub();

		SayHello request = new SayHello();
		request.setName(args.length > 0 ? args[0] : "nobody");

		SayHelloResponse response = service.sayHello(request);

		System.out.println(response.get_return());
	}
}
