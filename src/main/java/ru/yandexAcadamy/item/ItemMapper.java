package ru.yandexAcadamy.item;

import org.springframework.stereotype.Component;
import ru.yandexAcadamy.item.dto.SystemItem;
import ru.yandexAcadamy.item.dto.SystemItemHistoryResponse;
import ru.yandexAcadamy.item.dto.SystemItemHistoryUnit;
import ru.yandexAcadamy.item.dto.SystemItemImport;
import ru.yandexAcadamy.item.model.Item;
import ru.yandexAcadamy.item.model.ItemHistory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public static Item toItem(SystemItemImport systemItemImportRequest, LocalDateTime updateDate) {
        Item item = new Item();
        item.setId(systemItemImportRequest.getId());
        item.setUrl(systemItemImportRequest.getUrl());
        item.setDate(updateDate);
        item.setSize(systemItemImportRequest.getSize());
        item.setType(systemItemImportRequest.getType());
        item.setParentId(systemItemImportRequest.getParentId());
        return item;
    }

    public static SystemItem toSystemItem(Item item) {
        return SystemItem.builder()
                .id(item.getId())
                .url(item.getUrl())
                .date(item.getDate())
                .parentId(item.getParentId())
                .type(item.getType())
                .size(item.getSize())
                .build();

    }

    public static SystemItem toSystemItem(Item item, List<SystemItem> itemChildren) {
        return SystemItem.builder()
                .id(item.getId())
                .url(item.getUrl())
                .date(item.getDate())
                .parentId(item.getParentId())
                .type(item.getType())
                .children(itemChildren)
                .size(item.getSize())
                .build();

    }

    public static SystemItemHistoryUnit toSystemItemHistoryUnit(Item item) {
        return SystemItemHistoryUnit.builder()
                .id(item.getId())
                .url(item.getUrl())
                .date(item.getDate())
                .parentId(item.getParentId())
                .type(item.getType())
                .size(item.getSize())
                .build();
    }

    public static SystemItemHistoryUnit toSystemItemHistoryUnit(ItemHistory item) {
        return SystemItemHistoryUnit.builder()
                .id(item.getId())
                .url(item.getUrl())
                .date(item.getDate())
                .parentId(item.getParentId())
                .type(item.getType())
                .size(item.getSize())
                .build();
    }

    public static List<SystemItemHistoryUnit> toSystemItemsHistoryUnitFromItem(List<Item> items) {
        List<SystemItemHistoryUnit> systemItemsHistoryUnit = new ArrayList<>();
        for (Item item : items) {
            systemItemsHistoryUnit.add(toSystemItemHistoryUnit(item));
        }
        return systemItemsHistoryUnit;
    }

    public static List<SystemItemHistoryUnit> toSystemItemsHistoryUnitFromItemHistory(List<ItemHistory> items) {
        List<SystemItemHistoryUnit> systemItemsHistoryUnit = new ArrayList<>();
        for (ItemHistory itemHistory : items) {
            systemItemsHistoryUnit.add(toSystemItemHistoryUnit(itemHistory));
        }
        return systemItemsHistoryUnit;
    }

    public static ItemHistory toItemHistory(Item item) {
        ItemHistory itemHistory = new ItemHistory();
        itemHistory.setId(item.getId());
        itemHistory.setType(item.getType());
        itemHistory.setUrl(item.getUrl());
        itemHistory.setSize(item.getSize());
        itemHistory.setDate(item.getDate());
        itemHistory.setParentId(item.getParentId());
        return itemHistory;
    }

    public static SystemItemHistoryResponse toSystemItemHistoryResponse(List<ItemHistory> items) {
        return SystemItemHistoryResponse.builder()
                .items(toSystemItemsHistoryUnitFromItemHistory(items))
                .build();
    }

}
