# Okmányirodai ügyintézés időpontfoglaló alkalmazás
#### A projekt az SZTE TTIK Mobil alkalmazásfejlesztés nevű kurzusára *[IB470G-1]* készül.

___

## Projekt témája: Okmányirodai ügyintézés időpontfoglaló alkalmazás

## Projekt leírása
* A projektmunka egy egyszerű CRUD (Create - Read - Update - Delete) mobilalkalmazást valósít meg.
* A mobilalkalmazásban a felhasználónak lehetősége van regisztrálni és bejelentkezni.
* Bejelentkezés után különböző okmányirodai ügyintézésekre foglalhat időpontot.
* Csak olyan időpontot lehet foglalni, amely még nem telt be.
* A felhasználó törölheti és módosíthatja a foglalt időpontot.

___

## 1. Mérföldkő *(2025. április 13. 23:55) [Leadva]*
### Értékelési szempontok:
* Nincs fordítási- és futtatási hiba
* Firebase autentikáció
  * Bejelentkezés
  * Regisztráció
  * Kijelentkezés
* Helyes beviteli mezők használata
* ConstraintLayout és még egy másik layout típus használata
* Reszponzivitás
* Animáció *(legalább 1)*
* Intentek használata
  * Navigáció az activityk között
  * Minden activity elérhető
* Kezdetleges főoldal
* Igényes GUI létrehozása
___

## 2. Mérföldkő *(2025. május 18. 23:55)*
### Értékelési szempontok:
* Előző mérföldkő szerint, és:
* Adatmodell definiálása:
  * *UserDetails, OfficeDetails, UserViewModel*
* Legalább 4 Activity:
  * *4 activity, 3 fragment*
* Legalább 2 animáció:
  * *Naptár kép, foglalás törlése*
* Lifecycle Hook:
  * *email és jelszó megjegyzése loginról regisztrációra menet*
* Android erőforrás:
  * *értesítés permission*
* 2 rendszerszolgáltatás:
  * *AlarmManager, NotificationManager*
* CRUD:
  * *C: időpont foglalása, regisztráció*
  * *R: időpontok, felhasználói adatok megjelenítése*
  * *U: felhasználó módosítása*
  * *D: időpont törlése*
* 3 komplex lekérdezés:
  * *CheckoutActivity.fetchBookings()*
  * *BookedFragment.fetchUserBookings()*
  * *BookedFragment.deleteBooking())*
___

## A projekt során használt eszközök
* Lenovo Ideapad 520
* Asus Vivobook 17
* Motorola Edge 40 Neo
* Android Studio / Emulator
* Java
* GitHub
* Firebase Cloud / Firestore

___
