/*
 * Copyright 2017-2019 CodingApi .
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
package com.fiberhome.filink.txlcncommon.util.serializer.jdk;


import com.fiberhome.filink.txlcncommon.exception.SerializerException;
import com.fiberhome.filink.txlcncommon.util.serializer.ISerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * JavaSerializer.
 *
 * @author lorne
 */
@SuppressWarnings("unchecked")
public class JavaSerializer implements ISerializer {

    @Override
    public byte[] serialize(final Object obj) throws SerializerException {
        try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(); ObjectOutput objectOutput = new ObjectOutputStream(arrayOutputStream)) {
            objectOutput.writeObject(obj);
            objectOutput.flush();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializerException("java serialize error " + e.getMessage());
        }
    }

    @Override
    public <T> T deSerialize(final byte[] param, final Class<T> clazz) throws SerializerException {
        try (ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(param); ObjectInput input = new ObjectInputStream(arrayInputStream)) {
            return (T) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializerException("java deSerialize error " + e.getMessage());
        }
    }

    @Override
    public void serialize(Object obj, OutputStream outputStream) throws SerializerException {
        try (ObjectOutput objectOutput = new ObjectOutputStream(outputStream)) {
            objectOutput.writeObject(obj);
            objectOutput.flush();
        } catch (IOException e) {
            throw new SerializerException("java serialize error " + e.getMessage());
        }
    }

    @Override
    public <T> T deSerialize(InputStream inputStream, Class<T> clazz) throws SerializerException {
        try (ObjectInput input = new ObjectInputStream(inputStream)) {
            return (T) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializerException("java deSerialize error " + e.getMessage());
        }
    }
}