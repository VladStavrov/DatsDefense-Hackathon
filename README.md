# DatsDefense Hackathon

## Описание

Наша команда "Три джависта в составе коалиции против CR/LF", состоящая из:
1. [Тиунчика Даниила](https://github.com/Daniil-Tsiunchyk)
2. [Ставрова Владислава](https://github.com/VladStavrov)
3. [Венского Никиты](https://github.com/VenskijNick)

приняла участие в хакатоне DatsDefense. В этом соревновании мы оказались в эпицентре зомби апокалипсиса, где наша задача — выжить и вылечить как можно больше зомби, используя свои навыки программирования.

## Правила и Механика Игры

### Основные Этапы
1. **Тренировка**: 12.07.2024 с 19:00 до 22:00
2. **Финал**: 13.07.2024 с 10:00 до 20:00

### Механика Игры
- **Карта**: Динамически определяется в зависимости от количества участников.
- **База**: Каждый игрок начинает с базой размером 4 блока и 10 золотых монет.
- **Зомби**: Появляются из специальных клеток на карте, имеют различные типы и характеристики.
- **Очки**: Набираются за вылеченных зомби и уничтожение баз противников.

### API Игры
- **POST /play/zombidef/command**: Отправка команд для строительства, атаки зомби и перемещения базы.
- **PUT /play/zombidef/participate**: Записаь на участие в текущем раунде.
- **GET /play/zombidef/units**: Получение информации о текущих юнитах и состоянии мира вокруг.
- **GET /play/zombidef/world**: Получение статичной информации о мире.
- **GET /rounds/zombidef**: Получение расписания раундов.