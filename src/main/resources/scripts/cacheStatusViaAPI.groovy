package scripts

import com.liferay.portal.cluster.AddressImpl;
import com.liferay.portal.cluster.ClusterLinkImpl;
import com.liferay.portal.kernel.cluster.ClusterLinkUtil;
import com.liferay.portal.kernel.cluster.Priority;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.util.ReflectionUtil;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.util.List;
import org.jgroups.JChannel;
import org.jgroups.util.UUID;
try {
    ClusterLinkImpl clusterLinkImpl = (ClusterLinkImpl) ClusterLinkUtil.getClusterLink();
    if (clusterLinkImpl.isEnabled()) {
        if (clusterLinkImpl.getBindInetAddress() == null) {
            println("getBindInetAddress is null");
        } else {
            println("getBindInetAddress is not null");
        }
    }
    Field field = ReflectionUtil.getDeclaredField(ClusterLinkImpl.class, "_transportChannels");
    List<JChannel> jChannels = (List<JChannel>) field.get(clusterLinkImpl);
    if (jChannels != null) {
        for (JChannel channel : jChannels) {
            println("channel AddressAsString: " + channel.getAddressAsString());
            println("channel ClusterName: " + channel.getClusterName());
            println("channel ViewAsString: " + channel.getViewAsString());
            println("channel Address: " + channel.getAddress().toString());
            println("channel ReceivedBytes: " + channel.getReceivedBytes());
            println("channel SentBytes: " + channel.getSentBytes());
            println("channel ReceivedMessages: " + channel.getReceivedMessages());
            println("channel SentMessages: " + channel.getSentMessages());
        }
    }
} catch (Exception e) {
    println(e);
}