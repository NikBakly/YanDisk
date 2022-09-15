package ru.yandexAcadamy.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandexAcadamy.item.model.Type;

@Builder
@Setter
@Getter
public class SystemItemHistoryUnit {
    private String id;
    private String url;
    private String parentId;
    private Type type;
    private Integer size;
    private String date;
}
