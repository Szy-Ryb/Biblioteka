# Biblioteka

Program w Javie. Dane książek i wypożyczeń zapisuje w pliku `biblioteka.txt`.

Projekt jest pracą zaliczeniową.

## Uruchomienie

Otwórz terminal w folderze z plikiem `Main.java`. Plik `biblioteka.txt` nie jest wymagany przy pierwszym uruchomieniu. Bez niego program rozpoczyna z pustym katalogiem i tworzy plik przy pierwszym zapisie. Jeśli chcesz korzystać z przykładowego katalogu, pozostaw `biblioteka.txt` w tym folderze.

```powershell
javac -encoding UTF-8 Main.java
java Main
```

Jeśli polskie znaki wyświetlają się niepoprawnie w Windows, przed uruchomieniem wpisz `chcp 65001`.

## Menu

1. Lista książek.
2. Wypożycz książkę. Program pokazuje dostępne książki. Termin zwrotu przypada 14 dni po wypożyczeniu.
3. Wypożyczone książki i termin zwrotu. Wpisz ID wypożyczenia, aby zwrócić książkę, lub `0`, aby pominąć.
4. Lista książek po terminie.
5. Dodawanie, edycja i usuwanie książek.
6. Zapis i wyjście.

W opcji 5 wybierz: `1` dodaj, `2` wybierz książkę do edycji lub usunięcia jednego egzemplarza, `3` anuluj. Puste pola i wciśnięcie Enter podczas edycji pozostawiają dotychczasowe wartości. Liczba egzemplarzy nie może być mniejsza od liczby aktywnych wypożyczeń. Usunięcie egzemplarza wymaga potwierdzenia i jest możliwe tylko wtedy, gdy jest dostępny; tytuł znika z katalogu po usunięciu ostatniej kopii.

## Dane

Program wczytuje dane z `biblioteka.txt` przy starcie i zapisuje je po każdej zmianie. Plik przechowuje książki oraz ich historię wypożyczeń. Data zwrotu oznacza, że książka została oddana; `-` oznacza wypożyczenie aktywne. Program nadal pokazuje aktywne wypożyczenia po terminie. Plik jest tekstowy i używa kodowania UTF-8.

## Autor

Szymon Rybikowski
