package ru.yandexAcadamy.item;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.yandexAcadamy.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends CrudRepository<Item, String> {

    //Метод для нахождения всех элементов по id родителя
    List<Item> findAllByParentId(String parentId);

    //Метод для нахождения всех файлов, который существуют в определенном интервале
    @Query("select it from Item as it" +
            " where it.type = 'FOLDER' " +
            " and cast(it.date as timestamp) between cast(:from_date as timestamp ) and cast(:to_date as timestamp )")
    List<Item> findAllFolderAndDateBetween(@Param("from_date") LocalDateTime fromDate,
                                           @Param("to_date") LocalDateTime toDate);

}
