import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

// projekt zaliczeniowy: prosty system obsługi biblioteki.
public class Main {

    private static final Path PLIK_DANYCH = Paths.get("biblioteka.txt"); // plik z książkami i wypożyczeniami
    private static final Scanner scanner = new Scanner(System.in); // odczyt danych z klawiatury
    private static final DateTimeFormatter FORMAT_DATY = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // format daty w menu
    private static final List<Book> books = new ArrayList<>(); // książki w katalogu
    private static final List<Loan> loans = new ArrayList<>(); // historia wypożyczeń

    public static void main(String[] args) {
        try {
            loadData(); // odczytanie katalogu i wypożyczeń
        } catch (IOException e) {
            System.out.println("Błąd odczytu danych: " + e.getMessage()); // pokazanie informacji o błędzie pliku
            return;
        }

        try {
            while (true) { // wyświetlanie menu do chwili wyjścia
                System.out.println("Copyright: Szymon Rybikowski, nr indeksu: 128930"); // copyright
                System.out.println();
                System.out.println("===== MENU GŁÓWNE ====="); // główne opcje programu
                System.out.println("1. Lista książek");
                System.out.println("2. Wypożycz książkę");
                System.out.println("3. Książki wypożyczone");
                System.out.println("4. Książki po terminie");
                System.out.println("5. Dodaj, edytuj lub usuń książkę");
                System.out.println("6. Wyjście");

                switch (readInt("Wybierz opcję: ")) { // wykonanie wybranej akcji
                    case 1:
                        showBooks(books); // pokazanie całego katalogu
                        break;
                    case 2:
                        borrowBook(); // wybór i wypożyczenie książki
                        break;
                    case 3:
                        showActiveLoans(); // pokazanie aktywnych wypożyczeń
                        break;
                    case 4:
                        showOverdueLoans(); // pokazanie zaległych zwrotów
                        break;
                    case 5:
                        manageBooks(); // otwarcie podmenu książek
                        break;
                    case 6:
                        saveData(); // zapis danych przed zakończeniem
                        System.out.println("Zamykam... Do widzenia!"); // pożegnanie użytkownika
                        scanner.close(); // zwolnienie skanera wejścia
                        return;
                    default:
                        System.out.println("Błąd!! Nie ma takiej opcji!"); // komunikat dla błędnego wyboru
                }
            }
        } catch (NoSuchElementException e) { // obsługa końca danych wejściowych
            System.out.println("Zamykam... Do widzenia!");
            return;
        }
    }

    private static void manageBooks() { // podmenu dodawania, edycji i usuwania książek
        while (true) { // powtarzanie menu aż do anulowania
            System.out.println();
            System.out.println("--- ZARZĄDZANIE KSIĄŻKAMI ---");
            System.out.println("1. Dodaj książkę");
            System.out.println("2. Edytuj lub usuń książkę z listy");
            System.out.println("3. Anuluj");

            switch (readInt("Wybierz opcję: ")) {
                case 1:
                    addBook(); // uruchomienie dodawania książki
                    break;
                case 2:
                    editOrDeleteBook(); // pokazanie listy do edycji lub usunięcia
                    break;
                case 3:
                    return; // powrót do menu głównego
                default:
                    System.out.println("Błąd!! Nie ma takiej opcji!"); // nieobsługiwany numer
            }
        }
    }

    private static void showBooks(List<Book> list) { // wyświetlenie podanej listy książek
        if (list.isEmpty()) { // obsługa pustej listy
            System.out.println("Brak książek.");
            return;
        }

        System.out.printf("%-4s %-50s %-34s %s%n", "ID", "Tytuł", "Autor", "Dostępne"); // nagłówki kolumn
        for (Book book : list) {
            System.out.printf("%-4d %-50s %-34s %d/%d%n",
                    book.id, shorten(book.title, 50), shorten(book.author, 34),
                    book.available, book.total); // wyświetlenie dostępnych i wszystkich egzemplarzy
        }
    }

    private static void addBook() { // dodanie nowej książki do katalogu
        String title = cleanText(readText("Tytuł: ")); // pobranie tytułu książki
        String author = cleanText(readText("Autor: ")); // pobranie autora
        int copies = readInt("Liczba egzemplarzy: "); // pobranie liczby kopii

        if (copies < 1) {
            System.out.println("Liczba egzemplarzy musi być większa od zera."); // walidacja liczby kopii
            return;
        }
        if (hasDuplicate(title, author, null)) {
            System.out.println("Taka książka już istnieje. Edytuj jej liczbę egzemplarzy."); // uniknięcie duplikatu
            return;
        }

        books.add(new Book(nextBookId(), title, author, copies, copies)); // dodanie książki z nowym numerem
        System.out.println("Dodano książkę.");
        saveData(); // natychmiastowy zapis zmiany
    }

    private static void editOrDeleteBook() { // wybór książki do edycji albo usunięcia
        showBooks(books); // pokazanie katalogu przed wyborem
        if (books.isEmpty()) {
            return; // brak pozycji do edycji lub usunięcia
        }

        Book book = findBook(readInt("Numer książki: ")); // wyszukanie wybranej książki
        if (book == null) {
            System.out.println("Nie znaleziono książki."); // błędny numer książki
            return;
        }

        System.out.println("1. Edytuj książkę"); // zmiana tytułu, autora lub liczby kopii
        System.out.println("2. Usuń jeden egzemplarz"); // usunięcie jednej kopii książki
        System.out.println("3. Anuluj"); // rezygnacja bez zmian
        switch (readInt("Wybierz opcję: ")) {
            case 1:
                editBook(book); // otwarcie formularza edycji
                break;
            case 2:
                deleteBook(book); // próba usunięcia książki
                break;
            case 3:
                System.out.println("Anulowano."); // informacja o anulowaniu
                break;
            default:
                System.out.println("Błąd!! Nie ma takiej opcji!"); // nieobsługiwany numer
        }
    }

    private static void editBook(Book book) { // zmiana danych wybranej książki
        String title = cleanText(readOptionalText("Nowy tytuł (Enter bez zmian): ", book.title)); // aktualizacja tytułu
        String author = cleanText(readOptionalText("Nowy autor (Enter bez zmian): ", book.author)); // aktualizacja autora
        int total = readOptionalInt("Nowa liczba egzemplarzy (Enter bez zmian): ", book.total); // aktualizacja liczby kopii
        int borrowed = book.total - book.available; // liczba egzemplarzy poza biblioteką

        if (total < 1) { // liczba egzemplarzy musi być dodatnia
            System.out.println("Liczba egzemplarzy musi być większa od zera.");
            return;
        }
        if (total < borrowed) { // liczba egzemplarzy nie może spaść poniżej wypożyczonych
            System.out.println("Liczba egzemplarzy nie może być mniejsza od liczby wypożyczonych."); // ochrona aktywnych wypożyczeń
            return;
        }
        if (hasDuplicate(title, author, book)) {
            System.out.println("Taka książka już istnieje."); // uniknięcie duplikatu tytułu i autora
            return;
        }

        book.title = title; // zapis nowego tytułu
        book.author = author; // zapis nowego autora
        book.total = total; // zapis nowej liczby egzemplarzy
        book.available = total - borrowed; // zachowanie prawidłowej liczby dostępnych egzemplarzy
        System.out.println("Zapisano zmiany.");
        saveData(); // natychmiastowy zapis zmiany
    }

    private static void deleteBook(Book book) { // usunięcie jednego dostępnego egzemplarza
        if (book.available < 1) { // nie można usunąć egzemplarza, którego nie ma na półce
            System.out.println("Nie można usunąć egzemplarza, ponieważ wszystkie są wypożyczone.");
            return;
        }
        if (!confirm("Czy na pewno usunąć jeden egzemplarz książki \"" + book.title + "\"?")) {
            System.out.println("Anulowano."); // pozostawienie katalogu bez zmian
            return;
        }

        book.total--; // zmniejszenie liczby posiadanych egzemplarzy
        book.available--; // zmniejszenie liczby dostępnych egzemplarzy
        if (book.total == 0) { // usunięcie tytułu dopiero po ostatniej kopii
            books.remove(book); // usunięcie pustej pozycji z katalogu
            System.out.println("Usunięto ostatni egzemplarz i tytuł z katalogu.");
        } else {
            System.out.println("Usunięto jeden egzemplarz. Pozostało: "
                    + book.available + "/" + book.total + ".");
        }
        saveData(); // natychmiastowy zapis zmiany
    }

    private static void borrowBook() { // wypożyczenie wybranej dostępnej książki
        List<Book> available = new ArrayList<>(); // przygotowanie listy książek na półce
        for (Book book : books) {
            if (book.available > 0) {
                available.add(book);
            }
        }

        showBooks(available); // pokazanie tylko pozycji dostępnych
        if (available.isEmpty()) {
            return;
        }

        Book book = findBook(readInt("Numer książki do wypożyczenia: ")); // pobranie wybranej pozycji
        if (book == null || book.available == 0) {
            System.out.println("Ta książka nie jest dostępna."); // obsługa błędnego lub zajętego ID
            return;
        }

        Loan loan = new Loan(nextLoanId(), book.id, LocalDate.now(),
                LocalDate.now().plusDays(14), null); // zapis historii bez danych czytelnika
        loans.add(loan); // dodanie wypożyczenia do historii
        book.available--; // zmniejszenie liczby dostępnych egzemplarzy
        System.out.println("Wypożyczono \"" + book.title + "\". Termin zwrotu: "
                + FORMAT_DATY.format(loan.dueDate) + ".");
        saveData(); // natychmiastowy zapis wypożyczenia
    }

    private static void showActiveLoans() { // pokazanie wypożyczeń i terminu zwrotu
        List<Loan> active = new ArrayList<>(); // zebranie niezwróconych książek
        for (Loan loan : loans) { // sprawdzenie całej historii
            if (loan.returnDate == null) {
                active.add(loan); // zachowanie tylko niezwróconych książek
            }
        }
        if (active.isEmpty()) { // komunikat, gdy brak wypożyczeń
            System.out.println("Brak wypożyczonych książek.");
            return;
        }

        LocalDate today = LocalDate.now(); // data potrzebna do obliczenia czasu
        for (Loan loan : active) {
            long days = ChronoUnit.DAYS.between(today, loan.dueDate); // liczba dni do terminu lub po terminie
            String remaining; // tekst wyświetlany obok tytułu
            if (days > 0) {
                remaining = days == 1 ? "pozostał 1 dzień" : "pozostało " + days + " dni";
            } else if (days == 0) {
                remaining = "termin zwrotu mija dzisiaj";
            } else {
                long overdueDays = -days;
                remaining = overdueDays == 1 ? "1 dzień po terminie" : overdueDays + " dni po terminie";
            }
            System.out.println("ID " + loan.id + ": " + bookTitle(loan.bookId)
                    + " - zwrot do " + FORMAT_DATY.format(loan.dueDate) + " (" + remaining + ")"); // dane wypożyczenia
        }

        int id = readInt("Numer wypożyczenia do zwrotu (0 - pomiń): "); // wybór wypożyczenia do zwrotu
        if (id != 0) {
            returnBook(id); // wykonanie zwrotu wybranej książki
        }
    }

    private static void showOverdueLoans() { // pokazanie książek po terminie
        LocalDate today = LocalDate.now(); // pobranie dzisiejszej daty
        boolean found = false; // informacja, czy znaleziono zaległe pozycje
        for (Loan loan : loans) {
            if (loan.returnDate == null && loan.dueDate.isBefore(today)) {
                long days = ChronoUnit.DAYS.between(loan.dueDate, today); // liczba dni opóźnienia
                String overdueTime = days == 1 ? "1 dzień" : days + " dni";
                System.out.println("ID " + loan.id + ": " + bookTitle(loan.bookId)
                        + " - termin zwrotu: " + FORMAT_DATY.format(loan.dueDate)
                        + ", po terminie " + overdueTime);
                found = true; // oznaczenie, że istnieje zaległy zwrot
            }
        }
        if (!found) {
            System.out.println("Brak książek po terminie."); // komunikat, gdy nie ma zaległości
        }
    }

    private static void returnBook(int loanId) { // zwrot wypożyczonej książki
        Loan loan = findLoan(loanId); // wyszukanie wypożyczenia po numerze
        if (loan == null || loan.returnDate != null) {
            System.out.println("Nie znaleziono aktywnego wypożyczenia."); // ID nie istnieje lub książka jest już zwrócona
            return;
        }

        Book book = findBook(loan.bookId); // wyszukanie książki do aktualizacji dostępności
        if (book == null) {
            System.out.println("Nie znaleziono książki przypisanej do wypożyczenia."); // komunikat o niespójnych danych
            return;
        }

        loan.returnDate = LocalDate.now(); // zapis daty zwrotu
        book.available++; // przywrócenie egzemplarza do dostępnych
        System.out.println("Zwrócono książkę.");
        saveData(); // natychmiastowy zapis zwrotu
    }

    private static void loadData() throws IOException { // wczytanie danych z pliku tekstowego
        if (!Files.exists(PLIK_DANYCH)) { // brak pliku oznacza pusty katalog
            return;
        }

        List<String> lines = Files.readAllLines(PLIK_DANYCH, StandardCharsets.UTF_8); // odczyt pliku w UTF-8
        for (int i = 0; i < lines.size(); i++) { // przetwarzanie wierszy pliku
            String line = lines.get(i).trim(); // pobranie bieżącego wiersza
            if (line.isEmpty() || line.startsWith("#")) { // pominięcie pustych wierszy i komentarzy
                continue;
            }

            String[] fields = line.split(";", -1); // podział rekordu na pola
            try {
                switch (fields[0]) { // rozpoznanie rodzaju rekordu
                    case "KSIAZKA": // wczytanie książki
                        requireFields(fields, 6);
                        Book book = new Book(Integer.parseInt(fields[1]), fields[2], fields[3],
                                Integer.parseInt(fields[4]), Integer.parseInt(fields[5]));
                        if (book.id < 1 || book.title.isEmpty() || book.author.isEmpty()
                                || book.total < 1 || book.available < 0 || book.available > book.total) {
                            throw new IllegalArgumentException();
                        }
                        books.add(book);
                        break;
                    case "CZYTELNIK": // pominięcie danych czytelnika ze starszego formatu
                        break;
                    case "WYPOZYCZENIE": // wczytanie terminu i stanu wypożyczenia
                        Loan loan;
                        if (fields.length == 8) { // odczyt starszego rekordu z numerem czytelnika
                            loan = new Loan(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]),
                                    LocalDate.parse(fields[4]), LocalDate.parse(fields[5]),
                                    fields[6].equals("-") ? null : LocalDate.parse(fields[6]));
                        } else { // odczyt uproszczonego rekordu bez czytelnika
                            requireFields(fields, 6);
                            loan = new Loan(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]),
                                    LocalDate.parse(fields[3]), LocalDate.parse(fields[4]),
                                    fields[5].equals("-") ? null : LocalDate.parse(fields[5]));
                        }
                        loans.add(loan);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } catch (RuntimeException e) { // zgłoszenie uszkodzonego rekordu z numerem linii
                throw new IOException("uszkodzona linia " + (i + 1) + " w pliku " + PLIK_DANYCH);
            }
        }
    }

    private static void saveData() { // zapis książek i wypożyczeń w formacie tekstowym
        List<String> lines = new ArrayList<>(); // rekordy do zapisania
        lines.add("# Dane biblioteki"); // nagłówek pliku
        for (Book book : books) { // dodanie wszystkich książek
            lines.add(book.toFileLine()); // zamiana książki na wiersz tekstu
        }
        for (Loan loan : loans) { // dodanie historii wypożyczeń
            lines.add(loan.toFileLine()); // zamiana wypożyczenia na wiersz tekstu
        }

        try {
            Files.write(PLIK_DANYCH, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); // zapis bieżących danych
            System.out.println("Zapisano dane do pliku " + PLIK_DANYCH + ".");
        } catch (IOException e) {
            System.out.println("Błąd zapisu danych: " + e.getMessage());
        }
    }

    private static boolean hasDuplicate(String title, String author, Book except) { // sprawdzenie duplikatu tytułu i autora
        for (Book book : books) { // porównanie z pozycjami w katalogu
            if (book != except && book.title.equalsIgnoreCase(title)
                    && book.author.equalsIgnoreCase(author)) {
                return true;
            }
        }
        return false;
    }

    private static Book findBook(int id) { // wyszukanie książki po numerze
        for (Book book : books) { // sprawdzenie każdej pozycji
            if (book.id == id) {
                return book;
            }
        }
        return null;
    }

    private static Loan findLoan(int id) { // wyszukanie wypożyczenia po numerze
        for (Loan loan : loans) { // sprawdzenie historii
            if (loan.id == id) {
                return loan;
            }
        }
        return null;
    }

    private static String bookTitle(int id) { // pobranie tytułu książki z wypożyczenia
        Book book = findBook(id); // wyszukanie w bieżącym katalogu
        return book == null ? "(usunięta książka nr " + id + ")" : book.title;
    }

    private static int nextBookId() { // wyznaczenie kolejnego numeru książki
        int max = 0; // dotychczas największy numer
        for (Book book : books) {
            max = Math.max(max, book.id);
        }
        for (Loan loan : loans) {
            max = Math.max(max, loan.bookId);
        }
        return max + 1;
    }

    private static int nextLoanId() { // wyznaczenie kolejnego numeru wypożyczenia
        int max = 0; // dotychczas największy numer
        for (Loan loan : loans) {
            max = Math.max(max, loan.id);
        }
        return max + 1;
    }

    private static void requireFields(String[] fields, int count) { // sprawdzenie liczby pól rekordu
        if (fields.length != count) {
            throw new IllegalArgumentException();
        }
    }

    private static String cleanText(String text) { // zamiana separatora, aby tekst nie uszkodził formatu pliku
        return text.trim().replace(';', ',');
    }

    private static String shorten(String text, int maxLength) { // skrócenie długiego tekstu w tabeli
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }

    private static String readText(String prompt) { // pobranie niepustego tekstu
        while (true) { // pytanie ponawiane przy pustym wpisie
            System.out.print(prompt); // wyświetlenie pytania
            String text = scanner.nextLine().trim(); // pobranie odpowiedzi
            if (!text.isEmpty()) { // sprawdzenie, czy odpowiedź zawiera tekst
                return text; // zwrócenie odpowiedzi
            }
            System.out.println("Pole nie może być puste.");
        }
    }

    private static String readOptionalText(String prompt, String current) { // pobranie tekstu albo pozostawienie starej wartości
        System.out.print(prompt); // wyświetlenie pytania
        String text = scanner.nextLine().trim(); // pobranie odpowiedzi
        return text.isEmpty() ? current : text; // Enter oznacza brak zmian
    }

    private static int readInt(String prompt) { // pobranie liczby całkowitej
        while (true) { // ponawianie pytania do poprawnego wpisu
            System.out.print(prompt); // wyświetlenie pytania
            try {
                return Integer.parseInt(scanner.nextLine().trim()); // zamiana tekstu na liczbę
            } catch (NumberFormatException e) {
                System.out.println("Wpisz poprawną liczbę całkowitą.");
            }
        }
    }

    private static int readOptionalInt(String prompt, int current) { // pobranie liczby albo pozostawienie starej
        while (true) { // ponawianie pytania do poprawnego wpisu
            System.out.print(prompt); // wyświetlenie pytania
            String text = scanner.nextLine().trim(); // pobranie odpowiedzi
            if (text.isEmpty()) { // Enter oznacza brak zmian
                return current; // pozostawienie obecnej wartości
            }
            try {
                return Integer.parseInt(text); // zamiana tekstu na liczbę
            } catch (NumberFormatException e) {
                System.out.println("Wpisz poprawną liczbę całkowitą.");
            }
        }
    }

    private static boolean confirm(String prompt) { // potwierdzenie usunięcia książki
        System.out.print(prompt + " (t/n): "); // wyświetlenie pytania
        return scanner.nextLine().trim().equalsIgnoreCase("t"); // tylko odpowiedź t potwierdza
    }

    private static final class Book {
        private final int id; // niezmienny numer książki
        private String title; // tytuł
        private String author; // autor
        private int total; // łączna liczba egzemplarzy
        private int available; // liczba egzemplarzy dostępnych

        private Book(int id, String title, String author, int total, int available) { // utworzenie pozycji katalogu
            this.id = id;
            this.title = title;
            this.author = author;
            this.total = total;
            this.available = available;
        }

        private String toFileLine() { // konwersja książki do formatu pliku
            return "KSIAZKA;" + id + ";" + cleanText(title) + ";" + cleanText(author)
                    + ";" + total + ";" + available;
        }
    }

    private static final class Loan {
        private final int id; // numer wypożyczenia
        private final int bookId; // numer wypożyczonej książki
        private final LocalDate borrowDate; // data wypożyczenia
        private final LocalDate dueDate; // termin zwrotu
        private LocalDate returnDate; // data zwrotu albo null, gdy książka jest nadal wypożyczona

        private Loan(int id, int bookId, LocalDate borrowDate, // utworzenie rekordu wypożyczenia bez danych czytelnika
                     LocalDate dueDate, LocalDate returnDate) {
            this.id = id;
            this.bookId = bookId;
            this.borrowDate = borrowDate;
            this.dueDate = dueDate;
            this.returnDate = returnDate;
        }

        private String toFileLine() { // konwersja wypożyczenia do formatu pliku
            String returned = returnDate == null ? "-" : returnDate.toString(); // myślnik oznacza aktywne wypożyczenie
            return "WYPOZYCZENIE;" + id + ";" + bookId + ";"
                    + borrowDate + ";" + dueDate + ";" + returned;
        }
    }
}
// copyright Szymon Rybikowski 128930
