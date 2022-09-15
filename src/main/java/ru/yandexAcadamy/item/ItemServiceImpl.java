package ru.yandexAcadamy.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandexAcadamy.exception.BadRequestException;
import ru.yandexAcadamy.exception.NotFoundException;
import ru.yandexAcadamy.item.dto.*;
import ru.yandexAcadamy.item.model.Item;
import ru.yandexAcadamy.item.model.Type;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс-сервис, который предназначен для реализации основной бизнес-логики.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    public static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private final ItemRepository itemRepository;
    private final ItemHistoryRepository itemHistoryRepository;

    @Transactional
    @Override
    public void importing(SystemItemImportRequest systemItemImportRequest) {
        for (SystemItemImport systemItemImport : systemItemImportRequest.getItems()) {
            Optional<Item> item = itemRepository.findById(systemItemImport.getId());
            validateWhenImporting(item, systemItemImport, systemItemImportRequest.getUpdateDate());
            LocalDateTime updateDate = LocalDateTime.parse(systemItemImportRequest.getUpdateDate(), formatter);
            if (systemItemImport.getParentId() != null) {
                if (systemItemImport.getType().equals(Type.FILE)) {
                    //разница между новым размером и старым.
                    int differenceSizes;
                    //если уже существует файл, то ищем разницу в размерах
                    if (item.isPresent()) {
                        int newSize = systemItemImport.getSize();
                        int oldSize = item.get().getSize();
                        differenceSizes = newSize - oldSize;
                    } else {
                        differenceSizes = systemItemImport.getSize();
                    }
                    updateSizeOrDateParentByParentId(systemItemImport.getParentId(), differenceSizes, updateDate);
                }
            }
            if (item.isPresent()) {
                log.info("Successful updating item id={}", systemItemImport.getId());
            } else {
                log.info("Successful importing item id={}", systemItemImport.getId());
            }
            //Сохраняем элемент
            Item resultItem = itemRepository.save(ItemMapper.toItem(systemItemImport, updateDate));
            //Записываем в историю
            itemHistoryRepository.save(ItemMapper.toItemHistory(resultItem));
        }
    }

    @Transactional
    @Override
    public void delete(String itemId, String updateDate) {
        checkItemId(itemId);
        if (!isValidISODateTime(updateDate)) {
            log.warn("Date does not fit the ISO 8601 format");
            throw new BadRequestException();
        }
        String parentId = itemRepository.findById(itemId).get().getParentId();
        if (parentId != null) {
            updateSizeOrDateParentByParentId(parentId, 0, LocalDateTime.parse(updateDate, formatter));
        }
        log.info("Successful delete item id={}", itemId);
        deleteItemById(itemId);
    }

    @Transactional(readOnly = true)
    @Override
    public SystemItem findItemById(String itemId) {
        checkItemId(itemId);
        log.info("Successful find item id={}", itemId);
        return findSystemItemById(itemId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SystemItemHistoryUnit> findUpdatedFile(String date) {
        if (!isValidISODateTime(date)) {
            log.warn("Date does not fit the ISO 8601 format");
            throw new BadRequestException();
        }
        List<Item> updatedItems = itemRepository
                .findAllByTypeAndDateBetween(Type.FILE,
                        LocalDateTime.parse(date, formatter).minusHours(24),
                        LocalDateTime.parse(date, formatter));
        return ItemMapper.toSystemItemsHistoryUnitFromItem(updatedItems);
    }

    @Transactional(readOnly = true)
    @Override
    public SystemItemHistoryResponse getHistory(String itemId, String dateStart, String dateEnd) {
        checkItemId(itemId);
        //если интервал определен, но не до конца, то выкидывается ошибка
        if (dateStart != null && dateEnd != null) {
            if (!isValidISODateTime(dateStart)) {
                log.warn("Date does not fit the ISO 8601 format");
                throw new BadRequestException();
            }
            if (!isValidISODateTime(dateEnd)) {
                log.warn("Date does not fit the ISO 8601 format");
                throw new BadRequestException();
            }
            return ItemMapper.toSystemItemHistoryResponse(itemHistoryRepository
                    .findAllByIdAndDateIsBetween(itemId,
                            LocalDateTime.parse(dateStart, formatter),
                            LocalDateTime.parse(dateEnd, formatter)));
        }
        return ItemMapper.toSystemItemHistoryResponse(itemHistoryRepository.findAllById(itemId));
    }

    private void validateWhenImporting(Optional<Item> item, SystemItemImport systemItemImport, String updateDate) {
        if (systemItemImport.getParentId() != null) {
            Optional<Item> parentItem = itemRepository.findById(systemItemImport.getParentId());

            if (systemItemImport.getType().equals(Type.FILE)
                    && systemItemImport.getParentId() != null) {
                //Проверка файла на не существование родителя или проверка, что родитель точно папка
                if (parentItem.isEmpty() || (parentItem.get().getType().equals(Type.FILE))) {
                    log.warn("Problem with parent element of element id={}", systemItemImport.getId());
                    throw new BadRequestException();
                }
            }
        }
        // проверка на то, что поле url при импорте папки не равен null
        if (item.isEmpty()
                && systemItemImport.getType().equals(Type.FOLDER)
                && systemItemImport.getUrl() != null) {
            log.warn("Url must be null when importing of element id={}", systemItemImport.getId());
            throw new BadRequestException();
        }
        // проверка на то, что длина поля url > 255 при испорте
        if (item.isEmpty()
                && systemItemImport.getType().equals(Type.FILE)
                && systemItemImport.getUrl().length() > 255) {
            log.warn("Url length must be less than or equal to 255 when importing of element id={}", systemItemImport.getId());
            throw new BadRequestException();
        }
        // проверка на то, что поле size у папки не равен null при импорте
        if (item.isEmpty()
                && systemItemImport.getType().equals(Type.FOLDER)
                && systemItemImport.getSize() != null) {
            log.warn("Folder size should be null when importing of element id={}", systemItemImport.getId());
            throw new BadRequestException();
        }
        // проверка поля size на не положительность
        if (systemItemImport.getType().equals(Type.FILE)
                && systemItemImport.getSize() <= 0) {
            log.warn("File size should be positive when importing of element id={}", systemItemImport.getId());
            throw new BadRequestException();
        }
        // проверка даты на формат ISO 8601
        if (!isValidISODateTime(updateDate)) {
            log.warn("Date does not fit the ISO 8601 format of element id={}", systemItemImport.getId());
            throw new BadRequestException();
        }

        // проверка на обновление типа элемента
        if (item.isPresent() && !item.get().getType().equals(systemItemImport.getType())) {
            log.warn("Element type is not updated");
            throw new BadRequestException();
        }
    }

    // метод проверят дату на формат ISO 8601
    private boolean isValidISODateTime(String date) {
        try {
            formatter.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // метод обновляет размер и дату папки, в которой случилось изменение, если она существует
    private void updateSizeOrDateParentByParentId(String parentId, int differenceInSize, LocalDateTime updateDate) {
        if (parentId != null) {
            Optional<Item> parentItem = itemRepository.findById(parentId);
            // проверка на существование родителя
            if (parentItem.isPresent()) {
                boolean checkSize = false;
                if (differenceInSize != 0) {
                    if (parentItem.get().getSize() != null) {
                        int actualSize = parentItem.get().getSize() + differenceInSize;
                        parentItem.get().setSize(actualSize);
                    } else {
                        parentItem.get().setSize(differenceInSize);
                    }
                    checkSize = true;
                }
                boolean checkDate = false;
                if (!parentItem.get().getDate().equals(updateDate)) {
                    parentItem.get().setDate(updateDate);
                    checkDate = true;
                }
                // добавление в историю, если папка изменилась
                if (checkDate || checkSize) {
                    itemHistoryRepository.save(ItemMapper.toItemHistory(parentItem.get()));
                }
                //обновление папки
                itemRepository.save(parentItem.get());
                updateSizeOrDateParentByParentId(parentItem.get().getParentId(), differenceInSize, updateDate);
            }

        }
    }


    // метод для проверки существования элемента
    private void checkItemId(String itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.warn("Элемент itemId={} не найден", itemId);
            throw new NotFoundException();
        }
    }

    // метод, который удаляет элементы по id, если элемент - папка, то он удаляет в ней все дочерние элементы
    private void deleteItemById(String itemId) {
        Item item = itemRepository.findById(itemId).get();
        if (item.getType().equals(Type.FOLDER)) {
            List<SystemItem> children = getItemChildren(itemId);
            for (SystemItem child : children) {
                deleteItemById(child.getId());
            }
        }
        itemRepository.deleteById(itemId);
        itemHistoryRepository.deleteHistoryById(itemId);
    }

    // метод, который возвращает элемент SystemItem
    private SystemItem findSystemItemById(String itemId) {
        Item item = itemRepository.findById(itemId).get();
        if (item.getType().equals(Type.FOLDER)) {
            List<SystemItem> children = getItemChildren(itemId);
            return ItemMapper.toSystemItem(item, children);
        }
        return ItemMapper.toSystemItem(item);
    }

    // метод, который возвращает детей элемента по id родителя
    private List<SystemItem> getItemChildren(String parentId) {
        List<SystemItem> itemChildren = new ArrayList<>();
        //проходимся по всем "детям"
        for (Item itemChild : itemRepository.findAllByParentId(parentId)) {
            if (itemChild.getType().equals(Type.FOLDER)) {
                List<SystemItem> children = getItemChildren(itemChild.getId());
                itemChildren.add(ItemMapper.toSystemItem(itemChild, children));
            } else {
                itemChildren.add(ItemMapper.toSystemItem(itemChild));
            }
        }
        return itemChildren;
    }


}
