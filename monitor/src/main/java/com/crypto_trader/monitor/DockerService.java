package com.crypto_trader.monitor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.crypto_trader.monitor.WebSocketServerManager.apiServerImage;

@Service
public class DockerService {

    private final DockerClient dockerClient;

    private boolean isDefaultServerStarted = false; // 기본 서버 상태를 추적

    public DockerService() {
        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(URI.create("unix:///var/run/docker.sock"))
                .build();

        this.dockerClient = DockerClientBuilder.getInstance()
                .withDockerHttpClient(httpClient)
                .build();

        // initializer
        List<Container> containers = dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec();

        for (Container container : containers) {
            String[] image = container.getImage().split(":");
            String name = image[0];
            String tag = image.length > 1 ? image[1] : "";

            if (apiServerImage.equals(name) && "latest".equals(tag)) {
                this.dockerClient.stopContainerCmd(container.getId()).exec();
                this.dockerClient.removeContainerCmd(container.getId()).exec();
            }
        }
    }

    public String scaleOut(String imageName, String containerName) throws Exception {
        Integer port;

        if (!isDefaultServerStarted) {
            port = 8090;
            isDefaultServerStarted = true;
        } else {
            port = null;
        }

        Ports portBindings = new Ports();
        ExposedPort tcp8090 = ExposedPort.tcp(8090);

        if (port != null) {
            portBindings.bind(tcp8090, Ports.Binding.bindPort(8090));
        } else {
            portBindings.bind(tcp8090, Ports.Binding.empty());  // Docker가 자동으로 포트 할당
        }

        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .withExposedPorts(tcp8090)
                .withHostConfig(HostConfig.newHostConfig()
                        .withPortBindings(portBindings)
                        .withNetworkMode("scheduler_app-network"))
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();

        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(container.getId()).exec();
        Map<ExposedPort, Ports.Binding[]> portBindingsMap = containerInfo.getNetworkSettings().getPorts().getBindings();
        Ports.Binding[] bindings = portBindingsMap != null ? portBindingsMap.get(tcp8090) : null;

        String assignedPort;
        if (port != null) {
            assignedPort = "8090"; // Default port assigned
        } else if (bindings != null && bindings.length > 0) {
            assignedPort = bindings[0].getHostPortSpec(); // Docker auto-assigned port
        } else {
            throw new IllegalStateException("Port binding not found for container " + containerName);
        }

        return "http://localhost:" + assignedPort;
    }

    public void scaleIn(String containerId, int port) throws Exception {
        dockerClient.stopContainerCmd(containerId).withTimeout(10).exec();
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
