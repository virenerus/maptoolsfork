/*
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
package com.t3.clientserver.handler;

import com.t3.clientserver.Command;
import com.t3.clientserver.NetworkSerializer;
import com.t3.clientserver.NetworkSerializer.TransferredMessage;

public abstract class AbstractMethodHandler<T extends Enum<T> & Command> implements MessageHandler {

	@Override
    public void handleMessage(String id, byte[] message) {
        TransferredMessage<T> tm=NetworkSerializer.<T>deserialize(message);
		handleMethod(id, tm.getMessage(), tm.getParameters());
    }

	public abstract void handleMethod(String id, T message,Object... parameters);
}
