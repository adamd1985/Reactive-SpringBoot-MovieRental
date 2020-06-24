package org.adamd.demo.domain;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "movie_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieTypeEntity {

    @AllArgsConstructor
    @Getter
    public enum Type {
        NEW_RELEASE(1), REGULAR(2), OLD(3);

        private Integer id;

        private static Map<Integer, Type> map = new HashMap<>();

        static {
            for (Type type : Type.values()) {
                map.put(type.getId(), type);
            }
        }
        
        public static Type getType(Integer id) {
            return map.get(id);
        }
    }

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String type;

    @Column
    private BigDecimal price;
}
