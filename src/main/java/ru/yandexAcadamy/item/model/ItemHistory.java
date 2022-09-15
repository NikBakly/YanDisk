package ru.yandexAcadamy.item.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "items_history")
public class ItemHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_history")
    private Long idHistory;

    // Уникальный идентификатор
    @Column(name = "id")
    private String id;

    // Ссылка на файл. Для папок поле равно null.
    private String url;

    // Время последнего обновления элемента.
    private String date;

    // id родительской папки
    @Column(name = "parent_id")
    private String parentId;

    // Тип элемента - папка или файл
    @Enumerated(EnumType.STRING)
    private Type type;

    // Целое число, для папки - это суммарный размер всех элементов.
    private Integer size;
}
