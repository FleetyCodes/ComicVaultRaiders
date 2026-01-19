package com.comicvaultraiders.comicvaultraiders.specification;

import com.comicvaultraiders.comicvaultraiders.dto.filter.UserComicFilter;
import com.comicvaultraiders.comicvaultraiders.model.UserXComics;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;


import java.util.ArrayList;
import java.util.List;


public class UserComicSpecs {
    public static Specification<UserXComics> withFilters(UserComicFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(filter.getUserId() != null){
                predicates.add(cb.equal(root.get("user").get("id"), filter.getUserId()));
            }
            if (filter.getTitle() != null) {
                predicates.add(cb.like(cb.lower(root.get("comic").get("title")),"%" + filter.getTitle().toLowerCase() + "%"));
            }
            if (filter.getAuthor() != null) {
                predicates.add(cb.like(cb.lower(root.get("comic").get("author")),"%" + filter.getAuthor().toLowerCase() + "%"));
            }
            if (filter.getIllustrator() != null) {
                predicates.add(cb.like(cb.lower(root.get("comic").get("illustrator")),"%" + filter.getIllustrator().toLowerCase() + "%"));
            }
            if (filter.getPublisher() != null && !filter.getPublisher().isEmpty()) {
                predicates.add(root.get("comic").get("publisher").in(filter.getPublisher()));
            }
            if (filter.getFormat() != null && !filter.getFormat().isEmpty()) {
                predicates.add(root.get("comic").get("format").in(filter.getFormat()));
            }
            if (filter.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("comic").get("releaseDate"), filter.getFromDate()));
            }
            if (filter.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("comic").get("releaseDate"), filter.getToDate()));
            }
            if (filter.getWishlisted() != null) {
                predicates.add(cb.equal(root.get("wishlisted"), filter.getWishlisted()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
