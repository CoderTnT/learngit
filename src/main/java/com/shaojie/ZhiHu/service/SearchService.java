package com.shaojie.ZhiHu.service;


import com.shaojie.ZhiHu.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private static final String SOLR_URL = "http://127.0.0.1:8983/solr/wenda";
    private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD="question_content";


    //搜索功能
    public List<Question> searchQuestion(String keyword,int offset,int limit,String hlPre,String hlPos) throws Exception{

        List<Question> questionList = new ArrayList<>();

        SolrQuery query = new SolrQuery(keyword);
        query.setRows(limit);
        query.setStart(offset);
        query.setHighlight(true);
        query.setHighlightSimplePre(hlPre);
        query.setHighlightSimplePost(hlPos);
        query.set("hl.fl",QUESTION_TITLE_FIELD+","+QUESTION_CONTENT_FIELD);
        QueryResponse response = client.query(query);

        for(Map.Entry<String,Map<String,List<String>>> entry :response.getHighlighting().entrySet()){

            Question question = new Question();
            question.setId(Integer.parseInt(entry.getKey()));
            if(entry.getValue().containsKey(QUESTION_CONTENT_FIELD)){
                //虽然只有一个content但是因为是List的数据结构所以
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if(contentList.size()>0){
                    question.setContent(contentList.get(0));
                }
            }

            if(entry.getValue().containsKey(QUESTION_TITLE_FIELD)){
                //虽然只有一个content但是因为是List的数据结构所以
                List<String> titleList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if(titleList.size()>0){
                    question.setTitle(titleList.get(0));
                }
            }
            questionList.add(question);
        }

        return questionList;

    }

    //每次添加问题的时候要添加索引
    public boolean indexQuestion(int qid,String title,String content) throws Exception{

        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id",qid);
        doc.setField(QUESTION_TITLE_FIELD,title);
        doc.setField(QUESTION_CONTENT_FIELD,content);
        //1S钟就返回
        UpdateResponse response =client.add(doc,1000);
        return response!=null &&response.getStatus()==0;
    }



}
