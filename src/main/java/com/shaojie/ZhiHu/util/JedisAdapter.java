package com.shaojie.ZhiHu.util;


import com.shaojie.ZhiHu.controller.MessageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Set;

/*
    因为系统一启动就可以用，所以需要初始化连接池
 */
@Service
public class JedisAdapter implements InitializingBean{


    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;



    public List<String> lrang(String key,int start,int end){


        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);


        } catch (Exception e) {

            logger.error(e.getMessage());
        } finally {

            if (jedis != null) {
                jedis.close();
            }

        }

        return null;
    }








    public Double zscore(String key,String member){

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            return jedis.zscore(key,member);


        } catch (Exception e) {

            logger.error("失败" + e.getMessage());
        } finally {

            if (jedis != null) {

                jedis.close();
            }
        }
        return null;

    }

    public long zcard(String key){

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            return jedis.zcard(key);


        } catch (Exception e) {

            logger.error("失败" + e.getMessage());
        } finally {

            if (jedis != null) {

                jedis.close();
            }
        }
        return 0;

    }


    public Set<String> zrevrange(String key, int start , int end){

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            return jedis.zrevrange(key,start,end);


        } catch (Exception e) {

            logger.error("失败" + e.getMessage());
        } finally {

            if (jedis != null) {

                jedis.close();
            }
        }
        return null;

    }

    public long zrem(String key,String value){

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            return jedis.zrem(key,value);


        } catch (Exception e) {

            logger.error("失败" + e.getMessage());
        } finally {

            if (jedis != null) {

                jedis.close();
            }
        }
        return 0;

    }


    public long zadd(String key,double score,String value){


        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            return jedis.zadd(key,score,value);


        } catch (Exception e) {

            logger.error("失败" + e.getMessage());
        } finally {

            if (jedis != null) {

                jedis.close();

            }

        }

        return 0;

    }



    public List<Object> exec(Transaction tx, Jedis jedis) {

        try {

            return tx.exec();

        } catch (Exception e) {
            logger.error("失败" + e.getMessage());

        } finally {

            if (tx != null) {

                try {

                    tx.close();

                } catch (Exception e) {
                    logger.error("失败" + e.getMessage());
                }
            }

            if (jedis != null) {

                try {
                    jedis.close();

                } catch (Exception e) {
                    logger.error("失败" + e.getMessage());
                }
            }

        }

        return null;

    }



    public Transaction multi(Jedis jedis){

        try{

        return jedis.multi();

        }catch (Exception e){
            logger.error("失败" + e.getMessage());

        }

        return null;
    }



    public Jedis getJedis(){

        return  pool.getResource();

    }


    public List<String> brpop(int timeout, String key) {

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            return jedis.brpop(timeout, key);


        } catch (Exception e) {

            logger.error("失败" + e.getMessage());
        } finally {

            if (jedis != null) {

                jedis.close();

            }

        }

        return null;
    }


    public long lpush(String key,String value){

        Jedis jedis = null;

        try {

            jedis = pool.getResource();

            return jedis.lpush(key,value);
        }catch(Exception e){

            logger.error("失败"+e.getMessage());
        }finally {

            if (jedis!=null){

                jedis.close();

            }
        }

        return 0;
    }



    public static void print(int index,Object obj){

        System.out.println(String.format("%d %s",index,obj.toString()));

    }



    public static void main(String[] args){

        Jedis jedis = new Jedis("redis://localhost:6379/10");

        jedis.flushDB();


        String listName = "list";

        for(int i = 0;i<10;i++){


        jedis.lpush(listName,"a"+String.valueOf(i));

        }
        print(1,jedis.lrange(listName,0,12));


        String userKey = "userxx";
        jedis.hset(userKey,"name","lsj");
        jedis.hset(userKey,"age","12");

        print(2,jedis.hget(userKey,"name"));

        String commentKey = "likeComment1";
        String commentkeyy = "likeComment2";

        for(int i = 0;i<10;i++){

            jedis.sadd(commentKey,String.valueOf(i));
            jedis.sadd(commentkeyy,String.valueOf(i*i));

        }
        print(3,jedis.smembers(commentKey));
        print(4,jedis.smembers(commentkeyy));




    //    jedis.set("hello","world");
     //   print(1,jedis.get("hello"));



//        String userKey = "userxx";
//
//        jedis.hset("userxx","name","jim");
//        jedis.hset("userxx","iphone","155");
//        jedis.hset("userxx","address","446");
//        print(2,jedis.hget("userxx","iphone"));
//
//        jedis.hdel("userxx","iphone");
//        print(3,jedis.hgetAll("userxx"));


        //set
//        String likeKey1 = "commentLike1";
//        String likeKey2 = "commentLike2";
////
//        for(int i =0;i<10;i++){
//
//        jedis.sadd("number",String.valueOf(i));
//        jedis.sadd("numberr",String.valueOf(i*i));
//
//        }
//
//        print(4,jedis.smembers("number"));
//        print(5,jedis.smembers("numberr"));
//        print(6,jedis.sdiff("number","numberr"));
//
//


//        jedis.sadd("m","w");
//        jedis.sadd("m","w");
//        print(6,jedis.smembers("m"));

    }

//创建redis连接池
    @Override
    public void afterPropertiesSet() throws Exception {

        pool = new JedisPool("redis://localhost:6379/2");

    }


    /*
        这几个方法都是赞踩功能

     */
    public long sadd(String key,String value){

        Jedis jedis = null;

        try{
            jedis = pool.getResource();
            return jedis.sadd(key,value);

        }catch (Exception e){

            logger.error("失败"+e.getMessage());

        }finally {
            if(jedis!=null){

                jedis.close();
            }
        }
        return 0;
    }

 //删除某个value
    public long srem(String key,String value){

        Jedis jedis = null;

        try{
            jedis = pool.getResource();
            return jedis.srem(key,value);

        }catch (Exception e){

            logger.error("失败"+e.getMessage());

        }finally {
            if(jedis!=null){

                jedis.close();
            }
        }
        return 0;
    }

    //返回集合中的数量
    public long scard(String key){

        Jedis jedis = null;

        try{
            jedis = pool.getResource();
            return jedis.scard(key);

        }catch (Exception e){

            logger.error("失败"+e.getMessage());

        }finally {
            if(jedis!=null){

                jedis.close();
            }
        }
        return 0;
    }


    //返回是否属于该集合
    public boolean sismember(String key,String value){

        Jedis jedis = null;

        try{
            jedis = pool.getResource();
            return jedis.sismember(key,value);

        }catch (Exception e){

            logger.error("失败"+e.getMessage());

        }finally {
            if(jedis!=null){

                jedis.close();
            }
        }
        return false;
    }



}
