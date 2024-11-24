package org.nstep.engine.module.system.framework.rpc.config;

import org.nstep.engine.module.infra.api.config.ConfigApi;
import org.nstep.engine.module.infra.api.file.FileApi;
import org.nstep.engine.module.infra.api.websocket.WebSocketSenderApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, WebSocketSenderApi.class, ConfigApi.class})
public class RpcConfiguration {
}
