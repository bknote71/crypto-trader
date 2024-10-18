package com.crypto_trader.monitor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class DockerService {

    private final Queue<DockerClientWrapper> dockerClients;

    public DockerService(List<DockerClientWrapper> dockerClients) {
        this.dockerClients = new LinkedList<>(dockerClients);
    }

    public String scaleOut(String imageName, String containerName) throws Exception {
        Ports portBindings = new Ports();
        ExposedPort tcp8090 = ExposedPort.tcp(8090);
        portBindings.bind(tcp8090, Ports.Binding.bindPort(8090));

        DockerClientWrapper dockerClientWrapper = dockerClients.poll();

        if (dockerClientWrapper == null) {
            // empty docker client
            System.out.println("empty docker client");
            return null;
        }

        if (!dockerClientWrapper.isReachable()) {
            System.out.println("not reachable");
            dockerClients.offer(dockerClientWrapper);
            return null;
        }

        System.out.println("scale out");

        DockerClient dockerClient = dockerClientWrapper.getDockerClient();
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .withExposedPorts(tcp8090)
                .withHostConfig(HostConfig.newHostConfig()
                        .withPortBindings(portBindings)
                )
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();

        return "http://" + dockerClientWrapper.getHost() + ":" + 8090;
    }

    public void scaleIn(String containerId, int port) throws Exception {
        // TODO
    }
}
