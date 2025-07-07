# 🚻 ToiletFinder – Aplicație pentru Găsirea Toaletelor Publice

Proiect realizat pentru disciplina „Metode de Dezvoltare Software” din cadrul Facultății de Matematică și Informatică, Universitatea din București.

## 👥 Membrii echipei:
•⁠  Judele Roberto  
•⁠  ⁠Miroiu Alexandru  
•⁠  Alexandru Octav-Andrei  

---

## 📱 Descrierea proiectului

ToiletFinder este o aplicație Android dezvoltată în Jetpack Compose care permite localizarea rapidă a toaletelor publice din apropiere. Utilizatorii pot vizualiza harta, detalii, adăuga recenzii și chiar adăuga noi toalete în mod colaborativ.

### Funcționalități cheie:
•⁠  ⁠Harta interactivă cu marker-e pentru toalete.  
•⁠  ⁠Detalii (nume, descriere, rating, accesibilitate).  
•⁠  ⁠Adăugare recenzii și rating-uri.  
•⁠  ⁠Adăugare de toalete de către utilizatori.  
•⁠  ⁠Acces fără autentificare pentru vizualizare.  
•⁠  ⁠Autentificare Firebase pentru funcții colaborative.  
•⁠  ⁠Autofill pentru coordonate la adăugare toaletă.  

---

## 🌐 🔙 Partea de Backend

Backendul aplicației este implementat folosind *Firebase* (Firestore + Auth), cu o arhitectură scalabilă și bine structurată.

### 🔐 Autentificare:
•⁠  ⁠Implementare cu Firebase Authentication.  
•⁠  ⁠Suport pentru login, logout, și înregistrare.  
•⁠  ⁠Login-ul este *opțional*: este necesar doar pentru funcționalități precum „Add Toilet” sau „Report Problem”.  

### 📦 Structura colecțiilor Firebase:
•⁠  ⁠⁠ toilets ⁠: stochează toaletele (cu GeoPoint, rating, accesibilitate, etc.)  
•⁠  ⁠⁠ reviews ⁠: recenzii legate de ID-ul toaletei  
•⁠  ⁠⁠ users ⁠: profiluri de utilizatori (opțional extins)  

### 🔁 Funcționalități gestionate de backend:
•⁠  ⁠Adăugarea și preluarea dinamică a toaletelor.  
•⁠  ⁠Asocierea recenziilor cu utilizatori autentificați.  
•⁠  ⁠Validarea și procesarea coordonatelor pentru plasarea pe hartă.  
•⁠  ⁠Actualizarea ratingului mediu în funcție de recenzii noi.  

### 📂 Cod backend dedicat:
•⁠  ⁠⁠ ToiletRepo.kt ⁠: comunică cu colecția ⁠ toilets ⁠  
•⁠  ⁠⁠ ReviewRepo.kt ⁠: gestionează recenziile pentru o toaletă  
•⁠  ⁠⁠ UserRepo.kt ⁠: interacțiune cu ⁠ FirebaseAuth ⁠ și Firestore pentru profiluri  

---

## 🔧 Tehnologii utilizate

•⁠  ⁠UI Toolkit: Jetpack Compose  
•⁠  ⁠Hărți: Google Maps Compose API  
•⁠  ⁠Limbaj: Kotlin  
•⁠  ⁠Arhitectură: MVVM  
•⁠  ⁠Persistență: Firebase Firestore  
•⁠  ⁠Autentificare: Firebase Auth  
•⁠  ⁠Image loading: Glide  
•⁠  ⁠Testare: JUnit, Compose Testing  

---

## 🧪 Testare automată

### Framework-uri:
•⁠  ⁠⁠ JUnit ⁠ pentru ViewModel și Repository  
•⁠  ⁠⁠ Compose UI Test ⁠ pentru interfața grafică  

### Exemple de teste:
•⁠  ⁠Verificare plasare markere.  
•⁠  ⁠Teste pe butonul de adăugare.  
•⁠  ⁠Testarea formularului de recenzie.  
•⁠  ⁠Stări de încărcare și fallback-uri.  

---

## 📋 Backlog & User Stories

Organizat prin GitHub Projects  
🔗 [Board][(https://github.com/user/proiect-toiletfinder/projects/1)](https://github.com/users/RobertoJudele/projects/2)

### User stories:
1.⁠ ⁠Ca utilizator, vreau să pot vedea toaletele pe hartă în funcție de locația curentă.  
2.⁠ ⁠Ca utilizator, vreau să pot vedea detalii și recenzii despre o toaletă.  
3.⁠ ⁠Ca utilizator, vreau să adaug o recenzie și un rating.  
4.⁠ ⁠Ca utilizator autentificat, vreau să adaug o toaletă.  
5.⁠ ⁠Ca utilizator, vreau ca aplicația să îmi ceară permisiuni de locație clar.  
6.⁠ ⁠Ca dezvoltator, vreau ca funcțiile să fie separate prin MVVM.  
7.⁠ ⁠Ca utilizator, vreau o aplicație rapidă și responsivă.  

---

## 🧠 Design Patterns

•⁠  ⁠MVVM (ViewModel + Repository)  
•⁠  ⁠State hoisting în Compose  
•⁠  ⁠Repository pattern pentru abstractizarea accesului la date  
•⁠  ⁠UIState (Loading / Error / Success)  
•⁠  ⁠Firebase Auth + Role-based rendering  

---

## 🎥 Demo

•⁠  ⁠[Video demo]([https://youtu.be/link-demo](https://youtube.com/shorts/2vCRKlZT6s8?si=KLgG8LSgzsQ0XK9u))  
•⁠  ⁠Include: hartă, adăugare toaletă, recenzie, logout/login  

---

## 🗺️ Diagrame

•⁠  ⁠Diagrama UI flow: Map → Marker → Detalii → Recenzie / Adăugare  
https://drive.google.com/file/d/1xzFUeZqIEd7cJPwuD6NLHq5EU1KBsoGD/view?usp=sharing
---

## 🔀 Source Control & Management

•⁠  ⁠Branch-uri dedicate per funcționalitate  
•⁠  ⁠Commituri explicite  
•⁠  ⁠Pull requests + code reviews  
•⁠  ⁠Issues deschise pentru bug-uri și feedback  

---

## 🤖 Prompt Engineering

Pentru eficiență și calitate, s-au folosit sugestii generate cu ajutorul ChatGPT:  
•⁠  ⁠Scriere ViewModel și testare Compose  
•⁠  ⁠Mock-uri pentru repo-uri  
•⁠  ⁠Generare UI Compose (AddToilet, Sidebar, Login)  
https://docs.google.com/document/d/19a9JoKCVOln5kwAjRgVtgqvCc_IcqNsB/edit?usp=sharing&ouid=110848913067712715946&rtpof=true&sd=true

---

## 📎 Alte detalii

•⁠  ⁠Fully compatible cu Material 3  
•⁠  ⁠Componente modulare   
•⁠  ⁠Design responsiv  
•⁠  ⁠Suport pentru teme Light / Dark  
