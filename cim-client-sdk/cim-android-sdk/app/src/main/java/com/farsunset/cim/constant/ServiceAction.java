/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
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
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim.constant;

public interface ServiceAction {

    String ACTION_CREATE_CONNECTION = "ACTION_CREATE_CONNECTION";

    String ACTION_DESTROY_SERVICE = "ACTION_DESTROY_SERVICE";

    String ACTION_ACTIVATE_PUSH_SERVICE = "ACTION_ACTIVATE_PUSH_SERVICE";

    String ACTION_SEND_REQUEST_BODY = "ACTION_SEND_REQUEST_BODY";

    String ACTION_CLOSE_CONNECTION = "ACTION_CLOSE_CONNECTION";

    String ACTION_SET_LOGGER_EATABLE = "ACTION_SET_LOGGER_EATABLE";

    String ACTION_SHOW_PERSIST_NOTIFICATION = "ACTION_SHOW_PERSIST_NOTIFICATION";

    String ACTION_HIDE_PERSIST_NOTIFICATION = "ACTION_HIDE_PERSIST_NOTIFICATION";

    String ACTION_CONNECTION_PONG = "ACTION_CONNECTION_PONG";
}
