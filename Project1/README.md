# README - DAS: Rozproszony System Uśredniający

## 1. Wstęp
DAS (Distributed Averaging System) to aplikacja implementująca system rozproszony oparty na protokole UDP. Obsługuje ona komunikację w trybie Master-Slave i umożliwia obliczenie średniej z liczb przesyłanych między procesami.

Głównym celem projektu jest:
- Implementacja rozproszonego systemu uśredniającego.
- Wykorzystanie UDP do przesyłania danych i komunikacji między procesami.
- Obsługa komunikacji w trybie Master-Slave.

## 2. Funkcjonalności
### Uruchomienie aplikacji:
```
java DAS <port> <number>
```
**Parametry:**
- `<port>` - Numer portu UDP, na którym odbywa się komunikacja.
- `<number>` - Liczba całkowita, którą proces w trybie Slave wysyła do procesu Master.

### Tryby działania:
1. **Master:**
   - Nasłuchuje na zadanym porcie UDP.
   - Odbiera liczby od procesów Slave.
   - Oblicza średnią przesłanych liczb (włączając liczbę początkową).
   - Rozsyła wynik do wszystkich Slave poprzez broadcast.
   - Kończy działanie po otrzymaniu komunikatu `-1`.
2. **Slave:**
   - Wysyła liczbę do procesu Master.
   - Oczekuje na potwierdzenie (ACK).
   - Obsługuje timeout (2 sekundy).

## 3. Opis protokołu
### Parametry wejściowe:
- **Port** – port komunikacyjny.
- **Liczba** – wartość numeryczna wysyłana przez Slave lub obsługiwana przez Master.

### Proces działania:
1. **Uruchomienie**
   - **Master:** Tworzy gniazdo i rozpoczyna nasłuchiwanie.
   - **Slave:** Wysyła liczbę do Master, oczekuje na ACK.
2. **Komunikacja**
   - **Slave wysyła:**
     - Liczba (`4-bajtowy integer`)
     - `0` (żądanie obliczenia średniej)
     - `-1` (zakończenie pracy)
   - **Master odpowiada:**
     - `1` (ACK)
     - Średnia (wynik obliczeń)
     - `-1` (koniec pracy)
3. **Obsługa wyjątków:**
   - Timeout (brak odpowiedzi od Mastera po 2 sekundach)
   - Błędy sieciowe są logowane

## 4. Instalacja i uruchomienie
### Wymagania:
- **Java JDK 1.8** lub nowsza.

### Kompilacja:
```
javac DAS.java
```
### Uruchomienie:
```
java DAS <port> <number>
```
- Jeśli port jest zajęty, aplikacja uruchamia się jako Slave.

### Testowanie:
- Skrypt `test_application.sh <port> <liczba>` wysyła losowe liczby do Mastera.
- Można uruchomić Mastera i Slave’a na tym samym hoście i obserwować komunikację.

## 5. Problemy i rozwój
### Aktualne ograniczenia:
- Brak zabezpieczeń przed utratą pakietów.
- Ograniczenie do sieci lokalnej (broadcast).
- Master jako pojedynczy punkt awarii.

### Możliwe ulepszenia:
- Retransmisja pakietów.
- Obsługa wielu Masterów.
- Szyfrowanie komunikacji.

---


