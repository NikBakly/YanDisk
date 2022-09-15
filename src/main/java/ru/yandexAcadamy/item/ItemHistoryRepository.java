package ru.yandexAcadamy.item;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.yandexAcadamy.item.model.ItemHistory;

import java.util.List;

public interface ItemHistoryRepository extends CrudRepository<ItemHistory, Long> {

    //Метод для удаления элемента по id
    void deleteHistoryById(String itemHistoryId);

    List<ItemHistory> findAllById(String itemId);

    @Query("select ih from ItemHistory as ih" +
            " where ih.id = :id " +
            " and cast(ih.date as timestamp) between cast(:date_start as timestamp ) and cast(:date_end as timestamp )")
    List<ItemHistory> findAllByIdAndDateIsBetween(@Param("id") String itemId,
                                                  @Param("date_start") String dateStart,
                                                  @Param("date_end") String dateEnd);


}
