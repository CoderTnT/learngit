package com.shaojie.ZhiHu.service;

import com.shaojie.ZhiHu.controller.HomeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class  SensitiveService implements InitializingBean{


    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    @Override
    public void afterPropertiesSet() throws Exception {

        try{

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        String lineTxt;
        while((lineTxt= reader.readLine())!=null){
            addKeyword(lineTxt.trim());

        }
        reader.close();
        }catch (Exception e){
            logger.error("敏感词文件读取失败"+e.getMessage());
        }


    }

//过滤传进来文本的方法。
    public String textFilter(String text){

        if(StringUtils.isEmpty(text)){
            return text;
        }
//用**替代
        String replacement = "**";
//将充满敏感词的树的指针传进来。1号指针
        TrieNode tempNode = rootTriNode;
//2号指针
        int begin = 0;
        //3号指针
        int postion = 0;

        StringBuilder sb = new StringBuilder();


        while(postion<text.length()){

            char c = text.charAt(postion);

            if(isSymbol(c)){
                //参考  我 ❥(^_-) 你    和  色(๑′ᴗ‵๑)Ｉ Lᵒᵛᵉᵧₒᵤ❤你
                if(tempNode==rootTriNode){
                    sb.append(c);
                    ++begin;
                }
                ++postion;
                continue;
            }
            //有没有以C开头的敏感词
            tempNode = tempNode.getSubNode(c);

            //没有就很好办就直接添加进来
            if(tempNode==null){
                sb.append(text.charAt(begin));

               postion = begin+1;

               begin =postion;

               tempNode = rootTriNode;

            }else if(tempNode.isKeywordEnd()){
                //是不是到了末尾
                sb.append(replacement);
                postion = postion+1;
                begin=postion;
                tempNode=rootTriNode;
            }else{

                postion++;

            }

        }
        //记得一定要加这句话  比如敏感词是h的时候   lsjh的时候lsj添加进去了，最后一次循环到h的时候postion++直接就跳走了，所以最后要加这个
        sb.append(text.substring(begin));
        return sb.toString();

    }




    //增加关键词的 比如abc
    public void addKeyword(String lineTxt){

        //首先指向父节点，一开始是空的
        TrieNode tempNode = rootTriNode;

        //开始添加
        for(int i =0;i<lineTxt.length();i++){
            //定义到当前字符
            Character character = lineTxt.charAt(i);
            //看上一个节点是否包含当前字符
            TrieNode node = tempNode.getSubNode(character);
            //不包含添加
            if(node==null){
                node = new TrieNode();
                tempNode.addSubNode(character,node);
            }

            tempNode = node;
            //到了末尾的时候添加标记
            if(i == lineTxt.length()-1)
            {
                node.setKeywordEnd(true);
            }

        }


    }

    //构造字典树这个结构
    private  class TrieNode{

        private boolean end = false;

       //   ab af   这个b,f就是key
        private Map<Character,TrieNode> subNodes = new HashMap<>();


        public void addSubNode(Character key,TrieNode trieNode){
            subNodes.put(key,trieNode);

        }

        TrieNode getSubNode(Character key){

           return subNodes.get(key);

        }
        public boolean isKeywordEnd(){

            return end;

        }

        public void setKeywordEnd(boolean end){
            this.end = end;

        }


    }




//判断是否是特殊字符
    private boolean isSymbol(char c){

        int ic = (int)c;

        return (ic<0x2E80||ic>0x9FFF);

    }


    private TrieNode rootTriNode = new TrieNode();


    public static void main(String[] args){

      SensitiveService s = new SensitiveService();
      s.addKeyword("i like jshavabcde");
      System.out.println(s.textFilter("你好色情"));


    }


}
