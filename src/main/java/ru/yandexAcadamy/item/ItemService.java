package ru.yandexAcadamy.item;

import ru.yandexAcadamy.item.dto.SystemItem;
import ru.yandexAcadamy.item.dto.SystemItemHistoryResponse;
import ru.yandexAcadamy.item.dto.SystemItemHistoryUnit;
import ru.yandexAcadamy.item.dto.SystemItemImportRequest;

import java.util.List;

public interface ItemService {
    //Базовые задачи

    /**
     * Импортирует элементы файловой системы.
     * Элементы импортированные повторно обновляют текущие.
     * Изменение типа элемента с папки на файл и с файла на папку не допускается.
     * Порядок элементов в запросе является произвольным.
     */
    void importing(SystemItemImportRequest systemItemImportRequest);

    /**
     * Удалить элемент по идентификатору.
     * При удалении папки удаляются все дочерние элементы.
     * Доступ к истории обновлений удаленного элемента невозможен.
     *
     * @param itemId идентификатор Item
     */
    void delete(String itemId, String updateDate);

    /**
     * Получить информацию об элементе по идентификатору.
     * При получении информации о папке также предоставляется информация о её дочерних элементах.
     *
     * @return SystemItem - найденный элемент
     */
    SystemItem findItemById(String itemId);

    //Дополнительные задачи

    /**
     * Получение списка файлов, которые были обновлены
     * за последние 24 часа включительно [date - 24h, date] от времени переданном в запросе.
     *
     * @return список обновленных файлов
     */
    List<SystemItemHistoryUnit> findUpdatedFile(String date);

    /**
     * Получение истории обновлений по элементу за заданный полуинтервал [from, to).
     * История по удаленным элементам недоступна.
     *
     * @param itemId    идентификатор Item
     * @param dateStart начало интервала
     * @param dateEnd   конец интервала
     * @return список истории элементов из интервала
     */
    SystemItemHistoryResponse getHistory(String itemId, String dateStart, String dateEnd);


}
