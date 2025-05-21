package com.cpt202.music_management.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.cpt202.music_management.model.Playlist;

public class PlaylistSpec {
    public static Specification<Playlist> hasAuthor(String author) {
        return (root, query, cb) -> author == null ? null :
                cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Playlist> hasName(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Playlist> hasType(String type) {
        return (root, query, cb) -> type == null ? null :
                cb.equal(root.get("type"), type);
    }
}