package ta.technikumwien.dmsocr.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ta.technikumwien.dmsocr.service.dto.ResultDTO;

import java.util.List;

@Service
public class ElasticsearchService {

    @Value("${elasticsearch.url}")
    private String elastic_url;

    private RestClient restClient;

    private ElasticsearchTransport transport;
    public ElasticsearchClient client;

    @PostConstruct
    public void init() {
        restClient = RestClient
                .builder(HttpHost.create(elastic_url))
                .build();
        transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
    }

    public void close() throws Exception {
        restClient.close();
    }

    public void saveDocument(ResultDTO document) {
        try {
            IndexResponse response = client.index(i -> i
                    .index("document")
                    .id(document.getDocumentId().toString())
                    .document(document));

            System.out.println("success:"+response.toString());
        } catch (Exception e) {
            System.out.println("Fehler:"+e.getMessage());
            e.printStackTrace();
        }
    }

    public void searchDocument(String query) {
        try {
            SearchResponse<ResultDTO> searchResponse = client.search(s -> s
                    .index("document")
                    .query(q -> q
                            .match(t -> t
                                    .field("recognizedText")
                                    .query(query))), ResultDTO.class);

            List<Hit<ResultDTO>> hits = searchResponse.hits().hits();

            for (Hit<ResultDTO> hit : hits) {
                System.out.println("Found: "+hit.id());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDocumentById(String id) {
        try {
            GetResponse<ResultDTO> getResponse = client.get(s -> s
                    .index("document")
                    .id(id), ResultDTO.class);
            ResultDTO source = getResponse.source();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteDocumentById(String id) {
        try {
            DeleteResponse response = client.delete(i -> i
                    .index("document")
                    .id(id));
            System.out.println("Deleted successfully:"+ response.id());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
