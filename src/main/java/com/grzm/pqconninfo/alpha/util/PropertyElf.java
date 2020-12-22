/*
 * This source contains code from https://github.com/brettwooldridge/HikariCP/blob/f005a4769ff77d03fb7e86903c5978a35e9ccb96/src/main/java/com/zaxxer/hikari/util/PropertyElf.java
 * which is licensed as below. Slight modifications have removed HikariCP-specific and unused code.
 *
 * Copyright (C) 2013 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grzm.pqconninfo.alpha.util;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * A class that reflectively sets bean properties on a target object.
 *
 * @author Brett Wooldridge
 */
public final class PropertyElf {
    private PropertyElf() {
        // cannot be constructed
    }

    public static Object getProperty(final String propName, final Object target) {
        try {
            // use the english locale to avoid the infamous turkish locale bug
            String capitalized = "get" + propName.substring(0, 1).toUpperCase(Locale.ENGLISH) + propName.substring(1);
            Method method = target.getClass().getMethod(capitalized);
            return method.invoke(target);
        } catch (Exception e) {
            try {
                String capitalized = "is" + propName.substring(0, 1).toUpperCase(Locale.ENGLISH) + propName.substring(1);
                Method method = target.getClass().getMethod(capitalized);
                return method.invoke(target);
            } catch (Exception e2) {
                return null;
            }
        }
    }
}
