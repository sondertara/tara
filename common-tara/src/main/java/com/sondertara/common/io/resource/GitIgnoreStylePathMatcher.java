package com.sondertara.common.io.resource;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.pattern.patternset.AntPathMatcher;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GitIgnoreStylePathMatcher implements PathMatcher {
    private AntPathMatcher matcher;
    private String paths;

    public GitIgnoreStylePathMatcher(){

    }

    public GitIgnoreStylePathMatcher(String pathsExpression){
        setPaths(pathsExpression);
    }

    public void setPaths(String pathsExpression){
        if(StringUtils.isBlank(pathsExpression)){
            return;
        }
        this.paths = pathsExpression;
        final String[] paths = StringUtils.split(pathsExpression,";");
        final List<String> antPaths = new ArrayList<>();
        CollectionUtils.forEach(paths, new Consumer<String>() {
            @Override
            public void accept(String path) {
                // .svn/ => /**/.svn/**
                // /.svn/ => /.svn/**
                // !.svn/ => !/**/.svn/**
                // !/.svn/ => !/.svn/**

                // *.json => /**/*.json
                // /*.json => /*.json
                // !*.json => !/**/*.json
                // !/*.json => !/*.json

                // !.properties => !/**/.properties
                // !/.properties => !/.properties

                boolean isDirectory = StringUtils.endsWith(path, "/");
                boolean notIgnoredMode = StringUtils.startsWith(path,"!");

                if(notIgnoredMode){
                    path = StringUtils.substring(path,1);
                }

                if(!StringUtils.startsWith(path,"/")){
                    path = "**/" + path;
                }
                if(notIgnoredMode){
                    path = "!"+path;
                }
                if(!StringUtils.startsWith(path,"/")){
                    path = "/"+path;
                }

                if(isDirectory){
                    String directory = StringUtils.substring(path,0, path.length()-1);
                    if(StringUtils.isNotEmpty(directory)){
                        antPaths.add(directory);
                    }
                    // 目录下所有文件
                    antPaths.add(path+"**");
                }else{
                    antPaths.add(path);
                }
            }
        });

        String expression = StringUtils.join(";", antPaths);
        this.matcher = new AntPathMatcher();
        this.matcher.setGlobal(true);
        this.matcher.setPatternExpression(expression);
    }

    public Boolean matches(String path){
        if(this.matcher == null){
            return true;
        }
        return this.matcher.matches(path);
    }

    @Override
    public String toString() {
        return "GitIgnoreStylePathMatcher{" +
                "paths='" + paths + '\'' +
                '}';
    }

    public boolean matches(@NonNull String path, @Nullable String pathRoot){
        if(StringUtils.isBlank(path)){
            return false;
        }
        path = path.trim();
        if(pathRoot!=null && StringUtils.startsWith(path, pathRoot)){
            path = StringUtils.substring(path, pathRoot.length());
        }
        path = StringUtils.replace(path, "\\","/");
        if(!StringUtils.startsWith(path,"/")){
            path="/"+path;
        }
        if(StringUtils.isEmpty(path)){
            return false;
        }
        boolean match = matches(path);
        return match;
    }

}
