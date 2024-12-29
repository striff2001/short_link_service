## О проекте
Сервис сокращения ссылок — это инструмент, который позволяет пользователю сократить длинный URL, 
получить уникальную короткую ссылку и управлять её параметрами.

Пользователь может устанавливать лимит на количество переходов по ссылке и отслеживать её активность. 
После достижения лимита переходов или истечения заданного срока ссылка станет недоступной, 
и пользователь получит уведомление с предложением создать новую ссылку.

## Основные задачи:

1. **Создание коротких ссылок.** Система должна принимать длинный URL и преобразовывать его в короткую ссылку. 
Пример: при передаче ссылки https://www.baeldung.com/java-9-http-client вы должны получить короткий вариант clck.ru/3DZHeG.
2. **Уникальные ссылки для каждого пользователя.** 
Если один и тот же ресурс сокращают разные пользователи, они должны получать уникальные сокращенные ссылки.
3. **Лимит переходов.** Пользователь может задать максимальное количество переходов по ссылке. 
Как только этот лимит исчерпан, ссылка должна стать недоступной.
4. **Ограничение времени жизни ссылки.** Время жизни ссылки должно задаваться системой и ограничиваться определенным сроком (например, сутки). 
После истечения этого срока ссылка должна автоматически удаляться.
5. **Уведомление пользователя.** Пользователь должен получать уведомление, если лимит переходов исчерпан или время жизни ссылки истекло.
6. **Идентификация пользователя по UUID.** 
Каждый пользователь идентифицируется без авторизации с помощью UUID, который генерируется при первом запросе на создание короткой ссылки. 
Этот UUID используется для отслеживания всех действий пользователя с его ссылками.

## Как разрабатывать (ПОТОМ УДАЛИТСЯ)

Репозиторий — это специальный класс в Java, который сохраняет данные объектов куда-то, обычно в базу данных.

1. ~~Создать функциональность по сокращению коротких ссылок.~~ 
2. ~~При вводе короткой ссылки пользователя автоматически перенаправляет на сайт~~
3. Задать лимиты ссылки с помощью конфиг файла
4. Создать уведомлялки пользователя в момент обращения к сервису, если ссылка протухла
5. Привязать ссылки к UUID'ам


## Инструкция для пользователя