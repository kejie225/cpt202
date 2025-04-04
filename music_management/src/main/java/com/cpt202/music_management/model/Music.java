package com.cpt202.music_management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "musics")  // 明确指定表名
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String name;
    @Column(nullable = false, length = 50)  // 明确列定义
    private int time;
    private String author;

    public Music(){

    }
    public Music(String name , int time , String author){
        this.name=name;
        this.time=time;
        this.author=author;
    }

    public int getTime(){
        return time;
    }
    public void setTime(int time){
        this.time=time;
    }
    public String getAuthor(){return author;}
    public void setAuthor(String author){this.author=author;}
    public String getName(){return name ;}
    public void setName(){this.name=name;}
}
