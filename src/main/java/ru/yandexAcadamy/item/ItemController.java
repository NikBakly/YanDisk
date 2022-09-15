package ru.yandexAcadamy.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandexAcadamy.item.dto.SystemItem;
import ru.yandexAcadamy.item.dto.SystemItemHistoryResponse;
import ru.yandexAcadamy.item.dto.SystemItemHistoryUnit;
import ru.yandexAcadamy.item.dto.SystemItemImportRequest;

import java.util.List;

/**
 * Класс-контроллер, который предназначен для обработки запросов и возвращение результата.
 */

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/imports")
    public void importing(@RequestBody SystemItemImportRequest systemItemImportRequest) {
        itemService.importing(systemItemImportRequest);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") String itemId,
                       @RequestParam(name = "date") String updateTime) {
        itemService.delete(itemId, updateTime);
    }

    @GetMapping("/nodes/{id}")
    public SystemItem findItemById(@PathVariable("id") String itemId) {
        return itemService.findItemById(itemId);
    }


    @GetMapping("/updates")
    public List<SystemItemHistoryUnit> findUpdatedItems(@RequestParam(name = "date") String date) {
        return itemService.findUpdatedFile(date);
    }

    @GetMapping("/node/{id}/history")
    public SystemItemHistoryResponse getHistory(@PathVariable("id") String itemId,
                                                @RequestParam(name = "dateStart", required = false) String dateStart,
                                                @RequestParam(name = "dateEnd", required = false) String dateEnd) {
        return itemService.getHistory(itemId, dateStart, dateEnd);
    }
}
