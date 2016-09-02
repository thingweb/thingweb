/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2016 Siemens AG and the thingweb community
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package de.thingweb.binding;

import java.net.*;
import java.util.Collections;
import java.util.List;

/**
 * Tools and helper Functions for Protocol Binding
 * Created by Johannes on 06.07.2016.
 */
public class BindingTools {


    /**
     * Loops through all addresses of all interfaces and returns the first one that is non-local and IPv4
     * @return candidate IP
     * @throws UnknownHostException bubbled up from deep withing java, can happen if the hostname is not in /etc/hosts
     * @throws SocketException something is borked with the socket
     */
    public static String getIpAddress() throws UnknownHostException, SocketException {
        if(!InetAddress.getLocalHost().isLoopbackAddress())
            return InetAddress.getLocalHost().getHostAddress();
        else {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    //filter to non-local IPv4 (uri constructor is not coping with v6)
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        //first catch is the best catch
                        return addr.getHostAddress();
                    }
                }
            }
        }

        // well - we tried. but it seems there is only loopback
        return InetAddress.getLocalHost().getHostAddress();
    }
}
