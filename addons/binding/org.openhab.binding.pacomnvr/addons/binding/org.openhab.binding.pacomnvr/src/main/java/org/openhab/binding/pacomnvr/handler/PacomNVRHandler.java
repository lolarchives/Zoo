/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.pacomnvr.handler;

import static org.openhab.binding.pacomnvr.PacomNVRBindingConstants.CHANNEL_IMAGE;
import static org.openhab.binding.pacomnvr.PacomNVRBindingConstants.CHANNEL_SWITCH;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.RawType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link PacomNVRHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 * 
 * @author oscar - Initial contribution
 */
public class PacomNVRHandler extends BaseThingHandler {

	private Logger logger = LoggerFactory.getLogger(PacomNVRHandler.class);

	private Integer refresh;

	private Integer camera;

	private ScheduledFuture<?> refreshJob;

	private ChannelUID channelSwitchUID;

	private ChannelUID channelImageUID;

	private String sampleVideoPath = null;

	public PacomNVRHandler(Thing thing) {
		super(thing);

	}

	@Override
	public void initialize() {
		super.initialize();

		channelSwitchUID = new ChannelUID(this.getThing().getUID(),
				CHANNEL_SWITCH);

		channelImageUID = new ChannelUID(this.getThing().getUID(),
				CHANNEL_IMAGE);

		final Configuration config = getThing().getConfiguration();
		try {
			this.camera = Integer.valueOf(config.get("camera").toString());
		} catch (Exception e) {
			camera = new Integer(1);
			logger.error("Failed to read 'camera' config property", e);
		}
		try {
			this.refresh = Integer.valueOf(config.get("refresh").toString());
		} catch (Exception e) {
			refresh = new Integer(1);
			logger.error("Failed to read 'refresh' config property", e);
		}

		try {
			this.sampleVideoPath = config.get("mp4videoPath").toString();
		} catch (Exception e) {
			this.sampleVideoPath = "c:/temp/HDDVDTrailer.mp4";
			logger.error("Failed to read 'sampleVideoPath' config property", e);
		}

		logger.debug("Initialized: " + this);

		enableVideoStreaming(false);
	}

	private void showNoVideo() {
		InputStream stream = null;
		try {
			stream = PacomNVRHandler.class.getResourceAsStream("/novideo.png");
			if (stream != null) {
				final State state = new RawType(toByteArray(stream));
				updateState(channelImageUID, state);
				updateState(channelSwitchUID, OnOffType.OFF);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}

			} catch (IOException e) {
			}
		}
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
		if (channelUID.getId().equals(CHANNEL_SWITCH)) {
			if (command instanceof OnOffType) {
				enableVideoStreaming(OnOffType.ON == command);
			}
		}
	}

	private void enableVideoStreaming(boolean enable) {
		if (enable) {
			logger.debug("Video streaming ON");
			final Runnable runnable = new Runnable() {
				public void run() {
					try {
						updateFrame();
					} catch (Exception e) {
						logger.error("Failed to udpate frame", e);
					}
				}

			};
			this.refreshJob = this.scheduler.scheduleAtFixedRate(runnable, 0L,
					this.refresh.intValue(), TimeUnit.SECONDS);

		} else {
			logger.debug("Video streaming OFF");
			if (refreshJob != null) {
				refreshJob.cancel(true);
			}
			showNoVideo();
		}
	}

	private void updateFrame() {
		try {
			UUID randomUUID = UUID.randomUUID();
			long start = System.currentTimeMillis();
			logger.debug("Update frame: " + randomUUID);
			updateState(channelImageUID, getNextFrame());
			logger.debug("Frame updated: " + randomUUID + ", time (sec):"
					+ (System.currentTimeMillis() - start) / 1000);
		} catch (Exception e) {
			logger.error("Failed to read frame from path: " + sampleVideoPath,
					e);
		}
	}

	static byte[] toByteArray(InputStream input) throws IOException {
		try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			int n = 0;
			byte[] buffer = new byte[512];
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
			return output.toByteArray();
		}
	}

	private State getNextFrame() throws Exception {
		final File input = new File(sampleVideoPath);
		try (FileInputStream stream = new FileInputStream(input)) {
			return new RawType(toByteArray(stream));
		}
	}

	@Override
	public void dispose() {
		if (this.refreshJob != null) {
			this.refreshJob.cancel(true);
		}
	}

	@Override
	public String toString() {
		return "PacomNVRHandler [refresh=" + refresh + ", camera=" + camera
				+ ", channelSwitchUID=" + channelSwitchUID
				+ ", channelImageUID=" + channelImageUID + ", sampleVideoPath="
				+ sampleVideoPath + "]";
	}

}
