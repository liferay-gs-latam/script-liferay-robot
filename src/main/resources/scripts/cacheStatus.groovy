package scripts

import com.liferay.portal.kernel.cluster.ClusterExecutorUtil
import com.liferay.portal.service.LockLocalServiceUtil
import com.liferay.portal.kernel.cluster.AddressSerializerUtil
import com.liferay.portal.kernel.cluster.Address
import com.liferay.portal.kernel.cluster.ClusterLinkUtil
import com.liferay.portal.kernel.cluster.Priority
import java.net.InetAddress
import com.liferay.portal.kernel.resiliency.spi.SPIUtil
import com.liferay.portal.kernel.cluster.ClusterMasterExecutorUtil;

String MASTER_CLASS_NAME = com.liferay.portal.cluster.ClusterMasterExecutorImpl.class.getName()
String masterAddrStr = ""
try {
    out.println ("Is enabled: " + ClusterExecutorUtil.isEnabled())
    out.println ("Bind Inet Address: " + ClusterLinkUtil.getBindInetAddress())
    out.println ("Is SPI?: " + SPIUtil.isSPI())
    ClusterMasterExecutorUtil cmeu = new ClusterMasterExecutorUtil();
	out.println("master node?" + cmeu.isMaster());
    out.println ("")
    count = 0
    out.println ("*** ClusterLink Transport Addresses ***");
    out.println ("");
    for(def add: ClusterLinkUtil.getTransportAddresses(Priority.LEVEL1)) {
        out.println(add)
        count++
    }
    
    out.println ("Nodes Total: " + count);
    out.println ("")
    out.println ("*** Cluster Nodes ***");
    out.println ("");
    count = 0
    for(def node: ClusterExecutorUtil.getClusterNodes()) {
        out.print(node)
        out.println(" - Is Alive? " + ClusterExecutorUtil.isClusterNodeAlive(node.getClusterNodeId()))
        count++
    }
    out.println ("Nodes Total: " + count);
    out.println ("")
    out.println ("*** Cluster Node Addresses ***");
    out.println ("");
    count = 0
    for (Address addr: ClusterExecutorUtil.getClusterNodeAddresses()) {
        out.print(addr);
        if (masterAddrStr.equals(addr.toString())) {
              out.println(" => Master ");
        } else {
            out.println("");
        }
        count++
    }
    out.println ("Nodes Total: " + count);
} catch(Exception e) {
    out.println (e);
}