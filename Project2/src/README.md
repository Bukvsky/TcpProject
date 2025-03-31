# README - CCS: Rozproszony System Komunikacji Klient-Serwer

## 1. Wstęp
CCS (Concurrent Communication System) to aplikacja implementująca system rozproszony oparty na protokołach TCP i UDP. Obsługuje ona komunikację klient-serwer, wykonywanie podstawowych operacji arytmetycznych oraz analizę metryk działania.

Głównym celem projektu jest:
- Obsługa zapytań od wielu klientów jednocześnie.
- Wykonywanie operacji arytmetycznych przy użyciu wielowątkowości.
- Dynamiczne raportowanie statystyk działania.

## 2. Funkcjonalności
### Uruchomienie serwera:
```
java -jar CCS.jar <port>
```
**Parametry:**
- `<port>` - Numer portu, na którym serwer będzie nasłuchiwał połączeń (TCP i UDP).

### Główne funkcje:
1. **Usługa UDP:**
   - Nasłuchuje na zadanym porcie i odbiera komunikaty od klientów.
   - Obsługuje protokół wykrywania usług („CCS DISCOVER” -> „CCS FOUND”).
2. **Usługa TCP:**
   - Akceptuje wielu klientów jednocześnie (pula wątków).
   - Obsługuje operacje arytmetyczne (`ADD`, `SUB`, `MUL`, `DIV`).
   - Obsługuje błędy (np. niepoprawne komendy, dzielenie przez zero).
3. **Monitorowanie metryk:**
   - Liczba aktywnych klientów.
   - Całkowita liczba operacji.
   - Statystyki operacji (`ADD`, `SUB`, `MUL`, `DIV`).
   - Liczba błędów.
   - Sumaryczny wynik operacji.
   - Raportowanie co 10 sekund.

## 3. Opis protokołów
### Protokół UDP
- Wykorzystywany do wykrywania serwera przez klientów.
- Brak gwarancji dostarczenia wiadomości, ale szybkie działanie.
- Obsługa komunikatów:
  - Klient wysyła `CCS DISCOVER`.
  - Serwer odpowiada `CCS FOUND`.

### Protokół TCP
- Obsługuje niezawodne połączenia między klientem a serwerem.
- Mechanizmy potwierdzeń (ACK) i retransmisji.
- Obsługa wielu klientów jednocześnie w osobnych wątkach.
- Komunikacja:
  - Klient wysyła polecenie np. `ADD 10 5`.
  - Serwer przetwarza i zwraca wynik.
  - Rejestrowane są metryki operacji.

## 4. Implementacja
### Główne klasy:
1. **CCS** - Uruchamia usługi UDP i TCP, zarządza pulą wątków.
2. **Client** - Reprezentuje klienta TCP, przetwarza komendy.
3. **MetricsAnalyzer** - Zarządza metrykami i generuje raporty.

### Obsługa wielowątkowości:
- Wykorzystanie `ExecutorService` do obsługi klientów TCP.
- Osobne wątki dla usługi UDP i raportowania statystyk.

### Raportowanie statystyk:
Generowane co 10 sekund, zawiera:
- Liczbę aktywnych klientów.
- Statystyki operacji (`ADD`, `SUB`, `MUL`, `DIV`).
- Liczbę błędów.
- Suma wyników operacji.

## 5. Instalacja i uruchomienie
### Wymagania:
- **Java JDK 8** lub nowsza.

### Kompilacja:
```
javac CCS.java
```
### Uruchomienie serwera:
```
java -jar CCS.jar <port>
```
### Testowanie:
- Można uruchomić wiele klientów TCP i wysyłać komendy.
- Użyć narzędzia do generowania zapytań UDP (np. `netcat`).

## 6. Problemy i rozwój
### Aktualne ograniczenia:
- Brak uwierzytelniania klientów.
- Brak retransmisji pakietów w UDP.
- Serwer jako pojedynczy punkt awarii.

### Możliwe ulepszenia:
- Dodanie uwierzytelniania klientów.
- Redundancja serwera.
- Kolejkowanie i buforowanie zapytań.
- Szyfrowanie komunikacji.

---

