package com.crypto_trader.monitor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PingCmd;
import com.github.dockerjava.api.exception.DockerException;

public class DockerClientWrapper {
    private final DockerClient dockerClient;
    private final String host;

    public DockerClientWrapper(DockerClient dockerClient, String host) {
        this.dockerClient = dockerClient;
        this.host = host;
    }

    public DockerClient getDockerClient() {
        return dockerClient;
    }

    public String getHost() {
        return host;
    }

    public boolean isReachable() {
        try {
            dockerClient.pingCmd().exec();
            return true;
        } catch (DockerException e) {
            return false;
        }
    }
}
