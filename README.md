# Biblioteka

Prosty program konsolowy w Javie. Dane książek i wypożyczeń zapisuje w pliku `biblioteka.txt`.

Projekt jest pracą zaliczeniową.

## Uruchomienie

Otwórz terminal w folderze z `Main.java` i `biblioteka.txt`, a następnie wpisz:

```powershell
javac -encoding UTF-8 Main.java
java Main
```

Jeśli polskie znaki wyświetlają się niepoprawnie w Windows, przed uruchomieniem wpisz `chcp 65001`.

## Menu

1. Lista książek.
2. Wypożyczenie - program pokazuje dostępne książki.
3. Wypożyczone książki i termin zwrotu. Wpisz ID wypożyczenia, aby zwrócić książkę, lub `0`, aby pominąć.
4. Lista książek po terminie.
5. Dodawanie, edycja i usuwanie książek.
6. Zapis i wyjście.

W opcji 5 wybierz: `1` dodaj, `2` wybierz książkę do edycji lub usunięcia jednego egzemplarza, `3` anuluj. Tytuł znika z katalogu dopiero po usunięciu ostatniej kopii.

## Dane

Program wczytuje dane z `biblioteka.txt` przy starcie i zapisuje je po każdej zmianie. Plik przechowuje książki oraz ich historię wypożyczeń. Data zwrotu oznacza, że książka została oddana; `-` oznacza wypożyczenie aktywne. Program nadal pokazuje aktywne wypożyczenia po terminie. Plik jest tekstowy i używa kodowania UTF-8.

## Autor

Szymon Rybikowski
