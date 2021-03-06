# AlbumsOnSwagger
Образовательный проект курса ["Многопоточность и сетевое взаимодействие в Android"](https://www.coursera.org/learn/android-multithreading-and-network) 

# Технологии 
Layout:
- SwipeRefreshLayout
- RecyclerView
- CardView
- ConstraintLayout

Network:
- Retrofit2
- Okhttp3
- Gson

Multitask:
- RxJava2
    
Database:
- Room

## Экраны приложения

<img src="screenshots/reg.jpg" width="200"> <img src="screenshots/auth.jpg" width="200"> <img src="screenshots/albums.jpg" width="200"> <img src="screenshots/songs.jpg" width="200"> <img src="screenshots/comments.jpg" width="200">

## Требования к проектоному заданию
1. Сделать переход с детального описания альбома (списка песен этого альбома) к комментированию 
этого альбома. К дизайнерским откровениям мы не стремимся, достаточно добавить пункт меню в тулбар.

2. Экран комментирования представляет собой RecyclerView с комментариями, с EditText и Button внизу (см пример в чате любого мессенджера).
    Верстка холдера - на усмотрение исполнителя.
    RecyclerView обернут в SwipeRefreshLayout.
    При запросах появляется индикатор прогресса.
    После загрузки комментариев, если данных нет, то показывать лейаут с ошибкой.
    Интерфейс лейаута - на усмотрение исполнителя, но должно быть понятно, что комментариев нет.
    Достаточно простого текста.

3. Элемент списка RecyclerView - Комментарий - должен отображать:
    имя автора
    текст комментария
    время создания
    Все эти поля есть в модели Comment.

4. Время создания необходимо преобразовать из таймштампа в человеческий вид: показывать время,
 если прошло меньше суток с создания комментария, иначе - показывать дату.

5. При набранном тексте в EditText и нажатии на Button должен отправляться post запрос с этим текстом.
 После успешного ответа - отправить запрос на получение созданного комментария и добавить его в RecyclerView.
 От момента отправки первого запроса и до появления нового комментария в списке - должен крутиться
 индикатор прогресса SwipeRefreshLayout.
 Если в EditText нет текста, то при нажатии на кнопку показывать тост о том, что нет текста для отправки.
 Если при отправке запроса произошла ошибка, показывать тост с текстом ошибки.

6. SwipeRefreshLayout должен использоваться для обновления списка комментариев.
 После обновления показывать тосты - “Комментарии обновлены” или “Новых комментариев нет”
 в случае наличия или отсутствия изменений в списке комментариев. Это не относится к первой загрузке экрана.
 С лейаута с ошибкой также должна быть возможность обновиться свайпом.

7. Добавить возможность отправлять написанный комментарий прямо с помощью кнопки энтер на клавиатуре.

8. Сделать все это реактивно.

9. Сохранить комментарии в базе данных Room и отображать их при отсутствии интернета.

## API приложения
[Android academy e-Legion api](https://android.academy.e-legion.com/docs/)



