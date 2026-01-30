# TravelLog – Mobilny Dziennik Podróży

Aplikacja mobilna na system Android służąca do rejestrowania wycieczek / spacerów / podróży i dokumentowania ich. Projekt zrealizowany w ramach przedmiotu Programowanie Urządzeń Mobilnych.

## 📝 Opis projektu
TravelLog umożliwia użytkownikowi śledzenie trasy wycieczki w czasie rzeczywistym. Aplikacja zbiera dane z sensorów telefonu, aby dostarczyć informacje o przebytym dystansie oraz liczbie kroków. Dodatkowo pozwala na dokumentowanie podróży poprzez wykonywanie zdjęć, które są zapisywane w historii wraz ze statystykami.

## 🚀 Funkcjonalności
* **Rejestracja trasy:** Śledzenie aktywności (Start/Stop).
* **Statystyki:** Wyświetlanie liczby kroków i przebytego dystansu w metrach.
* **Dokumentacja wizualna:** Możliwość wykonania zdjęcia podczas wycieczki.
* **Historia podróży:** Przeglądanie listy zapisanych wycieczek (data, kroki, dystans, miniatura).
* **Podgląd i udostępnianie:** Wyświetlanie zdjęć na pełnym ekranie z możliwością ich udostępnienia (np. e-mail, dysk, komunikatory).
* **Zarządzanie danymi:** Możliwość usuwania wpisów z historii.

## 📱 Wykorzystane sensory i moduły
Aplikacja realizuje wymóg akwizycji danych z minimum 3 źródeł:
1.  **GPS (Location Services):** Wykorzystywany do obliczania przebytego dystansu na podstawie zmian lokalizacji użytkownika.
2.  **Akcelerometr / Krokomierz (Activity Recognition):** Służy do zliczania kroków wykonanych podczas sesji pomiarowej.
3.  **Aparat fotograficzny (Camera):** Umożliwia wykonanie zdjęcia i powiązanie go z konkretnym wpisem w bazie danych.

## 🛠 Technologie
* **Język:** Kotlin
* **Interfejs:** Jetpack Compose (Material 3)
* **Nawigacja:** Navigation Compose (Type-safe routes)
* **Architektura:** MVVM (Model-View-ViewModel) + Repository Pattern
* **Baza danych:** Room Database (SQLite)
* **Asynchroniczność:** Kotlin Coroutines & Flow
* **Uprawnienia:** Accompanist Permissions

## 📸 Zrzuty ekranu

| Ekran Główny | Rejestrowanie Trasy | Podgląd Zdjęcia | 
|:---:|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/11ec7f94-df1d-4b21-88ef-22a66060143b" width="250"> | <img src="https://github.com/user-attachments/assets/b90fe547-d4d0-40f5-a2db-16f817c70a4b" width="250"> | <img src="https://github.com/user-attachments/assets/5b0ae4f1-07e7-4032-8dfe-a70157ea73f8" width="250"> |

## ⚙️ Instrukcja uruchomienia na komputerze
1.  Pobierz repozytorium:
    ```bash
    git clone https://github.com/qombain/TravelLog.git
    ```
2.  Otwórz projekt w **Android Studio**.
3.  Poczekaj na synchronizację projektu z Gradle (Sync Project).
4.  Podłącz urządzenie fizyczne z systemem Android (zalecane ze względu na sensory) lub uruchom emulator.
5.  Uruchom aplikację przyciskiem Run.
6.  **Ważne:** Przy pierwszym uruchomieniu zaakceptuj wymagane uprawnienia (Lokalizacja, Kamera, Aktywność fizyczna), aby aplikacja działała poprawnie.

## 📦 Plik instalacyjny - do uruchomienia na telefonie
Plik `.apk` gotowy do instalacji znajduje się w katalogu głównym repozytorium.
* **Ważne:** Przy pierwszym uruchomieniu zaakceptuj wymagane uprawnienia (Lokalizacja, Kamera, Aktywność fizyczna), aby aplikacja działała poprawnie.

---
Autor: Adam Sowa 122424
