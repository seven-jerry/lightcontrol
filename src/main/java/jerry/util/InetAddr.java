package jerry.util;

import java.net.*;
import java.util.Enumeration;

public class InetAddr {

    public static String getIpAddr() {
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                final NetworkInterface next = interfaces.nextElement();
                if (next.isLoopback()) {
                    continue;
                }
                for ( final InterfaceAddress addr : next.getInterfaceAddresses( ) )
                {
                    final InetAddress inet_addr = addr.getAddress( );

                    if ( !( inet_addr instanceof Inet4Address) )
                    {
                        continue;
                    }

                    return inet_addr.getHostAddress();
                }
            }
        } catch (SocketException e) {
            return "";
        }

        return "";
    }
}
