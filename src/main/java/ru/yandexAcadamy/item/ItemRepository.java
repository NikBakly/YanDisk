package ru.yandexAcadamy.item;

import org.springframework.data.repository.CrudRepository;
import ru.yandexAcadamy.item.model.Item;
import ru.yandexAcadamy.item.model.Type;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends CrudRepository<Item, String> {

    //Метод для нахождения всех элементов по id родителя
    List<Item> findAllByParentId(String parentId);

    //Метод для нахождения всех файлов, который существуют в определенном интервале
    List<Item> findAllByTypeAndDateBetween(Type type,
                                           LocalDateTime fromDate,
                                           LocalDateTime toDate);

}
