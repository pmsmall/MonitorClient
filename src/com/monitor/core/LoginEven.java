package com.monitor.core;

import com.mine.MyUI.MyUI;

public class LoginEven {
	public final String type;
	public final Object content;
	public final MyUI sourceRootContainer;

	public LoginEven(String type, Object content, MyUI sourceRootContainer) {
		this.type = type;
		this.content = content;
		this.sourceRootContainer = sourceRootContainer;
	}
}
