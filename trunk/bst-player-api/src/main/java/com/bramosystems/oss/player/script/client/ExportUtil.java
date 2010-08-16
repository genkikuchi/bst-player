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
 */
package com.bramosystems.oss.player.script.client;

import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.bramosystems.oss.player.core.client.ConfigParameter;
import com.bramosystems.oss.player.core.client.LoadException;
import com.bramosystems.oss.player.core.client.PlayException;
import com.bramosystems.oss.player.core.client.PlaylistSupport;
import com.bramosystems.oss.player.core.client.Plugin;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.bramosystems.oss.player.core.client.TransparencyMode;
import com.bramosystems.oss.player.core.client.skin.MediaSeekBar;
import com.bramosystems.oss.player.core.client.ui.WinMediaPlayer;
import com.bramosystems.oss.player.core.event.client.DebugEvent;
import com.bramosystems.oss.player.core.event.client.DebugHandler;
import com.bramosystems.oss.player.core.event.client.LoadingProgressEvent;
import com.bramosystems.oss.player.core.event.client.LoadingProgressHandler;
import com.bramosystems.oss.player.core.event.client.PlayStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayStateHandler;
import com.bramosystems.oss.player.core.event.client.PlayerStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayerStateHandler;
import com.bramosystems.oss.player.core.event.client.SeekChangeEvent;
import com.bramosystems.oss.player.core.event.client.SeekChangeHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;

/**
 * Utility class to export the player and seek bar widgets as Javascript objects
 * that can be used in non-GWT applications.
 *
 * <p>
 * The objects are bound to the <code>bstplayer</code> namespace.
 * </p>
 *
 * @author Sikirulai Braheem <sbraheem at bramosystems dot com>
 * @see <a href="package-summary.html#export">Exporting the player widgets</a>
 */
public class ExportUtil {

    /**
     * Calls the <code>onBSTPlayerReady()</code> javascript function on the
     * host page.
     *
     * <p>This method should be called after all widgets have been exported</p>
     */
    public static native final void signalAPIReady() /*-{
    $wnd.onBSTPlayerReady();
    }-*/;

    /**
     * Exports the {@linkplain AbstractMediaPlayer} implementation as Javascript object.
     *
     * <p>
     * The player is made available as a <code>bstplayer.Player</code> Javascript
     * object. The object supports all the public methods defined in the
     * {@linkplain AbstractMediaPlayer} class.
     * </p>
     *
     * <p>
     * <b>Note:</b> The {@code bstplayer.Player} object defines an
     * {@code addEventListener(eventName, function)} method, instead of the {@code addXXXHandler()}
     * methods.
     * </p>
     */
    public static native final void exportPlayer() /*-{
    if($wnd.bstplayer == null){
    $wnd.bstplayer = new Object();
    }

    $wnd.bstplayer.Player = function(plugin, mediaURL, autoplay, width, height, options){
    // init the player ...
    var _player = @com.bramosystems.oss.player.script.client.ExportUtil::getPlayer(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(plugin,mediaURL,autoplay,width,height,options);
    
    this.inject = function(containerId) {
    @com.bramosystems.oss.player.script.client.ExportUtil::inject(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;)(_player, containerId);
    }
    this.setResizeToVideoSize = function(resize) {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::setResizeToVideoSize(Z)(resize);
    }
    this.isResizeToVideoSize = function() {
    return _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::isResizeToVideoSize()();
    }
    this.getVideoHeight = function() {
    return _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::getVideoHeight()();
    }
    this.getVideoWidth = function() {
    return _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::getVideoWidth()();
    }
    this.loadMedia = function(mediaURL) {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::loadMedia(Ljava/lang/String;)(mediaURL);
    }
    this.playMedia = function() {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::playMedia()();
    }
    this.stopMedia = function() {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::stopMedia()();
    }
    this.pauseMedia = function() {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::pauseMedia()();
    }
    this.close = function() {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::close()();
    }
    this.getMediaDuration = function() {
    return _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::getMediaDurationImpl()();
    }
    this.getPlayPosition = function() {
    return _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::getPlayPosition()();
    }
    this.setPlayPosition = function(position) {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::setPlayPosition(D)(position);
    }
    this.getVolume = function() {
    return _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::getVolume()();
    }
    this.setVolume = function(volume) {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::setVolume(D)(volume);
    }
    this.showLogger = function(show) {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::showLogger(Z)(show);
    }
    this.setControllerVisible = function(show) {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::setControllerVisible(Z)(show);
    }
    this.isControllerVisible = function() {
    return _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::isControllerVisible()();
    }
    this.setLoopCount = function(loop) {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::setLoopCount(I)(loop);
    }
    this.getLoopCount = function() {
    return _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::getLoopCount()();
    }
    this.addEventListener = function(name, _function) {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::addEventListener(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(name,_function);
    }
    this.setConfigParameter = function(param, value) {
    _player.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptPlayer::setConfigParameter(Ljava/lang/String;Ljava/lang/String;)(param,value);
    }
    }
    }-*/;

    /**
     * Exports a {@linkplain MediaSeekBar} implementation as Javascript object.
     *
     * <p>
     * The seekbar is made available as a <code>bstplayer.SeekBar</code> Javascript
     * object. The object supports all the public methods defined in the
     * {@linkplain MediaSeekBar} class.
     * </p>
     *
     * <p>
     * <b>Note:</b> The {@code bstplayer.SeekBar} object defines an
     * {@code addEventListener(eventName, function)} method, instead of the {@code addXXXHandler()}
     * methods.
     * </p>
     */
    public static native final void exportSeekBar() /*-{
    if($wnd.bstplayer == null){
    $wnd.bstplayer = new Object();
    }

    $wnd.bstplayer.SeekBar = function(height, containerId, options){
    // init the seekbar ...
    var _seek = @com.bramosystems.oss.player.script.client.ExportUtil::getSeekBar(ILjava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(height,containerId,options);

    this.setLoadingProgress = function(progress) {
    _seek.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptSeekBar::setLoadingProgress(D)(progress);
    }
    this.setPlayingProgress = function(progress) {
    _seek.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptSeekBar::setPlayingProgress(D)(progress);
    }
    this.addEventListener = function(name, _function) {
    _seek.@com.bramosystems.oss.player.script.client.ExportUtil.ScriptSeekBar::addEventListener(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(name,_function);
    }
    }
    }-*/;

    private static ScriptPlayer getPlayer(String plugin, String url, boolean autoplay, String width,
            String height, JavaScriptObject options) {
        return new ScriptPlayer(plugin, url, autoplay, width, height, options);
    }

    private static ScriptSeekBar getSeekBar(int height, String containerId, JavaScriptObject options) {
        return new ScriptSeekBar(height, containerId, options);
    }

    private static void inject(Widget widget, String containerId) {
        RootPanel.get(containerId).add(widget);
    }

    private static native void parseOptionsToMap(JavaScriptObject options, HashMap<String, String> map) /*-{
    for(x in options) {
    map.@java.util.HashMap::put(Ljava/lang/Object;Ljava/lang/Object;)(x, options[x]);
    }
    }-*/;

    private static void runCallback(JavaScriptObject _function, JavaScriptObject _event) {
        if (_function != null) {
            runCallbackImpl(_function, _event);
        }
    }

    private static native void runCallbackImpl(JavaScriptObject _function, JavaScriptObject _event) /*-{
    _function(_event);
    }-*/;

    private static native void putEventValue(JavaScriptObject _event, String param, String value) /*-{
    _event[param] = value;
    }-*/;

    private static native void putEventValue(JavaScriptObject _event, String param, double value) /*-{
    _event[param] = value;
    }-*/;
    private static final ExportProvider provider = GWT.create(ExportProvider.class);

    private static class ScriptPlayer extends AbstractMediaPlayer implements PlaylistSupport {

        private enum EventName {

            onPlayerState, onPlayState, onLoadingProgress, onMediaInfo, onError, onDebug
        }
        private AbstractMediaPlayer player;
        private HashMap<EventName, JavaScriptObject> eventHandlers;

        public ScriptPlayer(String plugin, String url, boolean autoplay, String width,
                String height, JavaScriptObject options) {

            Plugin _plugin = null;
            try {
                _plugin = Plugin.valueOf(plugin);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }

            try {
                HashMap<String, String> map = new HashMap<String, String>();
                if (options != null) {
                    parseOptionsToMap(options, map);
                }

                player = provider.getPlayer(_plugin, url, autoplay, width, height, map);
                initWidget(player);

                player.addDebugHandler(new DebugHandler() {

                    public void onDebug(DebugEvent event) {
                        switch (event.getMessageType()) {
                            case Info:
                                if (eventHandlers.containsKey(EventName.onDebug)) {
                                    JavaScriptObject evt = JavaScriptObject.createObject();
                                    putEventValue(evt, "message", event.getMessage());
                                    runCallback(eventHandlers.get(EventName.onDebug), evt);
                                }
                                break;
                            case Error:
                                if (eventHandlers.containsKey(EventName.onError)) {
                                    JavaScriptObject evt = JavaScriptObject.createObject();
                                    putEventValue(evt, "message", event.getMessage());
                                    runCallback(eventHandlers.get(EventName.onError), evt);
                                }
                                break;
                        }
                    }
                });
                player.addPlayerStateHandler(new PlayerStateHandler() {

                    public void onPlayerStateChanged(PlayerStateEvent event) {
                        JavaScriptObject evt = JavaScriptObject.createObject();
                        putEventValue(evt, "playerState", event.getPlayerState().name());
                        runCallback(eventHandlers.get(EventName.onPlayerState), evt);
                    }
                });
                player.addPlayStateHandler(new PlayStateHandler() {

                    public void onPlayStateChanged(PlayStateEvent event) {
                        JavaScriptObject evt = JavaScriptObject.createObject();
                        putEventValue(evt, "playState", event.getPlayState().name());
                        putEventValue(evt, "itemIndex", event.getItemIndex());
                        runCallback(eventHandlers.get(EventName.onPlayState), evt);
                    }
                });
                player.addLoadingProgressHandler(new LoadingProgressHandler() {

                    public void onLoadingProgress(LoadingProgressEvent event) {
                        JavaScriptObject evt = JavaScriptObject.createObject();
                        putEventValue(evt, "progress", event.getProgress());
                        runCallback(eventHandlers.get(EventName.onLoadingProgress), evt);
                    }
                });
/*                player.addMediaInfoHandler(new MediaInfoHandler() {

                    public void onMediaInfoAvailable(MediaInfoEvent event) {
                        JavaScriptObject evt = JavaScriptObject.createObject();
//                        putEventValue(evt, "playState", event.getMediaInfo().name());
                        runCallback(eventHandlers.get(EventName.onMediaInfo), evt);
                    }
                });
*/
            } catch (LoadException ex) {
            } catch (PluginNotFoundException ex) {
                initWidget(provider.getMissingPluginWidget());
            } catch (PluginVersionException ex) {
                initWidget(provider.getMissingPluginWidget());
            }

            eventHandlers = new HashMap<EventName, JavaScriptObject>();
        }

        @Override
        public void loadMedia(String mediaURL) throws LoadException {
            if (player == null) {
                return;
            }
            player.loadMedia(mediaURL);
        }

        @Override
        public void playMedia() throws PlayException {
            if (player == null) {
                return;
            }
            player.playMedia();
        }

        @Override
        public void stopMedia() {
            if (player == null) {
                return;
            }
            player.stopMedia();
        }

        @Override
        public void pauseMedia() {
            if (player == null) {
                return;
            }
            player.pauseMedia();
        }

        @Override
        public long getMediaDuration() {
            if (player == null) {
                return 0;
            }
            return player.getMediaDuration();
        }

        public double getMediaDurationImpl() {
            if (player == null) {
                return 0;
            }
            return player.getMediaDuration();
        }

        @Override
        public double getPlayPosition() {
            if (player == null) {
                return 0;
            }
            return player.getPlayPosition();
        }

        @Override
        public void setPlayPosition(double position) {
            if (player == null) {
                return;
            }
            player.setPlayPosition(position);
        }

        @Override
        public double getVolume() {
            if (player == null) {
                return 0;
            }
            return player.getVolume();
        }

        @Override
        public void setVolume(double volume) {
            if (player == null) {
                return;
            }
            player.setVolume(volume);
        }

        @Override
        public int getLoopCount() {
            if (player == null) {
                return 0;
            }
            return player.getLoopCount();

        }

        @Override
        public int getVideoHeight() {
            if (player == null) {
                return 0;
            }
            return player.getVideoHeight();
        }

        @Override
        public int getVideoWidth() {
            if (player == null) {
                return 0;
            }
            return player.getVideoWidth();
        }

        @Override
        public boolean isControllerVisible() {
            if (player == null) {
                return false;
            }
            return player.isControllerVisible();
        }

        @Override
        public boolean isResizeToVideoSize() {
            if (player == null) {
                return false;
            }
            return player.isResizeToVideoSize();
        }

        @Override
        public void setControllerVisible(boolean show) {
            if (player == null) {
                return;
            }
            player.setControllerVisible(show);
        }

        @Override
        public void setLoopCount(int loop) {
            if (player == null) {
                return;
            }
            player.setLoopCount(loop);
        }

        @Override
        public void setResizeToVideoSize(boolean resize) {
            if (player == null) {
                return;
            }
            player.setResizeToVideoSize(resize);
        }

        @Override
        public void showLogger(boolean show) {
            if (player == null) {
                return;
            }
            player.showLogger(show);
        }

        public void addEventListener(String eventName, JavaScriptObject callback) {
            try {
                EventName evnt = EventName.valueOf(eventName);
                eventHandlers.put(evnt, callback);
            } catch (Exception e) {
            }
        }

        public void setConfigParameter(String param, String value) {
            if (player == null) {
                return;
            }

            try {
                ConfigParameter cfg = ConfigParameter.valueOf(param);
                switch (cfg) {
                    case TransparencyMode:
                        player.setConfigParameter(cfg, TransparencyMode.valueOf(value));
                        break;
                    case WMPUIMode:
                        player.setConfigParameter(cfg, WinMediaPlayer.UIMode.valueOf(value));
                }
            } catch (Exception e) {
            }
        }

        public void setShuffleEnabled(boolean enable) {
            if (player == null) {
                return;
            }

            if (player instanceof PlaylistSupport) {
                ((PlaylistSupport) player).setShuffleEnabled(enable);
            }
        }

        public boolean isShuffleEnabled() {
            if (player == null) {
                return false;
            }

            if (player instanceof PlaylistSupport) {
                return ((PlaylistSupport) player).isShuffleEnabled();
            }
            return false;
        }

        public void addToPlaylist(String mediaURL) {
            if (player == null) {
                return;
            }

            if (player instanceof PlaylistSupport) {
                ((PlaylistSupport) player).addToPlaylist(mediaURL);
            }
        }

        public void removeFromPlaylist(int index) {
            if (player == null) {
                return;
            }

            if (player instanceof PlaylistSupport) {
                ((PlaylistSupport) player).removeFromPlaylist(index);
            }
        }

        public void clearPlaylist() {
            if (player == null) {
                return;
            }

            if (player instanceof PlaylistSupport) {
                ((PlaylistSupport) player).clearPlaylist();
            }
        }

        public void playNext() throws PlayException {
            if (player == null) {
                return;
            }

            if (player instanceof PlaylistSupport) {
                ((PlaylistSupport) player).playNext();
            }
        }

        public void playPrevious() throws PlayException {
            if (player == null) {
                return;
            }

            if (player instanceof PlaylistSupport) {
                ((PlaylistSupport) player).playPrevious();
            }
        }

        public void play(int index) throws IndexOutOfBoundsException {
            if (player == null) {
                return;
            }

            if (player instanceof PlaylistSupport) {
                ((PlaylistSupport) player).play(index);
            }
        }

        public int getPlaylistSize() {
            if (player == null) {
                return 0;
            }

            if (player instanceof PlaylistSupport) {
                return ((PlaylistSupport) player).getPlaylistSize();
            }

            return 0;
        }
    }

    private static class ScriptSeekBar {

        private JavaScriptObject seekChangeHandler;
        private MediaSeekBar seek;

        public ScriptSeekBar(int height, String eId, JavaScriptObject options) {
            HashMap<String, String> _options = new HashMap<String, String>();
            parseOptionsToMap(options, _options);

            seek = provider.getSeekBar(height, _options);
            RootPanel.get(eId).add(seek);

            seek.addSeekChangeHandler(new SeekChangeHandler() {

                public void onSeekChanged(SeekChangeEvent event) {
                    if (seekChangeHandler != null) {
                        JavaScriptObject evt = JavaScriptObject.createObject();
                        putEventValue(evt, "seekPosition", event.getSeekPosition());
                        runCallback(seekChangeHandler, evt);
                    }
                }
            });
        }

        public void setLoadingProgress(double loadingProgress) {
            seek.setLoadingProgress(loadingProgress);
        }

        public void setPlayingProgress(double playingProgress) {
            seek.setPlayingProgress(playingProgress);
        }

        public void addEventListener(String eventName, JavaScriptObject callback) {
            if (eventName.equals("onSeekChanged")) {
                seekChangeHandler = callback;
            }
        }
    }
}