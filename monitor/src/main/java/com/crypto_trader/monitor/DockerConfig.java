package com.crypto_trader.monitor;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;

import static com.crypto_trader.monitor.WebSocketServerManager.apiServerImage;

@Configuration
@ConfigurationProperties(prefix = "docker")
public class DockerConfig {

    private List<DockerInstance> instances;

    public List<DockerInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<DockerInstance> instances) {
        this.instances = instances;
    }

    @Bean
    public List<DockerClientWrapper> dockerClients() {
        return instances.stream()
                .map(instance -> {
                    // "unix:///var/run/docker.sock"
                    ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                            .dockerHost(URI.create("tcp://" + instance.getHost() + ":" + instance.getPort()))
                            .build();

                    DockerClient dockerClient = DockerClientBuilder.getInstance()
                            .withDockerHttpClient(httpClient)
                            .build();

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

                    return new DockerClientWrapper(dockerClient, instance.getHost());
                })
                .toList();
    }
}