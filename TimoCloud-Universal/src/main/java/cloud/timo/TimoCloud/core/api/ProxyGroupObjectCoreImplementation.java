package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.objects.ProxyGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.BaseObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ProxyObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerGroupObjectLink;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor
public class ProxyGroupObjectCoreImplementation extends ProxyGroupObjectBasicImplementation implements ProxyGroupObject {

    public ProxyGroupObjectCoreImplementation(String id, String name, Collection<ProxyObjectLink> proxies, int onlinePlayerCount, int maxPlayerCount, int maxPlayerCountPerProxy, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, Collection<ServerGroupObjectLink> serverGroups, BaseObjectLink base, String proxyChooseStrategy, Collection<String> hostNames) {
        super(id, name, proxies, onlinePlayerCount, maxPlayerCount, maxPlayerCountPerProxy, keepFreeSlots, minAmount, maxAmount, ram, motd, isStatic, priority, serverGroups, base, proxyChooseStrategy, hostNames);
    }
}
