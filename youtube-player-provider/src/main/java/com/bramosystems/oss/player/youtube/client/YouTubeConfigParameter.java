/*
 * Copyright 2012 Sikiru Braheem <sbraheem at bramosystems . com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bramosystems.oss.player.youtube.client;

import com.bramosystems.oss.player.core.client.ConfigParameter;

/**
 * Configuration parameters for the YouTube player widgets.
 *
 * @author Sikiru Braheem <sbraheem at bramosystems . com>
 * @since 2.0
 * @see ConfigParameter
 */
public enum YouTubeConfigParameter implements ConfigParameter {
    /**
     * URLParameters used to modify the behaviour of the YouTube player
     */
    URLParameters(com.bramosystems.oss.player.youtube.client.PlayerParameters.class);

     
    private Class[] valueType;

    private YouTubeConfigParameter(Class... valueType) {
        this.valueType = valueType;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Class[] getValueType() {
        return valueType;
    }
}
