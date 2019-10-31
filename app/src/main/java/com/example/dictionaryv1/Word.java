package com.example.dictionaryv1;

public class Word {
    private int id;
    String word;
    String content;

    public Word() {
    }

    public Word(int id, String word, String content) {
        this.id = id;
        this.word = word;
        this.content = content;
    }

    public Word(String word, String content) {
        this.word = word;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}