/*
rebuild - Building your business-systems freely.
Copyright (C) 2018-2019 devezhao <zhaofang123@gmail.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package com.rebuild.server.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rebuild.server.Application;
import com.rebuild.utils.CommonsUtils;
import com.rebuild.utils.JSONUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;
import java.util.UUID;

/**
 * 授权许可
 *
 * @author ZHAO
 * @since 2019-08-23
 */
public final class License {

    private static final String OSA_KEY = "IjkMHgq94T7s7WkP";

    /**
     * 授权码/SN
     *
     * @return
     */
    public static String SN() {
        String SN = SysConfiguration.get(ConfigurableItem.SN, false);
        if (SN == null) {
            SN = SysConfiguration.get(ConfigurableItem.SN, true);
        }

        if (SN == null) {
            try {
                String result = CommonsUtils.get(
                        String.format("https://getrebuild.com/api/authority/new?k=%s&ver=%s", OSA_KEY, Application.VER));
                if (JSONUtils.wellFormat(result)) {
                    JSONObject data = JSON.parseObject(result);
                    SN = data.getString("sn");
                }
            } catch (Exception ignored) {
                // UNCATCHABLE
            }
        }

        if (SN == null) {
            SN = String.format("ZR%s%s-%s",
                    "109",
                    StringUtils.leftPad(Locale.getDefault().getCountry(), 3, "0"),
                    UUID.randomUUID().toString().replace("-", "").substring(0, 15).toUpperCase());
            if (Application.serversReady()) {
                SysConfiguration.set(ConfigurableItem.SN, SN);
            }
        }

        return SN;
    }

    /**
     * 查询授权信息
     *
     * @return
     */
    public static JSON queryAuthority() {
        JSON result = siteApi("api/authority/query");
        if (result == null) {
            result = JSONUtils.toJSONObject(
                    new String[]{ "sn", "authType", "autoObject", "authExpires" },
                    new String[]{ SN(), "开源社区版", "GitHub", "无" });
        }
        return result;
    }

    /**
     * 调用 RB 官方服务 API
     *
     * @param api
     * @return
     */
    public static JSON siteApi(String api) {
        String apiUrl = "https://getrebuild.com/" + api;
        apiUrl += (api.contains("\\?") ? "&" : "?") + "k=" + OSA_KEY + "&sn=" + SN();

        try {
            String result = CommonsUtils.get(apiUrl);
            if (JSONUtils.wellFormat(result)) {
                return JSON.parseObject(result);
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}