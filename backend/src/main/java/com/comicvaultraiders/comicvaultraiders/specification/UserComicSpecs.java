package com.comicvaultraiders.comicvaultraiders.specification;

import com.comicvaultraiders.comicvaultraiders.dto.filter.UserComicFilter;
import com.comicvaultraiders.comicvaultraiders.modell.UserXComics;
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

            if (filter.getPublisher() != null) {
                predicates.add(cb.equal(root.get("comic").get("publisher"), filter.getPublisher()));
            }
            if (filter.getFormat() != null) {
                predicates.add(cb.equal(root.get("comic").get("format"), filter.getFormat()));
            }
            if (filter.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("comic").get("releaseDate"), filter.getFromDate()));
            }
            if (filter.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("comic").get("releaseDate"), filter.getToDate()));
            }
            if (filter.getWishlisted() != null) {
                predicates.add(cb.isTrue(root.get("wishlisted")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
