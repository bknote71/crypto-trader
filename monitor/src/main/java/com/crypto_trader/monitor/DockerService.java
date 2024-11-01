package com.crypto_trader.monitor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.crypto_trader.monitor.WebSocketServerManager.apiServerImage;

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

        return dockerClientWrapper.getHost();
    }

    public void scaleIn(String serverIp) {
        DockerClientWrapper dockerClientWrapper = dockerClients.stream()
                .filter(client -> client.getHost().equals(serverIp))
                .findFirst()
                .orElse(null);

        if (dockerClientWrapper == null) {
            System.out.println("No matching docker client found for host: " + serverIp);
            return;
        }

        DockerClient dockerClient = dockerClientWrapper.getDockerClient();

        List<Container> containers = dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec();

        for (Container container : containers) {
            String[] image = container.getImage().split(":");
            String name = image[0];
            String tag = image.length > 1 ? image[1] : "";

            if (apiServerImage.equals(name) && "latest".equals(tag)) {
                dockerClient.stopContainerCmd(container.getId()).exec();
                dockerClient.removeContainerCmd(container.getId()).exec();
            }
        }

        dockerClients.remove(dockerClientWrapper);
    }
}
