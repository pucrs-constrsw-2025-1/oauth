package com.constrsw.oauth.domain.entity;

import java.util.Objects;

public class Role {
    private String id;
    private String name;
    private String description;
    private boolean composite;

    public Role() {
    }

    public Role(String id, String name, String description, boolean composite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.composite = composite;
    }

    // Construtor para criação de nova role
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.composite = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isComposite() {
        return composite;
    }

    public void setComposite(boolean composite) {
        this.composite = composite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", composite=" + composite +
                '}';
    }
}