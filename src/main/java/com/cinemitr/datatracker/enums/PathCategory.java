package com.cinemitr.datatracker.enums;

public enum PathCategory {
    MEDIA_FILE("Media_file"),
    CONTENT_FILE("Content_file"),
    UPLOADED_FILE("Uploaded_file");

    private final String value;

    PathCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PathCategory fromValue(String value) {
        for (PathCategory category : PathCategory.values()) {
            if (category.getValue().equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown PathCategory value: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}