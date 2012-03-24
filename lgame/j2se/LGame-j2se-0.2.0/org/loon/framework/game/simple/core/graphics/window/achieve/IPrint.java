package org.loon.framework.game.simple.core.graphics.window.achieve;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import org.loon.framework.game.simple.action.map.Vector2D;
import org.loon.framework.game.simple.utils.GraphicsUtils;

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
 * @email��ceponline@yahoo.com.cn
 * @version 0.1.1
 */
public class IPrint {

	private char[] showMessages = new char[] { '\0' };

	private Color fontColor = Color.white;

	private int interceptMaxString = 0;

	private int interceptCount = 0;

	private int messageLength = 10;

	private String messages;

	private boolean onComplete, newLine, visible;

	private StringBuffer messageBuffer = new StringBuffer(messageLength);

	private int width, height, leftOffset, topOffset, next, nowLeft,
			messageCount;

	private float alpha;

	private Vector2D vector;

	private Image creeseIcon;

	public IPrint(Vector2D vector, int width, int height) {
		this("", vector, width, height);
	}

	public IPrint(String context, Vector2D vector, int width, int height) {
		this.setMessage(context);
		this.vector = vector;
		this.width = width;
		this.height = height;
		this.visible = true;
	}

	public void setMessage(String context) {
		setMessage(context, false);
	}

	public void setMessage(String context, boolean isComplete) {
		this.messages = context;
		this.next = context.length();
		this.onComplete = false;
		this.newLine = false;
		this.messageCount = 0;
		this.messageBuffer.delete(0, messageBuffer.length());
		if (isComplete) {
			this.complete();
		}
	}

	public String getMessage() {
		return messages;
	}

	private Color getColor(char flagName) {
		if ('r' == flagName || 'R' == flagName) {
			return Color.red;
		}
		if ('b' == flagName || 'B' == flagName) {
			return Color.black;
		}
		if ('l' == flagName || 'L' == flagName) {
			return Color.blue;
		}
		if ('g' == flagName || 'G' == flagName) {
			return Color.green;
		}
		if ('o' == flagName || 'O' == flagName) {
			return Color.orange;
		}
		if ('y' == flagName || 'Y' == flagName) {
			return Color.yellow;
		} else {
			return null;
		}
	}

	public void draw(Graphics2D g) {
		draw(g, Color.WHITE);
	}

	private void drawMessage(Graphics2D g, Color old) {
		int fontSize = g.getFont().getSize();
		int fontHeight = g.getFontMetrics().getHeight();
		nowLeft = (width / 2) - ((fontSize * messageLength) / 2)
				- (fontSize / 2);
		int size = showMessages.length;
		int index = 0, offset = 0;
		for (int i = 0; i < size; i++) {
			if (interceptCount < interceptMaxString) {
				interceptCount++;
				g.setColor(fontColor);
				continue;
			} else {
				interceptMaxString = 0;
				interceptCount = 0;
			}
			if (showMessages[i] == '\n') {
				index = 0;
				offset++;
				continue;
			} else if (showMessages[i] == '<') {
				Color color = getColor(showMessages[i < size - 1 ? i + 1 : i]);
				if (color != null) {
					interceptMaxString = 1;
					fontColor = color;
				}
				continue;
			} else if (showMessages[i] == '/') {
				if (showMessages[i < size - 1 ? i + 1 : i] == '>') {
					interceptMaxString = 1;
					fontColor = old;
				}
				continue;
			} else if (index > messageLength) {
				index = 0;
				offset++;
				newLine = false;
			}
			int fontLeft = nowLeft + (fontSize * index);
			if (i != size - 1) {
				GraphicsUtils.setAntialias(g, true);
				g.drawString(String.valueOf(showMessages[i]), vector.x()
						+ fontLeft + leftOffset, (offset * fontHeight)
						+ vector.y() + (fontSize * 2) + topOffset);
				GraphicsUtils.setAntialias(g, false);
			} else if (!newLine) {
				g.drawImage(creeseIcon, vector.x() + fontLeft + leftOffset,
						(offset * fontHeight) + vector.y() + (fontSize * 2)
								+ topOffset, null);
			}
			index++;
		}
		if (messageCount == next) {
			onComplete = true;
		}

	}

	public void draw(Graphics2D g, Color old) {
		if (!visible) {
			return;
		}
		alpha = GraphicsUtils.getAlpha(g);
		GraphicsUtils.setAlpha(g, 1.0);
		drawMessage(g, old);
		GraphicsUtils.setAlpha(g, alpha);
	}

	public void complete() {
		onComplete = true;
		messageCount = messages.length();
		next = messageCount;
		showMessages = messages.toCharArray();
	}

	public boolean isComplete() {
		return onComplete;
	}

	public boolean next() {
		if (!onComplete) {
			if (messageCount == next) {
				onComplete = true;
				return false;
			}
			if (messageBuffer.length() > 0) {
				messageBuffer.delete(messageBuffer.length() - 1, messageBuffer
						.length());
			}
			messageBuffer.append(messages.charAt(messageCount));
			messageBuffer.append("_");
			showMessages = messageBuffer.toString().toCharArray();
			messageCount++;
		} else {
			return false;
		}
		return true;
	}

	public Image getCreeseIcon() {
		return creeseIcon;
	}

	public void setCreeseIcon(Image creeseIcon) {
		this.creeseIcon = creeseIcon;
	}

	public int getMessageLength() {
		return messageLength;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getLeftOffset() {
		return leftOffset;
	}

	public void setLeftOffset(int leftOffset) {
		this.leftOffset = leftOffset;
	}

	public int getTopOffset() {
		return topOffset;
	}

	public void setTopOffset(int topOffset) {
		this.topOffset = topOffset;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
