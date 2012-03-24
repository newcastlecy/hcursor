package org.loon.framework.javase.game;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Dimension;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.graphics.Deploy;

/**
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
public abstract class GameApplet extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() {
		LSystem.isApplet = true;
		Deploy deploy = setupDeploy();
		this.setSize(new Dimension(GameManager.getSystemHandler().getWidth(),
				GameManager.getSystemHandler().getHeight()));
		this.setIgnoreRepaint(true);
		this.setLayout(null);
		setup(deploy);
	}

	protected abstract Deploy setupDeploy();

	public void setup(Deploy deploy) {
		if (deploy.getScreen().getComponentCount() > 0) {
			Component[] components = deploy.getScreen().getComponents();
			for (int i = 0; i < components.length; i++) {
				add(components[i]);
			}
		}
	}

}
