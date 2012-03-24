package org.loon.framework.javase.game;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.im.InputMethodRequests;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator.Attribute;

import org.loon.framework.javase.game.action.ActionControl;
import org.loon.framework.javase.game.action.map.shapes.RectBox;
import org.loon.framework.javase.game.core.EmulatorButtons;
import org.loon.framework.javase.game.core.EmulatorListener;
import org.loon.framework.javase.game.core.IHandler;
import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.graphics.Screen;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.loon.framework.javase.game.core.graphics.device.LGraphicsJava2D;
import org.loon.framework.javase.game.core.timer.LTimerContext;
import org.loon.framework.javase.game.core.timer.SystemTimer;
import org.loon.framework.javase.game.utils.GraphicsUtils;

/**
 * Copyright 2008 - 2011
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
 * @email��ceponline ceponline@yahoo.com.cn
 * @version 0.1.2
 */
public class Java2DView extends Java2DCanvas implements IView, Runnable,
		InputMethodListener, InputMethodRequests {

	private static final long serialVersionUID = 1982278682597393958L;

	final static private Toolkit systemToolKit = GraphicsUtils.toolKit;

	private final static long MAX_INTERVAL = 1000L;

	private boolean isStart, isFPS, isMemory;

	private Image logo;

	private String actionName;

	private AttributedString composedTextString;

	private AttributedCharacterIterator composedText;

	private TextHitInfo caret;

	final static private Attribute[] IM_ATTRIBUTES = { TextAttribute.INPUT_METHOD_HIGHLIGHT };

	final static private AttributedCharacterIterator EMPTY_TEXT = (new AttributedString(
			"")).getIterator();

	final static private Font fpsFont = GraphicsUtils.getFont("Dialog", 0, 20);

	private transient long remainderMicros;

	private transient boolean running = true;

	private transient int repaintMode, num_tmp;

	private long maxFrames, before, startTime, offsetTime, curFPS,
			calcInterval, lastTimeMicros, elapsedTime;

	private transient double frameCount;

	private boolean isSupportHardware;

	private Dimension dimension;

	private IHandler handler;

	private LGraphics gl;

	private Screen currentControl;

	private VolatileImage hardwareImage;

	private BufferedImage awtImage;

	private Thread mainLoop;

	private GameContext context;

	private EmulatorListener emulatorListener;

	private EmulatorButtons emulatorButtons;

	public Java2DView(IHandler handler) {
		format(handler);
	}

	/**
	 * 创建GameView初始设置
	 * 
	 * @param handler
	 */
	public void format(IHandler handler) {
		this.handler = handler;
		this.context = GameManager.getInstance().registerApp(this);
		this.setFPS(LSystem.DEFAULT_MAX_FPS);
		this.setBackground(Color.BLACK);
		this.dimension = new Dimension(handler.getWidth(), handler.getHeight());
		this.setSize(dimension);
		this.setIgnoreRepaint(true);
		this.addFocusListener(handler);
		this.addKeyListener(handler);
		this.addMouseListener(handler);
		this.addMouseMotionListener(handler);
		this.setIgnoreRepaint(true);
		this.enableInputMethods(true);
		this.addInputMethodListener(this);
	}

	/**
	 * 返回当前正在使用的游戏画布
	 * 
	 * @return
	 */
	public LGraphics getLGraphics() {
		return gl;
	}

	/**
	 * GameView内部用计时器
	 * 
	 * @return
	 */
	private long innerClock() {
		long now = System.currentTimeMillis();
		long ret = now - before;
		before = now;
		return ret;
	}

	/**
	 * 创建初始的Graphics
	 */
	public void createScreen() {
		int width = getWidth();
		int height = getHeight();
		try {
			hardwareImage = createVolatileImage(width, height,
					new ImageCapabilities(true));
		} catch (Exception e) {
			hardwareImage = null;
			int pixelSize = width * height;
			int[] pixels = new int[pixelSize];
			this.awtImage = GraphicsUtils.newAwtRGBImage(pixels, width, height,
					pixelSize);
		}
		if (hardwareImage == null) {
			this.isSupportHardware = false;
			this.gl = new LGraphicsJava2D(awtImage);
		} else {
			this.isSupportHardware = true;
			this.gl = new LGraphicsJava2D(hardwareImage);
		}
		LSystem.screenRect = new RectBox(0, 0, width, height);
	}

	/**
	 * 销毁图形资源
	 * 
	 */
	public void destroy() {
		synchronized (this) {
			handler.destroy();
			ActionControl.getInstance().stopAll();
			GameManager.getInstance().unregisterApp(this);
			context = null;
			if (gl != null) {
				gl.dispose();
				gl = null;
			}
			LSystem.destroy();
			LSystem.gc();
			notifyAll();
		}
	}

	/**
	 * 设定模拟按钮监听器
	 * 
	 * @param emulatorListener
	 */
	public void setEmulatorListener(EmulatorListener emulator) {
		this.emulatorListener = emulator;
		if (emulatorListener != null) {
			if (emulatorButtons == null) {
				emulatorButtons = new EmulatorButtons(emulatorListener, handler
						.getWidth(), handler.getHeight());
			} else {
				emulatorButtons.setEmulatorListener(emulator);
			}
		} else {
			emulatorButtons = null;
		}
	}

	/**
	 * 获得模拟器监听
	 * 
	 * @return
	 */
	public EmulatorListener getEmulatorListener() {
		return emulatorListener;
	}

	/**
	 * 获得模拟器按钮
	 * 
	 * @return
	 */
	public EmulatorButtons getEmulatorButtons() {
		return emulatorButtons;
	}

	/**
	 * 获得窗体图像
	 * 
	 * @return
	 */
	final public Image getAwtImage() {
		if (isSupportHardware) {
			return hardwareImage;
		} else {
			return awtImage;
		}
	}

	/**
	 * 清屏
	 * 
	 * @param g
	 */
	private void clearDraw(LGraphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	/**
	 * 刷新绘图
	 * 
	 */
	public synchronized void update() {
		if (running) {
			Graphics currentControl = this.getGraphics();
			if (currentControl != null) {
				currentControl.drawImage(getAwtImage(), 0, 0, null);
				systemToolKit.sync();
				gl.restore();
			}
		}
	}

	/**
	 * 刷新图像到指定位置
	 * 
	 * @param img
	 * @param x
	 * @param y
	 */
	public synchronized void updateLocation(BufferedImage img, int x, int y) {
		if (running) {
			Graphics currentControl = this.getGraphics();
			if (currentControl != null) {
				if (emulatorButtons != null) {
					gl.drawImage(img, x, y);
					emulatorButtons.draw(gl);
					currentControl.drawImage(getAwtImage(), 0, 0, null);
				} else {
					currentControl.drawImage(img, x, y, null);
				}
				systemToolKit.sync();
			}
		}
	}

	/**
	 * 刷新绘图，成比例调整显示位置（居中）
	 */
	public synchronized void update(BufferedImage img, int w, int h) {
		if (running) {
			Graphics currentControl = this.getGraphics();
			if (currentControl != null) {
				if (emulatorButtons != null) {
					gl.drawImage(img, getWidth() / 2 - w / 2, getHeight() / 2
							- h / 2);
					emulatorButtons.draw(gl);
					currentControl.drawImage(getAwtImage(), 0, 0, null);
				} else {
					currentControl.drawImage(img, getWidth() / 2 - w / 2,
							getHeight() / 2 - h / 2, null);
				}
				systemToolKit.sync();
			}
		}
	}

	/**
	 * 刷新绘图，成比例调整显示位置（画面变更为指定大小）
	 * 
	 * @param img
	 * @param w
	 * @param h
	 */
	public synchronized void updateFull(BufferedImage img, int w, int h) {
		if (running) {
			Graphics currentControl = this.getGraphics();
			if (currentControl != null) {
				if (emulatorButtons != null) {
					gl.drawImage(img, getWidth() / 2 - w / 2, getHeight() / 2
							- h / 2, w, h);
					emulatorButtons.draw(gl);
					currentControl.drawImage(getAwtImage(), 0, 0, null);
				} else {
					currentControl.drawImage(img, getWidth() / 2 - w / 2,
							getHeight() / 2 - h / 2, w, h, null);
				}
				systemToolKit.sync();
			}
		}
	}

	/**
	 * 刷新绘图
	 */
	public synchronized void update(BufferedImage img) {
		if (running) {
			Graphics currentControl = this.getGraphics();
			if (currentControl != null) {
				if (emulatorButtons != null) {
					gl.drawImage(img, 0, 0);
					emulatorButtons.draw(gl);
					currentControl.drawImage(getAwtImage(), 0, 0, null);
				} else {
					currentControl.drawImage(img, 0, 0, null);
				}
				systemToolKit.sync();
			}
		}
	}

	/**
	 * GameView内部线程
	 * 
	 */
	public void run() {
		if (LSystem.isLogo) {
			showLogo();
		}
		final LTimerContext timerContext = new LTimerContext();
		final SystemTimer timer = LSystem.getSystemTimer();
		long currTimeMicros, goalTimeMicros, elapsedTimeMicros;
		Thread currentThread = Thread.currentThread();
		do {
			if (LSystem.isPaused) {
				GraphicsUtils.wait(300);
				LSystem.gc(1000, 1);
				lastTimeMicros = timer.getTimeMicros();
				elapsedTime = 0;
				remainderMicros = 0;
			}
			if (!isStart) {
				Thread.yield();
				continue;
			}

			currentControl = handler.getScreen();

			if (currentControl == null) {
				continue;
			}
			if (!currentControl.next()) {
				if (LSystem.AUTO_REPAINT) {
					this.update();
				}
				continue;
			}
			currentControl.callEvents();

			goalTimeMicros = lastTimeMicros + 1000000L / maxFrames;
			currTimeMicros = timer.sleepTimeMicros(goalTimeMicros);
			elapsedTimeMicros = currTimeMicros - lastTimeMicros
					+ remainderMicros;
			elapsedTime = Math.max(0, (int) (elapsedTimeMicros / 1000));
			remainderMicros = elapsedTimeMicros - elapsedTime * 1000;
			lastTimeMicros = currTimeMicros;
			timerContext.millisSleepTime = remainderMicros;
			timerContext.timeSinceLastUpdate = elapsedTime;

			currentControl.runTimer(timerContext);

			if (LSystem.AUTO_REPAINT) {
				repaintMode = currentControl.getRepaintMode();
				switch (repaintMode) {
				case Screen.SCREEN_BITMAP_REPAINT:
					gl.drawImage(currentControl.getBackground(), 0, 0);
					break;
				case Screen.SCREEN_CANVAS_REPAINT:
					gl.drawClear();
					break;
				case Screen.SCREEN_NOT_REPAINT:
					break;
				default:
					gl.drawImage(currentControl.getBackground(), repaintMode
							/ 2 - LSystem.random.nextInt(repaintMode),
							repaintMode / 2
									- LSystem.random.nextInt(repaintMode));
					break;
				}
				currentControl.createUI(gl);
				if (isFPS) {
					tickFrames();
					gl.setFont(fpsFont);
					gl.setColor(Color.white);
					gl.drawString("FPS:" + curFPS, 5, 20);
				}
				if (isMemory) {
					Runtime runtime = Runtime.getRuntime();
					long totalMemory = runtime.totalMemory();
					long currentMemory = totalMemory - runtime.freeMemory();
					String memoryUsage = ((float) ((currentMemory * 10) >> 20) / 10)
							+ " of "
							+ ((float) ((totalMemory * 10) >> 20) / 10) + " MB";
					gl.setFont(fpsFont);
					gl.setColor(Color.white);
					gl.drawString("MEMORY:" + memoryUsage, 5, 45);
				}
				if (emulatorButtons != null) {
					emulatorButtons.draw(gl);
				}
				this.update();
			}

			if (isFocusOwner()) {
				Thread.yield();
				LSystem.isPaused = false;
				continue;
			} else {
				LSystem.isPaused = true;
			}
		} while (running && mainLoop == currentThread);
		this.destroy();
	}

	/**
	 * 生成FPS数值
	 * 
	 */
	private void tickFrames() {
		frameCount++;
		calcInterval += offsetTime;
		if (calcInterval >= MAX_INTERVAL) {
			long timeNow = System.currentTimeMillis();
			long realElapsedTime = timeNow - startTime;
			curFPS = Math.min(maxFrames,
					(long) ((frameCount / realElapsedTime) * MAX_INTERVAL));
			frameCount = 0L;
			calcInterval = 0L;
			startTime = timeNow;
		}
	}

	/**
	 * 框架Logo
	 * 
	 */
	public void showLogo() {
		try {
			long elapsedTime;
			int cx = 0, cy = 0;
			double delay;
			try {
				if (logo == null) {
					logo = GraphicsUtils
							.loadNotCacheImage("assets/logo.png");
				}
				cx = this.getWidth() / 2 - logo.getWidth(null) / 2;
				cy = this.getHeight() / 2 - logo.getHeight(null) / 2;
			} catch (Exception e) {
			}
			float alpha = 0.0f;
			boolean firstTime = true;
			elapsedTime = innerClock();
			while (alpha < 1.0f) {
				clearDraw(gl);
				Composite old = gl.getComposite();
				gl.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, alpha));
				gl.drawImage(logo, cx, cy);
				gl.setComposite(old);
				if (firstTime) {
					firstTime = false;
				}
				elapsedTime = innerClock();
				delay = 0.00065 * elapsedTime;
				if (delay > 0.22) {
					delay = 0.22 + (delay / 6);
				}
				alpha += delay;
				update();
			}
			while (num_tmp < 3000) {
				num_tmp += innerClock();
				update();
			}
			alpha = 1.0f;
			while (alpha > 0.0f) {
				clearDraw(gl);
				Composite old = gl.getComposite();
				gl.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, alpha));
				gl.drawImage(logo, cx, cy);
				gl.setComposite(old);
				elapsedTime = innerClock();
				delay = 0.00055 * elapsedTime;
				if (delay > 0.15) {
					delay = 0.15 + ((delay - 0.04) / 2);
				}
				alpha -= delay;
				update();
			}
			gl.restore();
		} catch (Throwable e) {
		} finally {
			if (logo != null) {
				logo.flush();
				logo = null;
			}
			LSystem.isLogo = false;
		}
	}

	protected void processEvent(AWTEvent e) {
		super.processEvent(e);
		if (e instanceof MouseEvent) {
			if (!isFocusOwner()) {
				requestFocus();
			}
		} else if (e instanceof KeyEvent) {
			if (!isFocusOwner()) {
				requestFocus();
			}
		}
	}

	public String getActionCommand() {
		return actionName;
	}

	public void setActionCommand(String name) {
		this.actionName = name;
	}

	public Thread getMainLoop() {
		return mainLoop;
	}

	public void mainLoop() {
		mainLoop = context.createThread(this);
		try {
			mainLoop.setPriority(Thread.NORM_PRIORITY);
		} catch (SecurityException ex) {
		}
		context.setAnimationThread(mainLoop);
		mainLoop.start();
	}

	public void mainStop() {
		this.mainLoop = null;
	}

	public void startPaint() {
		this.isStart = true;
	}

	public void endPaint() {
		this.isStart = false;
	}

	public void setFPS(long frames) {
		this.maxFrames = frames;
		this.offsetTime = (long) (1.0 / maxFrames * MAX_INTERVAL);
	}

	public long getMaxFPS() {
		return this.maxFrames;
	}

	public long getCurrentFPS() {
		return this.curFPS;
	}

	public void setShowFPS(boolean isFPS) {
		this.isFPS = isFPS;
	}

	public boolean isShowLogo() {
		return LSystem.isLogo;
	}

	public void setShowLogo(boolean showLogo) {
		LSystem.isLogo = showLogo;
	}

	public boolean isShowMemory() {
		return isMemory;
	}

	public void setShowMemory(boolean showMemory) {
		this.isMemory = showMemory;
	}

	public Image getLogo() {
		return logo;
	}

	public void setLogo(Image logo) {
		this.logo = logo;
	}

	public void setLogo(String fileName) {
		this.setLogo(GraphicsUtils.loadNotCacheImage(fileName));
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * 返回获得的输入法请求
	 */
	public InputMethodRequests getInputMethodRequests() {
		return this;
	}

	/**
	 * 返回当前用户输入的文本数据
	 */
	public AttributedCharacterIterator getDisplayText() {
		if (composedText == null) {
			return super.getDisplayText();
		} else {
			return EMPTY_TEXT;
		}
	}

	/**
	 * 返回当前插入符所在位置
	 */
	public TextHitInfo getCaret() {
		if (composedText == null) {
			return super.getCaret();
		} else if (caret == null) {
			return null;
		} else {
			return caret.getOffsetHit(getCommittedTextLength());
		}
	}

	/**
	 * 触发通过输入法引起的文本输入
	 */
	public void inputMethodTextChanged(InputMethodEvent e) {
		int committedCharacterCount = e.getCommittedCharacterCount();
		AttributedCharacterIterator text = e.getText();
		composedText = null;
		char c;
		if (text != null) {
			int toCopy = committedCharacterCount;
			c = text.first();
			while (toCopy-- > 0) {
				insertCharacter(c);
				c = text.next();
			}
			handler.changeText(getCommittedString());
			if (text.getEndIndex()
					- (text.getBeginIndex() + committedCharacterCount) > 0) {
				composedTextString = new AttributedString(text, text
						.getBeginIndex()
						+ committedCharacterCount, text.getEndIndex(),
						IM_ATTRIBUTES);
				composedTextString.addAttribute(TextAttribute.FONT, getFont());
				composedText = composedTextString.getIterator();
			}
		}
		e.consume();
		invalidateTextLayout();
		caret = e.getCaret();
		clear();
	}

	/**
	 * 获得插入点修改后的位置
	 */
	public void caretPositionChanged(InputMethodEvent e) {
		caret = e.getCaret();
		e.consume();
	}

	/**
	 * 获得输入文本定位
	 */
	public Rectangle getTextLocation(TextHitInfo offset) {
		Rectangle rectangle;
		if (offset == null) {
			rectangle = getCaretRectangle();
		} else {
			TextHitInfo globalOffset = offset
					.getOffsetHit(getCommittedTextLength());
			rectangle = getCaretRectangle(globalOffset);
		}
		Point location = getLocationOnScreen();
		rectangle.translate(location.x, location.y);
		return rectangle;
	}

	/**
	 * 获得指定x,y的偏移坐标
	 */
	public TextHitInfo getLocationOffset(int x, int y) {
		Point location = getLocationOnScreen();
		Point textOrigin = getTextOrigin();
		x -= location.x + textOrigin.x;
		y -= location.y + textOrigin.y;
		TextLayout textLayout = getTextLayout();
		if (textLayout != null && textLayout.getBounds().contains(x, y)) {
			return textLayout.hitTestChar(x, y).getOffsetHit(
					-getCommittedTextLength());
		} else {
			return null;
		}
	}

	public int getInsertPositionOffset() {
		return getCommittedTextLength();
	}

	public AttributedCharacterIterator getCommittedText(int beginIndex,
			int endIndex, Attribute[] attributes) {
		return getCommittedText(beginIndex, endIndex);
	}

	public AttributedCharacterIterator cancelLatestCommittedText(
			Attribute[] attributes) {
		return null;
	}

	public AttributedCharacterIterator getSelectedText(Attribute[] attributes) {
		return EMPTY_TEXT;
	}

	public boolean isSupportHardware() {
		return isSupportHardware;
	}

	public IHandler getHandler() {
		return handler;
	}

}
