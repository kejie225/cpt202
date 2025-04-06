package com.cpt202.music_management.specification;

import org.springframework.data.jpa.domain.Specification;

import com.cpt202.music_management.model.Music;

public class MusicSpec {
    public static Specification<Music> hasAuthor(String author) {
        return (root, query, cb) -> author == null ? null :
                cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Music> hasName(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    // public static Specification<Music> hasTime(Integer time) {
    //     return (root, query, cb) -> time == null ? null :
    //             cb.equal(root.get("time"), time);
    // }
}
