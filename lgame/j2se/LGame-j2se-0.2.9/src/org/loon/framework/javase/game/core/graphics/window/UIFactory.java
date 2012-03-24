package org.loon.framework.javase.game.core.graphics.window;

import java.awt.image.BufferedImage;
import java.util.Map;

import org.loon.framework.javase.game.core.graphics.LComponent;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;

/**
 * 
 * Copyright 2008 - 2009
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
public abstract class UIFactory {

	private final Map UIResource = new java.util.LinkedHashMap();

	/**
	 * 是有永久保存当前渲染器
	 */
	public boolean immutable = false;

	/**
	 * 当前渲染器名称
	 * 
	 */
	public UIFactory() {
	}

	/**
	 * 当前渲染器名称
	 * 
	 * @return
	 */
	public abstract String getUIName();

	/**
	 * 渲染器组件描述
	 * 
	 * @return
	 */
	public abstract String[] getUIDescription();

	/**
	 * 创建目标UI并设定为指定大小
	 * 
	 * @param component
	 * @param width
	 * @param height
	 * @return
	 */
	public abstract BufferedImage[] createUI(LComponent component, int width,
			int height);

	/**
	 * 处理当前UI
	 * 
	 * @param component
	 * @param ui
	 */
	public abstract void processUI(LComponent component, BufferedImage[] ui);

	/**
	 * 创建UI至指定位置
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param component
	 * @param ui
	 */
	public abstract void createUI(LGraphics g, int x, int y,
			LComponent component, BufferedImage[] ui);

	/**
	 * 返回指定的UI部分
	 * 
	 * @param key
	 * @param component
	 * @return
	 */
	public Object get(String key, LComponent component) {
		if (component == null) {
			return this.UIResource.get(key);
		}

		return (component.getUIResource().containsKey(key)) ? component
				.getUIResource().get(key) : this.UIResource.get(key);
	}

	/**
	 * 插入资源到此UI中
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		this.UIResource.put(key, value);
	}

	/**
	 * 删除指定资源
	 * 
	 * @param key
	 * @return
	 */
	protected final Object remove(String key) {
		return this.UIResource.remove(key);
	}

	/**
	 * 返回所有资源的字符串数组格式
	 * 
	 * @return
	 */
	public final String[] getUIResource() {
		String[] temp = new String[this.UIResource.size()];
		String[] keys = (String[]) this.UIResource.keySet().toArray(temp);
		return keys;
	}

}
