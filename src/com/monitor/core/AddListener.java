package com.monitor.core;

import java.awt.Component;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;

import javax.swing.JFrame;

import com.monitor.listener.MonitorViewListener;
import com.monitor.view.MonitorView;
import java.lang.reflect.Method;

public class AddListener {
	public static void addListener(Component c, EventListener lis) {
		Class<? extends EventListener> lisclass = lis.getClass();

		Class<? extends Component> comclass = c.getClass();
		AnnotatedType[] interfaces = lisclass.getAnnotatedInterfaces();
		
		for (AnnotatedType type : interfaces) {
//			try {
//				System.out.println(Class.forName(type.getType().getTypeName()).getSuperclass());
//			} catch (ClassNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			String listener = type.getType().toString();
			int lastPoint = listener.lastIndexOf(".");
			listener = listener.substring(lastPoint + 1);
			
			try {
				Method method = comclass.getMethod("add" + listener, Class.forName(type.getType().getTypeName()));
				method.invoke(c, lis);
			} catch (SecurityException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		addListener(new JFrame(), new MonitorViewListener(new MonitorView("234")));
	}
}
