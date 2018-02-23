package com.monitor.core;

import java.awt.Image;
import java.util.EventListener;

public interface BackGroundListener extends EventListener {
	public void backGroundChanged(Image oldImage, Image newImage);
	public void backGroundLocationChanged(Location oldDim,Location newDim);
}
