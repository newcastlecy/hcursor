package org.loon.framework.javase.game.action.sprite;

import java.awt.Image;

import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;

/**
 * Copyright 2008 - 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class Background extends AbstractBackground {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient LImage buffer;

	public Background(String fileName) {
		this(LImage.createImage(fileName));
	}

	public Background(String fileName, int w, int h) {
		this(LImage.createImage(fileName), w, h);
	}

	public Background(LImage image, int w, int h) {
		super(w, h);
		this.buffer = image;
	}

	public Background(LImage image) {
		super(image.getWidth(), image.getHeight());
		this.buffer = image;
	}

	public Image getBitmap() {
		return buffer.getBufferedImage();
	}

	public void setImage(LImage image) {
		this.buffer = image;
		this.setSize(image.getWidth(), image.getHeight());
	}

	public void createUI(LGraphics g,int nx,int ny, int x, int y, int w, int h) {
		g.translate(nx, ny);
		if (alpha > 0.1 && alpha < 1.0) {
			g.setAlpha(alpha);
			g.drawImage(buffer, 0, 0, w, h, x, y, x + w, h + y);
			g.setAlpha(1.0F);
		} else {
			g.drawImage(buffer, 0, 0, w, h, x, y, x + w, h + y);
		}
		g.translate(-nx, -ny);
	}

}
