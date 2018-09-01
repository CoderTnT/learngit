package com.shaojie.ZhiHu.service;

import com.shaojie.ZhiHu.dao.CommentDAO;
import com.shaojie.ZhiHu.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addComment(Comment comment){

        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.textFilter(comment.getContent()));

        return commentDAO.addComment(comment)>0?comment.getId():0;

    }

    public List<Comment> selectCommentListById(int entityId,int entityType){

        return commentDAO.selectCommentListById(entityId,entityType);

    }

    public int getCommentCount(int entityId,int entityType){

        return commentDAO.getCommentCount(entityId,entityType);


    }

    public void deleteComment(int entityId,int entityType){

        commentDAO.updateStatus(1,entityId,entityType);

    }

    public Comment getCommentById(int id){


        return commentDAO.getCommentById(id);
    }


    public int getUserCommentCount(int userId){

        return commentDAO.getUserCommentCount(userId);

    }




}
