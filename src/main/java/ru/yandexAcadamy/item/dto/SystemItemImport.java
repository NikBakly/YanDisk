package ru.yandexAcadamy.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.yandexAcadamy.item.model.Type;

@Getter
@Setter
public class SystemItemImport {
    private String id;
    private String url;
    private String parentId;
    private Type type;
    private Integer size;
}
