/*
 * Copyright 2009 Sikirulai Braheem
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package com.bramosystems.oss.player.core.client.ui;

import com.bramosystems.oss.player.core.client.spi.PlayerWidget;
import com.bramosystems.oss.player.core.client.playlist.PlaylistManager;
import com.bramosystems.oss.player.core.client.playlist.MRL;
import com.bramosystems.oss.player.core.client.*;
import com.bramosystems.oss.player.core.client.MediaInfo.MediaInfoKey;
import com.bramosystems.oss.player.core.client.impl.*;
import com.bramosystems.oss.player.core.client.impl.CorePlayerProvider;
import com.bramosystems.oss.player.core.event.client.*;
import com.bramosystems.oss.player.core.event.client.PlayerStateEvent.State;
import com.bramosystems.oss.player.core.client.spi.Player;
import com.bramosystems.oss.player.util.client.RegExp;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import java.util.List;

/**
 * Widget to embed Windows Media Player&trade; plugin.
 *
 * <h3>Usage Example</h3>
 *
 * <p>
 * <code><pre>
 * SimplePanel panel = new SimplePanel();   // create panel to hold the player
 * Widget player = null;
 * try {
 *      // create the player
 *      player = new WinMediaPlayer("www.example.com/mediafile.wma");
 * } catch(PluginVersionException e) {
 *      // catch plugin version exception and alert user, possibly providing a link
 *      // to the plugin download page.
 *      player = new HTML(".. some nice message telling the user to download plugin first ..");
 * } catch(PluginNotFoundException e) {
 *      // catch PluginNotFoundException and tell user to download plugin, possibly providing
 *      // a link to the plugin download page.
 *      player = new HTML(".. another kind of message telling the user to download plugin..");
 * }
 *
 * panel.setWidget(player); // add player to panel.
 * </pre></code>
 *
 * <p><a name='non-ie-browser'><h3>Embedding in non-IE browsers</h3></a>
 * As of version 1.2, this widget requires the <b>Windows Media Player plugin for Firefox</b> in
 * non-IE browsers.  The plugin provides javascript support for the underlying Windows Media
 * Player&trade;.  However, this requirement can be suppressed if the need is simply
 * to embed Windows Media Player&trade; without any programmatic access.  In this case, all method
 * calls are ignored.
 *
 * @author Sikirulai Braheem
 */
@Player(name = "WinMediaPlayer", providerFactory = CorePlayerProvider.class, minPluginVersion = WinMediaPlayer.reqVer)
public class WinMediaPlayer extends AbstractMediaPlayer implements PlaylistSupport {

    final static String reqVer = "1.1.1";
    private static WMPStateManager stateManager;
    private static String D__HEIGHT = "50px";
    private WMPStateManager.EventProcessor eventProcessor;
    private WinMediaPlayerImpl impl;
    private PlayerWidget playerWidget;
    private String playerId, _width, _height;
    private boolean isEmbedded, resizeToVideoSize;
    private UIMode uiMode;
    private WMPPlaylistManager playlistManager;
    private LoopManager loopManager;

    private WinMediaPlayer(EmbedMode embedMode, boolean autoplay)
            throws PluginNotFoundException, PluginVersionException {
        CorePlayerProvider cwf = (CorePlayerProvider) getWidgetFactory("core");
        if (embedMode.equals(EmbedMode.PROGRAMMABLE) && !cwf.isWMPProgrammableEmbedModeSupported()) {
            throw new PluginNotFoundException(Plugin.WinMediaPlayer, "'Media Player plugin for Firefox' is required");
        }

        PluginVersion req;
        try {
            req = PluginVersion.get(reqVer);
            PluginVersion v = PlayerUtil.getWindowsMediaPlayerPluginVersion();
            if (v.compareTo(req) < 0) {
                throw new PluginVersionException(Plugin.WinMediaPlayer, req.toString(), v.toString());
            }
        } catch (RegExp.RegexException ex) {
            throw new PluginNotFoundException(Plugin.WinMediaPlayer);
        }

        playerId = DOM.createUniqueId().replace("-", "");
        if (stateManager == null) {
            stateManager = GWT.create(WMPStateManager.class);
        }

        playlistManager = new WMPPlaylistManager(autoplay);
        loopManager = new LoopManager(new LoopManager.LoopCallback() {

            @Override
            public void onLoopFinished() {
                fireDebug("Media playback finished");
                playlistManager.loadNext();
                firePlayStateEvent(PlayStateEvent.State.Finished, playlistManager.getPlaylistIndex());
            }

            @Override
            public void repeatPlay() {
                playlistManager.play(playlistManager.getPlaylistIndex());
            }

            @Override
            public void playNextLoop() {
                try {
                    playlistManager.playNext(true);
                } catch (PlayException ex) {
                }
            }

            @Override
            public void playNextItem() throws PlayException {
                playlistManager.playNext();
            }
        });
        eventProcessor = stateManager.init(playerId, playlistManager, new WMPStateManager.WMPImplCallback() {

            @Override
            public WinMediaPlayerImpl getImpl() {
                return impl;
            }
        });
        resizeToVideoSize = false;
    }

    /**
     * Constructs <code>WinMediaPlayer</code> with the specified {@code height} and
     * {@code width} to playback media located at {@code mediaURL}. Media playback
     * begins automatically if {@code autoplay} is {@code true}.
     *
     * <p> The {@code embedMode} parameter is provided to permit using the widget without any other
     * programmatic access.
     *
     * <p> {@code height} and {@code width} are specified as CSS units. A value of {@code null}
     * for {@code height} or {@code width} puts the player in embedded mode.  When in embedded mode,
     * the player is made invisible on the page and media state events are propagated to registered
     * listeners only.  This is desired especially when used with custom sound controls.  For custom
     * video control, specify valid CSS values for {@code height} and {@code width} but hide the
     * player controls with {@code setControllerVisible(false)}.
     *
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to start playing automatically, {@code false} otherwise
     * @param height the height of the player
     * @param width the width of the player.
     * @param embedMode the embed mode of the widget
     *
     * @throws PluginVersionException if the required Windows Media Player plugin version is not installed on the client.
     * @throws PluginNotFoundException if the Windows Media Player plugin is not installed on the client.
     *
     * @since 1.2
     * @see EmbedMode
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public WinMediaPlayer(String mediaURL, final boolean autoplay, String height, String width,
            EmbedMode embedMode) throws PluginNotFoundException, PluginVersionException {
        this(embedMode, autoplay);

        _height = height;
        _width = width;

        playerWidget = new PlayerWidget("core", Plugin.WinMediaPlayer.name(), playerId, "", false);
        initWidget(playerWidget);

        isEmbedded = (height == null) || (width == null);
        if (!isEmbedded) {
             addMediaInfoHandler(new MediaInfoHandler() {

                @Override
                public void onMediaInfoAvailable(MediaInfoEvent event) {
                    MediaInfo info = event.getMediaInfo();
                    if (info.getAvailableItems().contains(MediaInfoKey.VideoHeight)
                            || info.getAvailableItems().contains(MediaInfoKey.VideoWidth)) {
                        checkVideoSize(Integer.parseInt(info.getItem(MediaInfoKey.VideoHeight)) + 50,
                                Integer.parseInt(info.getItem(MediaInfoKey.VideoWidth)));
                    }
                }
            });
            setUIMode(UIMode.FULL);
        } else {
            _width = "1px";
            _height = "1px";
            setConfigParameter(CoreConfigParameter.WMPUIMode, UIMode.INVISIBLE);
        }
        playerWidget.setSize("100%", _height);
        setWidth(_width);

        playlistManager.addToPlaylist(mediaURL);
    }

    /**
     * Constructs <code>WinMediaPlayer</code> with the specified {@code height} and
     * {@code width} to playback media located at {@code mediaURL}. Media playback
     * begins automatically if {@code autoplay} is {@code true}.
     *
     * <p> {@code height} and {@code width} are specified as CSS units. A value of {@code null}
     * for {@code height} or {@code width} puts the player in embedded mode.  When in embedded mode,
     * the player is made invisible on the page and media state events are propagated to registered
     * listeners only.  This is desired especially when used with custom sound controls.  For custom
     * video control, specify valid CSS values for {@code height} and {@code width} but hide the
     * player controls with {@code setControllerVisible(false)}.
     *
     * <p>This is the same as calling {@code WinMediaPlayer(mediaURL, true, height, width, EmbedMode.PROGRAMMABLE)}</p>
     *
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to start playing automatically, {@code false} otherwise
     * @param height the height of the player
     * @param width the width of the player.
     *
     * @throws PluginVersionException if the required Windows Media Player plugin version is not installed on the client.
     * @throws PluginNotFoundException if the Windows Media Player plugin is not installed on the client.
     */
    public WinMediaPlayer(String mediaURL, boolean autoplay, String height, String width)
            throws PluginNotFoundException, PluginVersionException {
        this(mediaURL, autoplay, height, width, EmbedMode.PROGRAMMABLE);
    }

    /**
     * Constructs <code>WinMediaPlayer</code> to automatically playback media located at
     * {@code mediaURL} using the default height of 50px and width of 100%.
     *
     * <p>This is the same as calling {@code WinMediaPlayer(mediaURL, true, "50px", "100%")}</p>
     *
     * @param mediaURL the URL of the media to playback
     *
     * @throws PluginVersionException if the required Windows Media Player plugin version is not installed on the client.
     * @throws PluginNotFoundException if the Windows Media Player plugin is not installed on the client.
     */
    public WinMediaPlayer(String mediaURL) throws PluginNotFoundException, PluginVersionException {
        this(mediaURL, true, D__HEIGHT, "100%");
    }

    /**
     * Constructs <code>WinMediaPlayer</code> to playback media located at {@code mediaURL} using
     * the default height of 50px and width of 100%. Media playback begins automatically if
     * {@code autoplay} is {@code true}.
     *
     * <p> This is the same as calling {@code WinMediaPlayer(mediaURL, autoplay, "50px", "100%")}
     *
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to start playing automatically, {@code false} otherwise
     *
     * @throws PluginVersionException if the required Windows Media Player plugin version is not installed on the client.
     * @throws PluginNotFoundException if the Windows Media Player plugin is not installed on the client.
     */
    public WinMediaPlayer(String mediaURL, boolean autoplay) throws 
            PluginNotFoundException, PluginVersionException {
        this(mediaURL, autoplay, D__HEIGHT, "100%");
    }

    /**
     * Quick resizing-fix for non-IE browsers. Method replaces player object
     * with another using video size of previously loaded media metadata.
     *
     * @param replaceWidget
     */
    private void setupPlayer(boolean replaceWidget) {
        if (replaceWidget) {
            eventProcessor.setEnabled(false);
            playerWidget.replace("core", Plugin.WinMediaPlayer.name(), playerId, playlistManager.getCurrentItem(), false);
        }

        impl = (WinMediaPlayerImpl) WinMediaPlayerImpl.getPlayer(playerId);
        stateManager.registerMediaStateHandlers(impl);
        eventProcessor.setEnabled(true);
        if (uiMode != null) {
            setUIMode(uiMode);
        }
    }

    /**
     * Overridden to register player for plugin DOM events
     */
    @Override
    protected final void onLoad() {
        fireDebug("Windows Media Player plugin");
        setupPlayer(false);
        fireDebug("Plugin Version : " + impl.getPlayerVersion());
        firePlayerStateEvent(PlayerStateEvent.State.Ready);
        playlistManager._start();
    }

    /**
     * Overridden to release resources
     */
    @Override
    protected void onUnload() {
        try {
            stateManager.close(playerId);
            impl.close();
        } catch (Exception e) {
            // See Issue 38: suppress exceptions.
            // Browser should release resources not present in DOM anyway
        }
    }

    @Override
    public void loadMedia(String mediaURL) throws LoadException {
        checkAvailable();
        impl.setURL(mediaURL);
    }

    @Override
    public void playMedia() throws PlayException {
        checkAvailable();
        impl.play();
    }

    @Override
    public void stopMedia() {
        checkAvailable();
        stateManager.stop(playerId);
        impl.stop();
    }

    @Override
    public void pauseMedia() {
        checkAvailable();
        impl.pause();
    }

    @Override
    public long getMediaDuration() {
        checkAvailable();
        return (long) impl.getDuration();
    }

    @Override
    public double getPlayPosition() {
        checkAvailable();
        return impl.getCurrentPosition();
    }

    @Override
    public void setPlayPosition(double position) {
        checkAvailable();
        impl.setCurrentPosition(position);
    }

    @Override
    public double getVolume() {
        checkAvailable();
        return impl.getVolume() / (double) 100;
    }

    @Override
    public void setVolume(double volume) {
        checkAvailable();
        volume *= 100;
        impl.setVolume((int) volume);
        fireDebug("Volume set to " + ((int) volume) + "%");
    }

    private boolean isAvailable() {
        return isPlayerOnPage(playerId) && stateManager.isPlayerStateManaged(playerId);
    }

    private void checkAvailable() {
        if (!isAvailable()) {
            String message = "Player not available, create an instance";
            fireDebug(message);
            throw new IllegalStateException(message);
        }
    }

    /**
     * Displays or hides the player controls.
     */
    @Override
    public void setControllerVisible(boolean show) {
        UIMode mode = null;
        if (show) {
            mode = isEmbedded ? UIMode.INVISIBLE : UIMode.FULL;
        } else {
            mode = isEmbedded ? UIMode.INVISIBLE : UIMode.NONE;
        }
        setUIMode(mode);
    }

    /**
     * Checks whether the player controls are visible.
     */
    @Override
    public boolean isControllerVisible() {
        UIMode mode = getUIMode();
        return mode.equals(UIMode.FULL) || mode.equals(UIMode.MINI);
    }

    /**
     * Returns the number of times this player repeats playback before stopping.
     */
    @Override
    public int getLoopCount() {
        checkAvailable();
        return loopManager.getLoopCount();
    }

    /**
     * Sets the number of times the current media file should repeat playback before stopping.
     *
     * <p>As of version 1.0, if this player is not available on the panel, this method
     * call is added to the command-queue for later execution.
     */
    @Override
    public void setLoopCount(final int loop) {
        if (isAvailable()) {
            loopManager.setLoopCount(loop);
        } else {
            addToPlayerReadyCommandQueue("loopcount", new Command() {

                @Override
                public void execute() {
                    loopManager.setLoopCount(loop);
                }
            });
        }
    }

    @Override
    public int getVideoHeight() {
        checkAvailable();
        return impl.getVideoHeight();
    }

    @Override
    public int getVideoWidth() {
        checkAvailable();
        return impl.getVideoWidth();
    }

    @Override
    public void setResizeToVideoSize(boolean resize) {
        resizeToVideoSize = resize;
        if (isAvailable()) {
            // if player is on panel now update its size, otherwise
            // allow it to be handled by the MediaInfoHandler...
            checkVideoSize(getVideoHeight() + 50, getVideoWidth());
        }
    }

    @Override
    public boolean isResizeToVideoSize() {
        return resizeToVideoSize;
    }

    private void checkVideoSize(int vidHeight, int vidWidth) {
        String _h = _height, _w = _width;
        if (resizeToVideoSize) {
            if ((vidHeight > 0) && (vidWidth > 0)) {
                // adjust to video size ...
                _w = vidWidth + "px";
                _h = vidHeight + "px";
            } else {
                _h = D__HEIGHT;
            }
            fireDebug("Resizing Player : " + _w + " x " + _h);
        }

        // TODO: disable resizing for non-IE browsers for now, unstable with playlists
        if (stateManager.shouldRunResizeQuickFix()) {
            return;
        }

        playerWidget.setSize(_w, _h);
        setWidth(_w);

        if (!_height.equals(_h) && !_width.equals(_w)) {
            if (stateManager.shouldRunResizeQuickFix()) {
                // TODO check this properly for playlists ...
                setupPlayer(true); // replace player ...
                playlistManager.play(playlistManager.getPlaylistIndex());
            }
            firePlayerStateEvent(State.DimensionChangedOnVideo);
        }
    }

    /**
     * Retrieves the current media library access rights of the player
     *
     * @return current media access rights
     * @since 1.0
     */
    public MediaAccessRights getMediaAccessRights() {
        checkAvailable();
        return MediaAccessRights.valueOf(impl.getMediaAccessRight().toLowerCase());
    }

    /**
     * Sets the media library access rights level of the player.
     *
     * <p>If the player is not attached to a player, this call is scheduled for execution
     * when the underlying plugin is initialized.
     *
     * <p><em>Note:</em> The request succeeds IF AND ONLY IF the user grants this permission.
     * <p><em>Note:</em> This operation ALWAYS FAIL on browsers using the Windows Media Player
     * plugin for Firefox.
     *
     * @param accessRights access level
     * @since 1.0
     */
    public void setMediaAccessRights(final MediaAccessRights accessRights) {
        if (isAvailable()) {
            impl.requestMediaAccessRight(accessRights.name().toLowerCase());
        } else {
            addToPlayerReadyCommandQueue("accessright", new Command() {

                @Override
                public void execute() {
                    impl.requestMediaAccessRight(accessRights.name().toLowerCase());
                }
            });
        }
    }

    /**
     * Sets the UI mode of the player
     *
     * <p>If the player is not attached to a player, this call is scheduled for execution
     * when the underlying plugin is initialized.
     *
     * <p><b>Note - Google Chrome 3:</b> This method fails! As of version 1.1 use
     * {@linkplain #setConfigParameter(com.bramosystems.oss.player.core.client.ConfigParameter, java.lang.Object)} instead.</p>
     *
     * @since 1.0
     * @param uiMode the UI mode to set
     */
    public void setUIMode(final UIMode uiMode) {
        this.uiMode = uiMode;
        if (isAvailable()) {
            impl.setUIMode(uiMode.name().toLowerCase());
        } else {
            addToPlayerReadyCommandQueue("uimode", new Command() {

                @Override
                public void execute() {
                    impl.setUIMode(uiMode.name().toLowerCase());
                }
            });
        }
    }

    /**
     * Retrieves the current UI mode of the player
     *
     * @return current UI mode
     * @since 1.0
     */
    public UIMode getUIMode() {
        checkAvailable();
        try {
            return UIMode.valueOf(impl.getUIMode().toUpperCase());
        } catch (Exception e) {
            // Chrome 3 UIMode workaround ...
            String wm = playerWidget.getParam("uimode");
            if (wm != null) {
                return UIMode.valueOf(wm.toUpperCase());
            } else {
                return null;
            }
        }
    }

    @Override
    public <C extends ConfigParameter> void setConfigParameter(C param, Object value) {
        super.setConfigParameter(param, value);
        if (param.getName().equals(CoreConfigParameter.WMPUIMode.getName())) {
            if (isEmbedded) {
                playerWidget.addParam("uimode", UIMode.INVISIBLE.name().toLowerCase());
                return;
            }

            if (value != null) {
                playerWidget.addParam("uimode", ((UIMode) value).name().toLowerCase());
            } else {
                playerWidget.removeParam("uimode");
            }
        }
    }

    @Override
    public void setRate(final double rate) {
        if (isPlayerOnPage(playerId)) {
            impl.setRate(rate);
        } else {
            addToPlayerReadyCommandQueue("rate", new Command() {

                @Override
                public void execute() {
                    impl.setRate(rate);
                }
            });
        }
    }

    @Override
    public double getRate() {
        checkAvailable();
        return impl.getRate();
    }

    @Override
    public void setShuffleEnabled(final boolean enable) {
        if (isPlayerOnPage(playerId)) {
            playlistManager.setShuffleEnabled(enable);
        } else {
            addToPlayerReadyCommandQueue("shuffle", new Command() {

                @Override
                public void execute() {
                    playlistManager.setShuffleEnabled(enable);
                }
            });
        }
    }

    @Override
    public boolean isShuffleEnabled() {
        checkAvailable();
        return playlistManager.isShuffleEnabled();
    }

    @Override
    public void addToPlaylist(String mediaURL) {
        playlistManager.addToPlaylist(mediaURL);
    }

    @Override
    public void addToPlaylist(MRL mediaLocator) {
        playlistManager.addToPlaylist(mediaLocator);
    }

    @Override
    public void addToPlaylist(String... mediaURLs) {
        playlistManager.addToPlaylist(mediaURLs);
    }

    @Override
    public void addToPlaylist(List<MRL> mediaLocators) {
        playlistManager.addToPlaylist(mediaLocators);
    }

    @Override
    public void removeFromPlaylist(int index) {
        checkAvailable();
        playlistManager.removeFromPlaylist(index);
    }

    @Override
    public void clearPlaylist() {
        checkAvailable();
        playlistManager.clearPlaylist();
    }

    @Override
    public void playNext() throws PlayException {
        checkAvailable();
        playlistManager.playNext();
    }

    @Override
    public void playPrevious() throws PlayException {
        checkAvailable();
        playlistManager.playPrevious();
    }

    @Override
    public void play(int index) throws IndexOutOfBoundsException {
        checkAvailable();
        playlistManager.play(index);
    }

    @Override
    public int getPlaylistSize() {
        checkAvailable();
        return playlistManager.getPlaylistSize();
    }

    @Override
    public RepeatMode getRepeatMode() {
        return loopManager.getRepeatMode();
    }

    @Override
    public void setRepeatMode(RepeatMode mode) {
        loopManager.setRepeatMode(mode);
    }

    /**
     * An enum of Library Access levels of the Windows Media Player&trade; plugin.
     *
     * @since 1.0
     * @author Sikirulai Braheem
     */
    public static enum MediaAccessRights {

        /**
         * No access
         */
        NONE,
        /**
         * Read only access
         */
        READ,
        /**
         * Read/write access
         */
        FULL
    }

    /**
     * An enum of user interface modes of the Windows Media Player&trade; plugin.
     *
     * <p>The mode indicates which controls are shown on the user interface.
     *
     * @since 1.0
     * @author Sikirulai Braheem
     */
    public static enum UIMode implements ConfigValue {

        /**
         * The player is embedded without any visible user interface
         * (controls, video or visualization window).
         */
        INVISIBLE,
        /**
         * The player is embedded without controls, and with only the video or
         * visualization window displayed.
         */
        NONE,
        /**
         * The player is embedded with the status window, play/pause, stop, mute, and
         * volume controls shown in addition to the video or visualization window.
         */
        MINI,
        /**
         * The player is embedded with the status window, seek bar, play/pause,
         * stop, mute, next, previous, fast forward, fast reverse, and
         * volume controls in addition to the video or visualization window.
         */
        FULL
    }

    /**
     * An enum of embed modes of the WinMediaPlayer widget.
     *
     * <p>The WinMediaPlayer widget requires the <b>Windows Media Player plugin for Firefox</b> which
     * provides extensive javascript support in non-IE browsers.  However, this requirement can be
     * suppressed if the need is simply to embed Windows Media Player&trade; without any programmatic
     * access.
     *
     * @author Sikirulai Braheem
     * @since 1.2
     */
    public static enum EmbedMode {

        /**
         * The widget is used to embed Windows Media Player&trade; without any programmatic access.
         *
         * <p>In this mode, all method calls are ignored
         */
        EMBED_ONLY,
        /**
         * The widget is used to embed Windows Media Player&trade; with full programmatic access. This mode
         * requires the Windows Media Player plugin for Firefox in non-IE browsers.
         */
        PROGRAMMABLE
    }

    private class WMPPlaylistManager extends PlaylistManager
            implements WMPStateManager.WMPEventCallback {

        private boolean _autoplay, shouldPlay, firstRun;

        public WMPPlaylistManager(boolean autoplay) {
            _autoplay = autoplay;
            firstRun = true;
        }

        private void _start() {
            flushMessageCache();
            if (_autoplay) {
                super.play(0);
            } else {
                super.load(0);
            }
        }

        @Override
        protected PlayerCallback initCallback() {
            return new PlayerCallback() {

                @Override
                public void play() throws PlayException {
                    shouldPlay = true;
                    impl.play();
                }

                @Override
                public void load(String url) {
                    impl.setAutoStart(!firstRun);
                    fireDebug("Loading media @ " + url);
                    impl.setURL(url);
                    if (firstRun) {
                        firstRun = false;
                    }
                }

                @Override
                public void onDebug(String message) {
                    fireDebug(message);
                }
            };
        }

        @Override
        public void onLoadingProgress(double progress) {
            fireLoadingProgress(progress);
        }

        @Override
        public void onError(String message) {
            fireError(message);
        }

        @Override
        public void onInfo(String message) {
            fireDebug(message);
        }

        @Override
        public void onBuffering(boolean started) {
            firePlayerStateEvent(started ? State.BufferingStarted : State.BufferingFinished);
        }

        @Override
        public void onStop() {
            firePlayStateEvent(PlayStateEvent.State.Stopped, getPlaylistIndex());
        }

        @Override
        public void onPlay() {
            fireDebug("Media playback started");
            firePlayStateEvent(PlayStateEvent.State.Started, getPlaylistIndex());
        }

        @Override
        public void onPaused() {
            firePlayStateEvent(PlayStateEvent.State.Paused, getPlaylistIndex());
        }

        @Override
        public void onEnded() {
            shouldPlay = false;
            loopManager.notifyPlayFinished();
        }

        @Override
        public void onMediaInfo(MediaInfo info) {
            fireMediaInfoAvailable(info);
        }

        @Override
        public void onOpening() {
        }

        @Override
        public void onReady() {
            if (shouldPlay && (impl.getCurrentPosition() == 0)) {
                impl.play();
            }
        }

        @Override
        public void onNativeEvent(NativeEvent event) {
            DomEvent.fireNativeEvent(event, WinMediaPlayer.this);
        }
    }
}
