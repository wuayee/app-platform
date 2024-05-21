package com.huawei.jade.fel.rag.retrieve;

import com.huawei.jade.fel.chat.content.Contents;
import com.huawei.jade.fel.chat.content.MessageContent;
import com.huawei.jade.fel.core.retriever.Retriever;
import com.huawei.jade.fel.embed.EmbedOptions;
import com.huawei.jade.fel.embed.EmbedRequest;
import com.huawei.jade.fel.model.openai.client.OpenAiClient;
import com.huawei.jade.fel.model.openai.service.OpenAiEmbedModelService;
import com.huawei.jade.fel.rag.store.config.VectorConfig;
import com.huawei.jade.fel.rag.store.connector.SqlConnector;
import com.huawei.jade.fel.rag.store.connector.VectorConnector;
import com.huawei.jade.fel.rag.store.query.VectorQuery;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MultiSourceRetriever implements Retriever<String> {

    private final Integer topK;
    private final List<String> collectionNames;

    private final String fieldName = "embedding";
    private final VectorConnector vecConn;
    private final SqlConnector dataConn;

    public MultiSourceRetriever(List<String> collectionNames,
                               Integer topK,
                               VectorConnector vecConn,
                               SqlConnector dataConn)
    {
        this.topK = topK / collectionNames.size();
        this.collectionNames = collectionNames;
        this.vecConn = vecConn;
        this.dataConn = dataConn;
    }

    @Override
    public MessageContent invoke(String arg) {
        List<String> searchRes = new ArrayList<>();
        StringBuffer str = new StringBuffer();
        EmbedRequest request = new EmbedRequest();
        EmbedOptions opts = new EmbedOptions();
        OpenAiClient client = new OpenAiClient("http://tzaip-beta.paas.huawei.com/model-gateway/", false);
        OpenAiEmbedModelService service = new OpenAiEmbedModelService(client);

        opts.setModel("bge-large-zh");
        request.setOptions(opts);
        request.setInputs(Arrays.asList(arg));
        List<Float> vec = service.generate(request).getEmbeddings().get(0);

        for (String collection : collectionNames) {
            VectorQuery query = new VectorQuery(vec, this.topK, null, 0.5, null);
            VectorConfig config = new VectorConfig();
            config.setCollectionName(collection);

            List<Pair<Map<String, Object>, Float>> searched =
                    vecConn.get(query, config);
            String sql = constructSqlQuery(searched);
            List<Map<String, Object>> res = dataConn.execute(sql);
            res.forEach((n) -> searchRes.add((String)n.get("content")));
        }

        searchRes.forEach((chunk) -> str.append(chunk).append(";"));
        return Contents.from(str.toString());
    }

    private String constructSqlQuery(List<Pair<Map<String, Object>, Float>> dataInMilvus) {
        StringBuffer ids = new StringBuffer("(");
        dataInMilvus.forEach((n) -> {
            ids.append((Long) n.getKey().get("id"));
            ids.append(",");
        });
        ids.setLength(ids.length() - 1);
        ids.append(")");

        return "select content from t_vector_knowledge_base_segment where id in" + ids.toString();
    }
}
