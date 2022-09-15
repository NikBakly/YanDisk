package ru.yandexAcadamy.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SystemItemImportRequest {
    private List<SystemItemImport> items;
    private String updateDate;
}
