package ru.yandexAcadamy.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandexAcadamy.item.model.Type;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
public class SystemItem {
    private String id;
    private String url;
    private LocalDateTime date;
    private String parentId;
    private Type type;
    private Integer size;
    private List<SystemItem> children;
}
