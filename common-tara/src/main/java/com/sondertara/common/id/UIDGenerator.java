/*
 * Copyright (c) 2001-2017, Zoltan Farkas All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Additionally licensed with:
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
package com.sondertara.common.id;

import com.sondertara.common.codec.base64.Base64;
import com.sondertara.common.concurrent.Sequence;
import com.sondertara.common.os.JdkUtils;
import com.sondertara.common.text.AppendableUtils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Unique ID Generator Based on the assumptions: 1. host MAC address is used. (each network interface has a Unique ID)
 * (encoded with provided encoder) 2. process id is used + current epoch seconds. it is assumed the PID is not recycled
 * within a second. 3. A process sequence is used. UIDs will cycle after Long.MaxValue is reached.
 *
 * @author zoly
 */
public final class UIDGenerator implements IdGenerator<CharSequence> {

  private final Sequence sequence;

  private final StringBuilder base;

  private final int maxSize;

  public UIDGenerator(final Sequence sequence) {
    this(sequence, 0);
  }

  public UIDGenerator(final Sequence sequence, final String prefix) {
    this(sequence, 0, '.', prefix);
  }

  public UIDGenerator(final Sequence sequence, final long customEpoch) {
    this(sequence, customEpoch, '.', "");
  }

  public UIDGenerator(final Sequence sequence, final String prefix, final long customEpoch) {
    this(sequence, customEpoch, '.', prefix);
  }

  /**
   * Construct a UID Generator
   *
   * @param sequence
   */
  public UIDGenerator(final Sequence sequence,
                      final long customEpoch, final char separator, final String prefix) {
    this.sequence = sequence;
    StringBuilder sb = generateIdBase(prefix, separator, customEpoch);
    base = sb;
    maxSize = base.length() + 16;
  }

  public static StringBuilder generateIdBase(final String prefix,
          final char separator) {
    return generateIdBase(prefix, separator, 1509741164184L);
  }


  public static StringBuilder generateIdBase(final String prefix, final char separator,
          final long customEpoch) {
    StringBuilder sb = new StringBuilder(16 + prefix.length());
    sb.append(prefix);

    byte[] intfMac;
    try {
      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
      if (networkInterfaces != null && networkInterfaces.hasMoreElements()) {
        do {
          intfMac = networkInterfaces.nextElement().getHardwareAddress();
        } while ((intfMac == null || intfMac.length == 0) && networkInterfaces.hasMoreElements());
        if (intfMac == null) {
          Logger.getLogger(UIDGenerator.class.getName()).warning(
                  "Unable to get interface MAC address for ID generation");
          try {
            intfMac = ByteBuffer.allocate(Long.BYTES).putLong(SecureRandom.getInstanceStrong().nextLong()).array();
          } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
          }
        }
      } else {
        Logger.getLogger(UIDGenerator.class.getName()).warning(
                "Unable to get interface MAC address for ID generation");
        try {
          intfMac = ByteBuffer.allocate(Long.BYTES).putLong(SecureRandom.getInstanceStrong().nextLong()).array();
        } catch (NoSuchAlgorithmException ex) {
          throw new IllegalStateException(ex);
        }
      }
    } catch (SocketException ex) {
      Logger.getLogger(UIDGenerator.class.getName()).log(Level.WARNING,
              "Unable to get interface MAC address for ID generation", ex);
      try {
        intfMac = ByteBuffer.allocate(Long.BYTES).putLong(SecureRandom.getInstanceStrong().nextLong()).array();
      } catch (NoSuchAlgorithmException ex2) {
        ex2.addSuppressed(ex);
        throw new IllegalStateException(ex2);
      }
    }
    sb.append(Base64.encodeBase64ToString(intfMac)).append(separator);

    AppendableUtils.appendUnsignedString(sb, JdkUtils.getPid(), 5);
    sb.append(separator);
    AppendableUtils.appendUnsignedString(sb, (System.currentTimeMillis() - customEpoch) / 1000, 5);
    sb.append(separator);
    return sb;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public CharSequence next() {
    StringBuilder result = new StringBuilder(maxSize);
    result.append(base);
    AppendableUtils.appendUnsignedString(result, sequence.next(), 5);
    return result;
  }

  @Override
  public String toString() {
    return "UIDGenerator{" + "sequence=" + sequence + ", base=" + base + ", maxSize=" + maxSize + '}';
  }

  @Override
  public CharSequence get() {
    return next();
  }

}
