package ru.yandexAcadamy.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class SystemItemHistoryResponse {
    List<SystemItemHistoryUnit> items;
}
