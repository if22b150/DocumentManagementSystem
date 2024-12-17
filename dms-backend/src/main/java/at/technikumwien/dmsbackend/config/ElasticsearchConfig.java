package at.technikumwien.dmsbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("elasticsearch:9200") // Adjust for your ElasticSearch container
                .withConnectTimeout(5000) // Optional timeout settings
                .withSocketTimeout(60000)
                .build();
    }
}