package com.aston.hibernateProject.item.model;

import com.aston.hibernateProject.user.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.aston.hibernateProject.request.model.ItemRequest;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "is_available")
    Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    User owner;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "request_id")
    ItemRequest request;
}