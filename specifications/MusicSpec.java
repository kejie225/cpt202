package com.cpt202.music_management.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.cpt202.music_management.model.Music;

public class MusicSpec {
    public static Specification<Music> hasAuthor(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return (root, query, cb) -> cb.conjunction(); // 返回总是为真的条件
        }
        return (root, query, cb) -> 
            cb.or(
                cb.like(cb.lower(root.join("singer").get("name")), "%" + keyword.toLowerCase() + "%")
            );
    }

    public static Specification<Music> hasName(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return (root, query, cb) -> cb.conjunction(); // 返回总是为真的条件
        }
        return (root, query, cb) -> 
            cb.or(
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("album")), "%" + keyword.toLowerCase() + "%")
            );
    }

    public static Specification<Music> hasType(String type) {
        if (type == null || type.isEmpty()) {
            return (root, query, cb) -> cb.conjunction(); // 返回总是为真的条件
        }
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }
    
    public static Specification<Music> hasStatus(String status) {
        if (status == null || status.isEmpty() || status.equalsIgnoreCase("All")) {
            return (root, query, cb) -> cb.conjunction(); // 返回总是为真的条件
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
