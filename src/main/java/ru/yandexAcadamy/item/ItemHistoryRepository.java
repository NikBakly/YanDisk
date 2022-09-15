package ru.yandexAcadamy.item;

import org.springframework.data.repository.CrudRepository;
import ru.yandexAcadamy.item.model.ItemHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemHistoryRepository extends CrudRepository<ItemHistory, Long> {

    //Метод для удаления элемента по id
    void deleteHistoryById(String itemHistoryId);

    List<ItemHistory> findAllById(String itemId);


    List<ItemHistory> findAllByIdAndDateIsBetween(String itemId,
                                                  LocalDateTime date_start,
                                                  LocalDateTime date_end);


}
