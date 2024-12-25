/**
 * Nacos 配置变更监听器，用于动态刷新 Spring Cloud Gateway 的路由信息
 * <p>
 * 本类用于监听 Nacos 配置的变化，特别是 `gateway-server.yaml` 配置文件中的路由信息，
 * 当配置发生变化时，自动触发 Spring Cloud Gateway 路由信息的刷新。
 * <p>
 * 使用方式：当在 Nacos 中修改 `gateway-server.yaml` 配置文件时，特别是 `spring.cloud.gateway.routes` 配置项，
 * Spring Cloud Alibaba Nacos Config 会触发配置刷新，进而更新网关的路由配置。
 */
package org.nstep.engine.gateway.route.dynamic;
